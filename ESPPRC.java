package FirstTry;
import java.util.*;

public class ESPPRC {
    Parameter userParam;
    ArrayList<label> labels;

    class label{
        public int city;
        public int lSpeed;
        public int indexPrevLabel;
        //public double indePrevSpeed;
        public double cost;
        public double demand;
        public double bPower;
        public double aPower;
        public double rechargeTime;
        public int passed;
        public boolean dominated;
        public boolean[] vertexVisited;
        public double totalTime;

        label(int a1, int a2, int a3,  double a5, double a6,double a7,double a8,double a9,int a10,boolean a11, boolean[] a12,double a13){
            city = a1;
            lSpeed = a2;
            indexPrevLabel = a3;
            //indePrevSpeed = a4;
            cost = a5;
            demand =a6;
            bPower = a7;
            aPower = a8;
            rechargeTime = a9;
            passed = a10;
            dominated = a11;
            vertexVisited = a12;
            totalTime = a13;
        }
    }

/*class myLabelComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            label A = labels.get(a);
            label B = labels.get(b);
            if(A.cost-B.cost < -1e-7)
                return -1;
            else if(A.cost-B.cost>1e-7)
                return 1;
            else {
                if(A.city == B.city){
                    if(A.demand-B.demand<-1e-7)
                        return -1;
                    else if (A.demand-B.demand>1e-7)
                        return 1;
                    else if(A.totalTime -B.totalTime <-1e-7)
                        return -1;
                    else if(A.totalTime -B.totalTime >1e-7)
                        return 1;
                    else if(A.passed != B.passed)
                    {
                        if(A.passed >0)
                            return 1;
                        else
                            return -1;
                    }
                    else if(A.bPower-B.bPower<-1e-7)
                        return -1;
                    else if(A.bPower-B.bPower>1e-7)
                        return 1;
                    else if(A.aPower-B.aPower<-1e-7)
                        return -1;
                    else if(A.aPower-B.aPower>1e-7)
                        return 1;
                    else {
                        int i = 0;
                        while (i < userParam.nbClients + 2) {
                            if (A.vertexVisited[i] != B.vertexVisited[i]) {
                                if (A.vertexVisited[i])
                                    return -1;
                                else
                                    return 1;
                            }
                            i++;
                        }
                        return 0;
                    }
                }else if(A.city> B.city)
                    return 1;
                else
                    return -1;
            }
        }
    }*/

    class MyLabelComparator implements Comparator<Integer> {
        public int compare(Integer a, Integer b) {
            label labelA = labels.get(a);
            label labelB = labels.get(b);
            if (labelA == null && labelB == null) {
                return 0; // 如果两个元素都是 null，则认为它们相等
            }
            if (labelA == null) {
                return 1; //
            }
            if (labelB  == null) {
                return -1; // 如果只有 b 是 null，将 b 视为较小的元素
            }
            // First, compare by cost
            int costComparison = Double.compare(labelA.cost, labelB.cost);
            if (costComparison != 0) return costComparison;
// Then, compare by demand
            int demandComparison = Double.compare(labelA.demand, labelB.demand);
            if (labelA.passed != labelB.passed) return Integer.compare(labelA.passed, labelB.passed);
            if (demandComparison != 0) return demandComparison;
            int bPowerComparison = Double.compare(labelA.bPower, labelB.bPower);
            if (bPowerComparison != 0) return bPowerComparison;

            int aPowerComparison = Double.compare(labelA.aPower, labelB.aPower);
            if (aPowerComparison != 0) return aPowerComparison;
            // Compare by total time
            int totalTimeComparison = Double.compare(labelA.totalTime, labelB.totalTime);
            if (totalTimeComparison != 0) return totalTimeComparison;

            // Next, compare by city index
            if (labelA.city != labelB.city) return Integer.compare(labelA.city, labelB.city);



            // Compare by number of passed charging stations

            // Compare by battery power before and after


            // Finally, compare by vertex visited (in case all other comparisons are equal)
            for (int i = 0; i < userParam.nbClients + 2; i++) {
                if (labelA.vertexVisited[i] != labelB.vertexVisited[i]) {
                    return labelA.vertexVisited[i] ? -1 : 1;
                }
            }

            // If all else is equal, consider them equal
            return 0;
        }
    }

    public void shortestPathSpeedOpt(Parameter userParamArg, ArrayList<Route> routes, int nbRoute,double mu, double[] pi,double preObj,HashSet<Route> set,double minAvgP){

        label current;
        int i,j,idx,nbSol,maxSol;
        double d,d2;
        int[] checkDom;
        double tempEnergy,mustEnergy2,tempEnergy2,aPower,bPower,rTime;
        Integer currentidx;

        int labelNum = 0;
        this.userParam = userParamArg;
        TreeSet<Integer> U = new TreeSet<Integer>(new MyLabelComparator());
        TreeSet<Integer> P = new TreeSet<Integer>(new MyLabelComparator());

        labels = new ArrayList<label>(2*userParam.nbClients);
        boolean[] cust = new boolean[userParam.nbClients+2];
        cust[0] = true;
        for (i  = 1; i < userParam.nbClients+2; i++) {
            cust[i] = false;
        }
        labels.add(new label(0,0,-1,0,0,0,0,0,0,false,cust,0));
        U.add(0);
        checkDom = new int[userParam.nbClients+2];
        ArrayList<Integer>[] citySpeed2labels = new ArrayList[userParam.nbClients+2];
        for (i = 0; i < userParam.nbClients+2; i++) {
            citySpeed2labels[i] = new ArrayList<Integer>();
            checkDom[i] = 0;
        }
        citySpeed2labels[0].add(0);
        //int dom = 0;
        while ((U.size()>0) ) {
            long now = System.currentTimeMillis();
            if ( (now-userParam.startTime)>= userParam.TimeLimit){
                break;
            }

            //System.out.println("Labelnum: "+labelNum+"; dom: "+dom);
            currentidx = U.pollFirst();

            current = labels.get(currentidx);
            if(current==null){
                continue;
            }

            int l1, l2;
            boolean pathSpeedDom;
            label la1, la2;
            ArrayList<Integer> cleaning = new ArrayList<Integer>();



            if(current.city == userParam.nbClients+1){
                /*for (i = checkDom[current.city]; i < citySpeed2labels[current.city].size(); i++) {
                    for (j = 0; j < i; j++) {
                        l1 = citySpeed2labels[current.city].get(i);
                        l2 = citySpeed2labels[current.city].get(j);
                        la1 = labels.get(l1);
                        la2 = labels.get(l2);
                        if (!(la1==null || la2==null)) {
                            pathSpeedDom = true;
                            *//*for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }*//*
                            if (pathSpeedDom && (la1.cost <= la2.cost-1e-3) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower-1e-3) && (la1.aPower <= la2.aPower-1e-3) ) {

                                //labels.get(l2).dominated = true;
                                labels.set(l2, null);
                                //dom++;
                                U.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            *//*for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }*//*
                            if (pathSpeedDom && (la2.cost <= la1.cost-1e-3) && (la2.demand <= la1.demand) && (la2.bPower <= la1.bPower-1e-3) && (la2.aPower <= la1.aPower-1e-3) ) {

                                //labels.get(l1).dominated = true;
                                labels.set(l1, null);
                                //dom++;
                                U.remove(l1);
                                cleaning.add(l1);
                                j = citySpeed2labels[current.city].size();
                            }
                        }
                    }
                }*/
            }else {
                for (i = checkDom[current.city]; i < citySpeed2labels[current.city].size(); i++) {
                    for (j = 0; j < i; j++) {
                        l1 = citySpeed2labels[current.city].get(i);
                        l2 = citySpeed2labels[current.city].get(j);
                        la1 = labels.get(l1);
                        la2 = labels.get(l2);
                        if (!(la1==null || la2==null)) {

                            if(la1.passed ==0 && la2.passed==0){
                                pathSpeedDom = true;
                                for (int k = 0; pathSpeedDom && (k < userParam.Clients.length); k++) {
                                    pathSpeedDom = (!la1.vertexVisited[userParam.Clients[k]] || la2.vertexVisited[userParam.Clients[k]]);
                                }
                                if (pathSpeedDom && (la1.cost <= la2.cost-1e-1) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower-1e-1) /*&& (la1.totalTime <=la2.totalTime*0.99)*/) {
                                    //labels.get(l2).dominated = true;
                                    labels.set(l2, null);
                                    //dom++;
                                    U.remove((Integer) l2);
                                    cleaning.add(l2);
                                    pathSpeedDom = false;
                                }
                                pathSpeedDom = true;
                                for (int k = 0; pathSpeedDom && (k < userParam.Clients.length); k++) {
                                    pathSpeedDom = (!la2.vertexVisited[userParam.Clients[k]] || la1.vertexVisited[userParam.Clients[k]]);
                                }
                                if (pathSpeedDom && (la2.cost <= la1.cost-1e-1) && (la2.demand <= la1.demand) && (la2.bPower <= la1.bPower-1e-1)  /*&&(la2.totalTime <=la1.totalTime*0.99)*/) {

                                    //labels.get(l1).dominated = true;
                                    labels.set(l1, null);
                                    //dom++;
                                    U.remove(l1);
                                    cleaning.add(l1);
                                    j = citySpeed2labels[current.city].size();
                                }
                            }
                            else if(la1.passed>0 && la2.passed==0){
                                pathSpeedDom = true;
                                for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                    pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                                }
                                if (pathSpeedDom && (la2.cost <= la1.cost-1e-1) && (la2.demand <= la1.demand) && (la2.bPower <= la1.aPower-1e-1) ) {

                                    //labels.get(l1).dominated = true;
                                    labels.set(l1, null);
                                    //dom++;
                                    U.remove(l1);
                                    cleaning.add(l1);
                                    j = citySpeed2labels[current.city].size();
                                }
                            }else if(la1.passed==0 && la2.passed>0){
                                pathSpeedDom = true;
                                for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                    pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                                }
                                if (pathSpeedDom && (la1.cost <= la2.cost-1e-1) && (la1.demand <= la2.demand) && (la1.bPower <= la2.aPower-1e-1) ) {
                                    //labels.get(l2).dominated = true;
                                    labels.set(l2, null);
                                    //dom++;
                                    U.remove((Integer) l2);
                                    cleaning.add(l2);
                                    pathSpeedDom = false;
                                }
                            }else if(la1.passed >0 && la2.passed>0){
                                pathSpeedDom = true;
                                for (int k = 0; pathSpeedDom && (k < userParam.Clients.length); k++) {
                                    pathSpeedDom = (!la1.vertexVisited[userParam.Clients[k]] || la2.vertexVisited[userParam.Clients[k]]);
                                }
                                if (pathSpeedDom && (la1.cost <= la2.cost-1e-1) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower-1e-1) && (la1.aPower <= la2.aPower-1e-1) /*&& (la1.totalTime <=la2.totalTime*0.99)*/) {
                                    //labels.get(l2).dominated = true;
                                    labels.set(l2, null);
                                    //dom++;
                                    U.remove((Integer) l2);
                                    cleaning.add(l2);
                                    pathSpeedDom = false;
                                }
                                pathSpeedDom = true;
                                for (int k = 0; pathSpeedDom && (k < userParam.Clients.length); k++) {
                                    pathSpeedDom = (!la2.vertexVisited[userParam.Clients[k]] || la1.vertexVisited[userParam.Clients[k]]);
                                }
                                if (pathSpeedDom && (la2.cost <= la1.cost-1e-1) && (la2.demand <= la1.demand) && (la2.bPower <= la1.bPower-1e-1) && (la2.aPower <= la1.aPower-1e-1)  /*&&(la2.totalTime <=la1.totalTime*0.99)*/) {

                                    //labels.get(l1).dominated = true;
                                    labels.set(l1, null);
                                    //dom++;
                                    U.remove(l1);
                                    cleaning.add(l1);
                                    j = citySpeed2labels[current.city].size();
                                }
                            }

                        }
                    }
                }
            }

            for (Integer c : cleaning)
                citySpeed2labels[current.city].remove((Integer) c);

            cleaning = null;
            checkDom[current.city] = citySpeed2labels[current.city].size();
            if(current==null){
                continue;
            }
            if(current.city == userParam.nbClients+1){
                current.cost = current.cost-mu;
                if(current.cost<0){
                    P.add(currentidx);
                    //nbSol = 0;
                }//
            }else if(current.cost - (userParam.capacity-current.demand)*minAvgP-mu+userParam.travelTime[userParam.speed.length-1][current.city][userParam.nbClients+1]<1e-3) {
                boolean[] newCust = new boolean[userParam.nbClients+2];
                newCust[0] = true;

                for (i = 1; i < userParam.nbClients+2;i ++) {
                    System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                    //if((!current.vertexVisited[i]) ){
                    if((!current.vertexVisited[i]) ){

                        //如果已经过充电站
                        if(current.passed>0 ){
                            if(!userParam.isRecharge[i])
                            {
                                d = current.demand + userParam.demand[i];
                                if(d<=userParam.capacity){
                                    for (int h = 0; h < userParam.speed.length; h++) {
                                        if(userParam.travelTime[h][current.city][i] < userParam.verybig - 10){
                                            tempEnergy = userParam.Phi[current.city][i] * current.demand + userParam.beta[current.city][i]*(userParam.speed[h]*userParam.speed[h]) + userParam.gamma[current.city][i] / userParam.speed[h] + userParam.epsilon[current.city][i];

                                            if(tempEnergy+current.aPower<=userParam.battery+1e-3){
                                                idx = labels.size();
                                                newCust[i] = true;
                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust[j]){
                                                        d2 = d+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust[j] = true;
                                                        }else{
                                                        tempEnergy2 = userParam.Phi[i][j] * d + userParam.beta[i][j] * (userParam.speed[0]*userParam.speed[0]) + userParam.gamma[i][j]/userParam.speed[0] + userParam.epsilon[i][j];
                                                        if(tempEnergy2+tempEnergy+current.aPower>userParam.battery+1e-1){
                                                        newCust[j] = true;
                                                            }
                                                        }
                                                    }
                                                }
                                                PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                aPower = current.aPower+tempEnergy;
                                                double leftPower = userParam.battery-current.bPower;
                                                double needPower = 0;
                                                if(aPower+ current.bPower>userParam.battery){
                                                    needPower = aPower+current.bPower-userParam.battery;
                                                }
                                                rTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                                //System.out.println("rTime: "+rTime);
                                                double deltaTime = rTime - current.rechargeTime;
                                                double cost = current.cost+deltaTime+userParam.travelTime[h][current.city][i]-pi[i];
                                                labels.add(new label(i,h,currentidx,cost,d,current.bPower,aPower,rTime,current.passed,false,newCust.clone(),current.totalTime +userParam.travelTime[h][current.city][i]+deltaTime));
                                                labelNum++;
                                                if (!U.add((Integer) idx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    labels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                    labels.set(idx,null);
                                                } else {
                                                    citySpeed2labels[i].add(idx);
                                                }
                                            }
                                        }
                                    }
                                }
                            }


                        }else{
                            //如果未经过充电站

                            //如果不是充电站
                            if(!userParam.isRecharge[i]){
                                d = current.demand + userParam.demand[i];
                                if(d<=userParam.capacity){

                                    for (int h = 0; h < userParam.speed.length; h++) {
                                        if(userParam.travelTime[h][current.city][i] < userParam.verybig - 10){
                                            tempEnergy = userParam.Phi[current.city][i] * current.demand + userParam.beta[current.city][i] * (userParam.speed[h]*userParam.speed[h]) + userParam.gamma[current.city][i]/userParam.speed[h] + userParam.epsilon[current.city][i];

                                            if(tempEnergy+current.bPower<=userParam.battery+1e-3){
                                                idx = labels.size();
                                                //boolean[] newCust = new boolean[userParam.nbClients+2];
                                                //System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                newCust[i] = true;
                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust[j]){
                                                        d2 = d+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust[j] = true;
                                                        }
                                                    }
                                                }
                                                //PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                aPower = current.aPower;
                                                if(current.aPower>0){
                                                    System.out.println("its impossible!");
                                                }

                                                bPower = current.bPower+tempEnergy;

                                                double cost = current.cost+userParam.travelTime[h][current.city][i]-pi[i];
                                                labels.add(new label(i,h,currentidx,cost,d,bPower,aPower,0,current.passed,false,newCust.clone(),current.totalTime +userParam.travelTime[h][current.city][i]));
                                                labelNum++;
                                                if (!U.add((Integer) idx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    labels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                    labels.set(idx,null);
                                                } else {
                                                    citySpeed2labels[i].add(idx);
                                                }
                                            }
                                        }
                                    }
                                }
                            }else {
                                //如果是充电站

                                d = current.demand;
                                for (int h = 0; h < userParam.speed.length; h++) {
                                    if(userParam.travelTime[h][current.city][i] < userParam.verybig - 10){
                                        tempEnergy = userParam.Phi[current.city][i] * current.demand + userParam.beta[current.city][i] * (userParam.speed[h]*userParam.speed[h]) + userParam.gamma[current.city][i]/userParam.speed[h] + userParam.epsilon[current.city][i];
                                        if(tempEnergy+current.bPower<=userParam.battery+1e-3){
                                            idx = labels.size();
                                            //boolean[] newCust = new boolean[userParam.nbClients+2];
                                            //System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                            newCust[i] = true;
                                            for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                                newCust[userParam.rechargeStations[k]] =true;
                                            }
                                            for (j = 1; j < userParam.nbClients+1; j++) {
                                                if(!newCust[j]){
                                                    d2 = d+userParam.demand[j];
                                                    if(d2>userParam.capacity){
                                                        newCust[j] = true;
                                                    }else{
                                                        tempEnergy2 = userParam.Phi[i][j] * d + userParam.beta[i][j] * (userParam.speed[0]*userParam.speed[0])+ (userParam.gamma[i][j]/userParam.speed[0])+userParam.epsilon[i][j];
                                                        if(tempEnergy2+current.aPower>userParam.battery+1e-3){
                                                        newCust[j] = true;
                                                        }

                                                    }
                                                }
                                            }
                                            aPower = current.aPower;
                                            bPower = current.bPower+tempEnergy;
                                            double cost = current.cost+userParam.travelTime[h][current.city][i]-pi[i]+userParam.waitingTime[i]+userParam.varTheta;
                                            labels.add(new label(i,h,currentidx,cost,d,bPower,aPower,current.rechargeTime,(i),false,newCust.clone(),current.totalTime +userParam.travelTime[h][current.city][i]+userParam.waitingTime[i]+userParam.varTheta));
                                            labelNum++;
                                            if (!U.add((Integer) idx)) {
                                                labels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                labels.set(idx,null);
                                            } else {
                                                citySpeed2labels[i].add(idx);
                                            }
                                        }
                                    }

                                }
                            }

                        }
                    }
                }
            }
        }
        checkDom = null;
        Integer lab;
        i = 0;
        int rrn= routes.size();
        int rnum = 0;
        while (!P.isEmpty()){
            lab = P.pollFirst();
            label s = labels.get(lab);
            if(s!=null){
                if(s.cost<-1e-3){

                    Route newRoute = new Route();
                    newRoute.setCost(s.cost);
                    //System.out.println("cost:"+s.cost);
                    //System.out.println("demand: "+s.demand);


                    newRoute.addCity(s.city);
                    newRoute.setRechargeTime(s.rechargeTime);

                    newRoute.addSpeed(s.lSpeed);
                    int path = s.indexPrevLabel;
                    boolean rightpath = true;
                    label pre;
                    while (path>=0 ){
                        pre= labels.get(path);
                        if(pre==null ){
                            rightpath =false;
                            break;
                        }
                        //System.out.println(path);
                        newRoute.path.add(0,pre.city);
                        newRoute.speedVec.add(0,pre.lSpeed);
                        path = pre.indexPrevLabel;

                    }
                    if(rightpath && (rnum-200) <Math.floor(2000/(routes.size()+1))){
                        boolean added = false;
                        newRoute.Station = s.passed;
                        /*System.out.println("newRoute: "+newRoute.getPath());
                        System.out.println("speed: "+newRoute.getSpeedVec());
                        System.out.println("cost: "+newRoute.cost);
                        System.out.println(newRoute.rechargeTime);
                        System.out.println(newRoute.Station);
                        System.out.println("bPower: "+s.bPower);
                        System.out.println("aPower: "+s.aPower);
                        System.out.println("RechargeTime: "+s.rechargeTime);*/
                        addUnique(routes,set,newRoute);
                        rnum++;
                        /*if(added){
                            i++;
                            System.out.println(i);
                        }*/
                    }
                }
            }
        }
        /*double change = 0;
        double lastfirst=0;
        if(routes.size()>0){
            lastfirst=routes.get(0).cost;
        }

        while ( ((lab = P.pollFirst())!=null)){

            label s = labels.get(lab);
            if(s!=null){
                if(s.cost<-1e-3){
                    Route newRoute = new Route();
                    newRoute.setCost(s.cost);
                    //System.out.println("cost:"+s.cost);
                    //System.out.println("demand: "+s.demand);
                    //System.out.println("bPower: "+s.bPower);
                    //System.out.println("aPower: "+s.aPower);

                    newRoute.addCity(s.city);
                    newRoute.setRechargeTime(s.rechargeTime);
                    newRoute.addSpeed(s.lSpeed);
                    int path = s.indexPrevLabel;
                    boolean rightpath = true;
                    label pre;
                    while (path>=0 ){
                        pre= labels.get(path);
                        if(pre==null){
                            rightpath =false;
                            break;
                        }
                        //System.out.println(path);
                        newRoute.path.add(0,pre.city);
                        newRoute.speedVec.add(0,pre.lSpeed);
                        path = pre.indexPrevLabel;

                    }
                    if(rightpath&&(routes.size()-rrn<5)){
                        //System.out.println("new: "+newRoute.cost+" last: "+0.6*lastfirst);
                        lastfirst = newRoute.cost;
                        //newRoute.switchPath();
                        //newRoute.switchSpeedVec();
                        newRoute.Station = s.passed;
                        //boolean added = false;

                        //System.out.println("newRoute: "+newRoute.getPath());
                        //System.out.println("speed: "+newRoute.getSpeedVec());
                        //System.out.println(newRoute.rechargeTime);
                        addUnique(routes,set,newRoute);
                        *//*if(added){
                            i++;
                            System.out.println(i);
                        }*//*

                    }
                }
            }
        }*/

        //System.out.println("labelnum:"+labelNum);
    }

    public void  Update(Route route,Parameter userParam){
        route.updatebPowerNo(userParam);
        //System.out.println(route.bPower);
        route.updateCost(userParam);
    }
    private static void addUnique(ArrayList<Route> list, HashSet<Route> set, Route r) {
        if (!set.contains(r)) {
            set.add(r);
            list.add(r);

        }
    }
}
/*package FirstTry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
public class ESPPRC {
    Parameter userParam;
    ArrayList<label> labels;

    class label{
        public int city;
        public int lSpeed;
        public int indexPrevLabel;
        //public double indePrevSpeed;
        public double cost;
        public double demand;
        public double bPower;
        public double aPower;
        public double rechargeTime;
        public int passed;
        public boolean dominated;
        public boolean[] vertexVisited;
        public double totalTime;

        label(int a1, int a2, int a3,  double a5, double a6,double a7,double a8,double a9,int a10,boolean a11, boolean[] a12,double a13){
            city = a1;
            lSpeed = a2;
            indexPrevLabel = a3;
            //indePrevSpeed = a4;
            cost = a5;
            demand =a6;
            bPower = a7;
            aPower = a8;
            rechargeTime = a9;
            passed = a10;
            dominated = a11;
            vertexVisited = a12;
            totalTime = a13;
        }
    }

    class myLabelComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            label A = labels.get(a);
            label B = labels.get(b);
            if(A.cost-B.cost < -1e-1)
                return -1;
            else if(A.cost-B.cost>1e-1)
                return 1;
            else {
                if(A.city == B.city){
                    if(A.totalTime -B.totalTime <-1e-1)
                        return -1;
                    else if(A.totalTime -B.totalTime >1e-1)
                        return 1;
                    else if(A.demand-B.demand<-1e-7)
                        return -1;
                    else if (A.demand-B.demand>1e-7)
                        return 1;

                    else if(A.passed != B.passed)
                    {
                        if(A.passed >0)
                            return 1;
                        else
                            return -1;
                    }
                    else if(A.bPower-B.bPower<-1e-1)
                        return -1;
                    else if(A.bPower-B.bPower>1e-1)
                        return 1;
                    else if(A.aPower-B.aPower<-1e-1)
                        return -1;
                    else if(A.aPower-B.aPower>1e-1)
                        return 1;
                    else {
                        int i = 0;
                        while (i < userParam.nbClients + 2) {
                            if (A.vertexVisited[i] != B.vertexVisited[i]) {
                                if (A.vertexVisited[i])
                                    return -1;
                                else
                                    return 1;
                            }
                            i++;
                        }
                        return 0;
                    }
                }else if(A.city> B.city)
                    return 1;
                else
                    return -1;
            }
        }
    }

    public void shortestPathSpeedOpt(Parameter userParamArg, ArrayList<Route> routes, int nbRoute,double mu, double[] pi){
        label current;
        int i,j,idx,nbSol,maxSol;
        double d,d2;
        int[] checkDom;
        double tempEnergy,mustEnergy2,tempEnergy2,aPower,bPower,rTime;
        Integer currentidx;

        int labelNum = 0;
        this.userParam = userParamArg;
        TreeSet<Integer> U = new TreeSet<Integer>(new myLabelComparator());
        TreeSet<Integer> P = new TreeSet<Integer>(new myLabelComparator());
        labels = new ArrayList<label>(2*userParam.nbClients);
        boolean[] cust = new boolean[userParam.nbClients+2];
        cust[0] = true;
        for (i  = 1; i < userParam.nbClients+2; i++) {
            cust[i] = false;
        }
        labels.add(new label(0,0,-1,0,0,0,0,0,0,false,cust,0));
        U.add(0);
        checkDom = new int[userParam.nbClients+2];
        ArrayList<Integer>[] citySpeed2labels = new ArrayList[userParam.nbClients+2];
        for (i = 0; i < userParam.nbClients+2; i++) {
            citySpeed2labels[i] = new ArrayList<Integer>();
            checkDom[i] = 0;
        }
        citySpeed2labels[0].add(0);
        int dom = 0;
        while ((U.size()>0) ) {
            //System.out.println("Labelnum: "+labelNum+"; dom: "+dom);
            currentidx = U.pollFirst();
            current = labels.get(currentidx);
            int l1, l2;
            boolean pathSpeedDom;
            label la1, la2;
            ArrayList<Integer> cleaning = new ArrayList<Integer>();



            if(current.city == userParam.nbClients+1){
                for (i = checkDom[current.city]; i < citySpeed2labels[current.city].size(); i++) {
                    for (j = 0; j < i; j++) {
                        l1 = citySpeed2labels[current.city].get(i);
                        l2 = citySpeed2labels[current.city].get(j);
                        la1 = labels.get(l1);
                        la2 = labels.get(l2);
                        if (!(la1.dominated || la2.dominated)) {
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la1.cost <= la2.cost-(1e-1)) ) {
                                labels.get(l2).dominated = true;

                                U.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la2.cost <= la1.cost-(1e-1)) ) {
                                labels.get(l1).dominated = true;
                                U.remove(l1);
                                cleaning.add(l1);
                                j = citySpeed2labels[current.city].size();
                            }
                        }
                    }
                }
            }else {
                for (i = checkDom[current.city]; i < citySpeed2labels[current.city].size(); i++) {
                    for (j = 0; j < i; j++) {
                        l1 = citySpeed2labels[current.city].get(i);
                        l2 = citySpeed2labels[current.city].get(j);
                        la1 = labels.get(l1);
                        la2 = labels.get(l2);
                        if (!(la1.dominated || la2.dominated)) {
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la1.cost <= la2.cost-(1e-1)) && (la1.passed <= la2.passed) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower-(1e-1)) && (la1.aPower <= la2.aPower-(1e-1)) && (la1.totalTime <=la2.totalTime-(1e-1))) {

                                labels.get(l2).dominated = true;
                                dom++;
                                U.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la2.cost <= la1.cost-(1e-1)) && (la2.demand <= la1.demand) && (la2.passed <= la1.passed) && (la2.bPower <= la1.bPower-(1e-1)) && (la2.aPower <= la1.aPower-(1e-1)) &&(la2.totalTime <=la1.totalTime-(1e-1))) {

                                labels.get(l1).dominated = true;
                                dom++;
                                U.remove(l1);
                                cleaning.add(l1);
                                j = citySpeed2labels[current.city].size();
                            }
                        }
                    }
                }
            }

            for (Integer c : cleaning)
                citySpeed2labels[current.city].remove((Integer) c);

            cleaning = null;
            checkDom[current.city] = citySpeed2labels[current.city].size();

            if(!current.dominated){
                if(current.city == userParam.nbClients+1){
                    current.cost = current.cost-mu;
                    if(current.cost<-1e-7){
                        P.add(currentidx);
                        nbSol = 0;
                        for(Integer labi:P){
                            label s = labels.get(labi);
                            if(!s.dominated){
                                nbSol++;
                            }
                        }
                    }
                }else{
                    for (i = 1; i < userParam.nbClients+2;i ++) {


                        //if((!current.vertexVisited[i]) ){
                        if((!current.vertexVisited[i]) && (userParam.dist[current.city][i] < userParam.verybig - 1e-6)){

                            //如果已经过充电站
                            if(current.passed>0){
                                d = current.demand + userParam.demand[i];
                                if(d<=userParam.capacity){
                                    for (int h = 0; h < userParam.speed.length; h++) {
                                        tempEnergy = userParam.Phi[current.city][i] * current.demand + userParam.beta[current.city][i]*(userParam.speed[h]*userParam.speed[h]) + userParam.gamma[current.city][i] / userParam.speed[h] + userParam.epsilon[current.city][i];

                                        if(tempEnergy+current.aPower<=userParam.battery){
                                            idx = labels.size();
                                            boolean[] newCust = new boolean[userParam.nbClients+2];
                                            System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                            newCust[i] = true;
                                            for (j = 1; j < userParam.nbClients+1; j++) {
                                                if(!newCust[j]){
                                                    d2 = d+userParam.demand[j];
                                                    if(d2>userParam.capacity){
                                                        newCust[j] = true;
                                                    }else{
                                                        tempEnergy2 = userParam.Phi[i][j] * d + userParam.beta[i][j] * (userParam.speed[0]*userParam.speed[0]) + userParam.gamma[i][j]/userParam.speed[0] + userParam.epsilon[i][j];
                                                        if(tempEnergy2+tempEnergy+current.aPower>userParam.battery){
                                                            newCust[j] = true;
                                                        }
                                                    }
                                                }
                                            }
                                            PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                            aPower = current.aPower+tempEnergy;
                                            double leftPower = userParam.battery-current.bPower;
                                            double needPower = 0;
                                            if(aPower+ current.bPower>userParam.battery){
                                                needPower = aPower+current.bPower-userParam.battery;
                                            }
                                            rTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                            double deltaTime = rTime - current.rechargeTime;
                                            double cost = current.cost+deltaTime+userParam.travelTime[h][current.city][i]-pi[i];
                                            labels.add(new label(i,h,currentidx,cost,d,current.bPower,aPower,rTime,current.passed,false,newCust,current.totalTime +userParam.travelTime[h][current.city][i]));
                                            labelNum++;
                                            if (!U.add((Integer) idx)) {
                                                // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                // It can happen with some paths where the order of the cities is permuted
                                                labels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                            } else {
                                                citySpeed2labels[i].add(idx);
                                            }
                                        }
                                    }
                                }
                                *//*boolean isStation = false;
                                //判断下一点是否为充电站
                                for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                    if(i == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站则可以去
                                if(!isStation){

                                }*//*
                            }else{
                                //如果未经过充电站
                                boolean isStation = false;
                                //判断下一点是否为充电站
                                for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                    if(i == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站
                                if(!isStation){

                                    d = current.demand + userParam.demand[i];
                                    if(d<=userParam.capacity){

                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            tempEnergy = userParam.Phi[current.city][i] * current.demand + userParam.beta[current.city][i] * (userParam.speed[h]*userParam.speed[h]) + userParam.gamma[current.city][i]/userParam.speed[h] + userParam.epsilon[current.city][i];

                                            if(tempEnergy+current.bPower<=userParam.battery){
                                                idx = labels.size();
                                                boolean[] newCust = new boolean[userParam.nbClients+2];
                                                System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                newCust[i] = true;
                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust[j]){
                                                        d2 = d+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust[j] = true;
                                                        }
                                                    }
                                                }
                                                //PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                aPower = current.aPower;
                                                *//*if(current.aPower>0){
                                                    System.out.println("its impossible!");
                                                }*//*
                                                bPower = current.bPower+tempEnergy;

                                                double cost = current.cost+userParam.travelTime[h][current.city][i]-pi[i];
                                                labels.add(new label(i,h,currentidx,cost,d,bPower,aPower,0,current.passed,false,newCust,current.totalTime +userParam.travelTime[h][current.city][i]));
                                                labelNum++;
                                                if (!U.add((Integer) idx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    labels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                } else {
                                                    citySpeed2labels[i].add(idx);
                                                }
                                            }
                                        }
                                    }
                                }else {
                                    //如果是充电站
                                    //看到这里了！！！！！！！！！！！！！！！！！！！！！！！！！
                                    //System.out.println("station");
                                    d = current.demand;

                                    for (int h = 0; h < userParam.speed.length; h++) {
                                        tempEnergy = userParam.Phi[current.city][i] * current.demand + userParam.beta[current.city][i] * (userParam.speed[h]*userParam.speed[h]) + userParam.gamma[current.city][i]/userParam.speed[h] + userParam.epsilon[current.city][i];
                                        if(tempEnergy+current.bPower<=userParam.battery){
                                            idx = labels.size();
                                            boolean[] newCust = new boolean[userParam.nbClients+2];
                                            System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                            newCust[i] = true;
                                            for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                                newCust[userParam.rechargeStations[k]] =true;
                                            }
                                            for (j = 1; j < userParam.nbClients+1; j++) {
                                                if(!newCust[j]){
                                                    d2 = d+userParam.demand[j];
                                                    if(d2>userParam.capacity){
                                                        newCust[j] = true;
                                                    }else{
                                                        tempEnergy2 = userParam.Phi[i][j] * d + userParam.beta[i][j] * (userParam.speed[0]*userParam.speed[0])+ (userParam.gamma[i][j]/userParam.speed[0])+userParam.epsilon[i][j];
                                                        if(tempEnergy2+current.aPower>userParam.battery){
                                                            newCust[j] = true;
                                                        }

                                                    }
                                                }
                                            }
                                            aPower = current.aPower;
                                            bPower = current.bPower+tempEnergy;
                                            double cost = current.cost+userParam.travelTime[h][current.city][i]-pi[i]+userParam.waitingTime[i]+userParam.varTheta;
                                            labels.add(new label(i,h,currentidx,cost,d,bPower,aPower,0,(current.passed+1),false,newCust,current.totalTime +userParam.travelTime[h][current.city][i]));
                                            labelNum++;
                                            if (!U.add((Integer) idx)) {
                                                labels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                            } else {
                                                citySpeed2labels[i].add(idx);
                                            }
                                        }
                                    }
                                }

                            }
                        }
                    }

                }
            }

        }
        checkDom = null;
        Integer lab;
        i = 0;
        while ((i<nbRoute) && ((lab = P.pollFirst())!=null)){
            label s = labels.get(lab);
            if(!s.dominated){
                if(s.cost<-1e-4){
                    Route newRoute = new Route();
                    newRoute.setCost(s.cost);
                    //System.out.println("cost:"+s.cost);
                    //System.out.println("demand: "+s.demand);
                    //System.out.println("bPower: "+s.bPower);
                    //System.out.println("aPower: "+s.aPower);

                    newRoute.addCity(s.city);
                    newRoute.setRechargeTime(s.rechargeTime);
                    newRoute.addSpeed(s.lSpeed);
                    int path = s.indexPrevLabel;
                    while (path>=0){
                        newRoute.addCity(labels.get(path).city);
                        newRoute.addSpeed(labels.get(path).lSpeed);
                        path = labels.get(path).indexPrevLabel;
                    }
                    newRoute.switchPath();
                    newRoute.switchSpeedVec();
                    //System.out.println("newRoute: "+newRoute.getPath());
                    //System.out.println("speed: "+newRoute.getSpeedVec());
                    routes.add(newRoute);
                    i++;
                }
            }
        }
        //System.out.println("labelnum:"+labelNum);
    }
}*/
