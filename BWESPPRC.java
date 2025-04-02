package FirstTry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
public class BWESPPRC {
    Parameter userParam;
    ArrayList<BwLabel> BwLabels;

    class BwLabel{
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
        public double PhiB;
        public double PhiA;

        BwLabel(int a1, int a2, int a3,  double a5, double a6,double a7,double a8,double a9,int a10,boolean a11, boolean[] a12, double a13, double a14){
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
            PhiB = a13;
            PhiA = a14;
        }
    }

    class myLabelComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            BwLabel A = BwLabels.get(a);
            BwLabel B = BwLabels.get(b);
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
                    else if(A.PhiB - B.PhiB < -1e-7)
                        return -1;
                    else if(A.PhiB - B.PhiB >1e-7)
                        return 1;
                    else if(A.PhiA-B.PhiA < -1e-7)
                        return -1;
                    else if(A.PhiA-B.PhiA > 1e-7)
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

    public void BwshortestPathSpeedOpt(Parameter userParamArg, ArrayList<Route> routes, int nbRoute,double mu, double[] pi){
        BwLabel current;
        int i,j,idx,nbSol,maxSol;
        double d,d2,PhiB = 0,PhiA = 0;
        int[] checkDom;
        double tt,tt2, mustEnergy,tempEnergy,mustEnergy2,tempEnergy2 = 0,aPower = 0,bPower = 0,rTime = 0;
        Integer currentidx;

        int labelNum = 0;
        this.userParam = userParamArg;
        TreeSet<Integer> BwU = new TreeSet<Integer>(new myLabelComparator());
        TreeSet<Integer> BwP = new TreeSet<Integer>(new myLabelComparator());
        BwLabels = new ArrayList<BwLabel>(2*userParam.nbClients);
        boolean[] cust = new boolean[userParam.nbClients+2];
        cust[userParam.nbClients+1] = true;
        for (i  = 0; i < userParam.nbClients+1; i++) {
            cust[i] = false;
        }
        BwLabels.add(new BwLabel(userParam.nbClients+1,0,-1,0,0,0,0,0,0,false,cust,0,0));
        BwU.add(0);
        checkDom = new int[userParam.nbClients+2];
        ArrayList<Integer>[] citySpeed2labels = new ArrayList[userParam.nbClients+2];
        for (i = 0; i < userParam.nbClients+2; i++) {
            citySpeed2labels[i] = new ArrayList<Integer>();
            checkDom[i] = 0;
        }
        citySpeed2labels[userParam.nbClients+1].add(0);
        nbSol = 0;
        maxSol = 2*nbRoute;
        while ((BwU.size()>0) ) {

            currentidx = BwU.pollFirst();
            current = BwLabels.get(currentidx);
            int l1, l2;
            boolean pathSpeedDom;
            BwLabel la1, la2;
            ArrayList<Integer> cleaning = new ArrayList<Integer>();

            if(current.city == 0){

                for (i = checkDom[current.city]; i < citySpeed2labels[current.city].size(); i++) {
                    for (j = 0; j < i; j++) {
                        l1 = citySpeed2labels[current.city].get(i);
                        l2 = citySpeed2labels[current.city].get(j);
                        la1 = BwLabels.get(l1);
                        la2 = BwLabels.get(l2);
                        if (!(la1.dominated || la2.dominated)) {
                            pathSpeedDom = true;
                            for (int k = 0; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la1.cost <= la2.cost) ) {
                                BwLabels.get(l2).dominated = true;

                                BwU.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            for (int k = 0; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la2.cost <= la1.cost) ) {
                                BwLabels.get(l1).dominated = true;
                                BwU.remove(l1);
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
                        la1 = BwLabels.get(l1);
                        la2 = BwLabels.get(l2);
                        if (!(la1.dominated || la2.dominated)) {
                            pathSpeedDom = true;
                            for (int k = 0; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la1.cost <= la2.cost) && (la1.passed <= la2.passed) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower) && (la1.aPower <= la2.aPower) && (la1.PhiB <= la2.PhiB) && (la1.PhiA<=la2.PhiA)) {
                                BwLabels.get(l2).dominated = true;

                                BwU.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            for (int k = 0; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la2.cost <= la1.cost) && (la2.demand <= la1.demand) && (la2.passed <= la1.passed) && (la2.bPower <= la1.bPower) && (la2.aPower <= la1.aPower)&& (la2.PhiB <= la1.PhiB) && (la2.PhiA<=la1.PhiA)) {
                                BwLabels.get(l1).dominated = true;
                                BwU.remove(l1);
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
                if(current.city == 0){
                    current.cost = current.cost-mu;
                    if(current.cost<-1e-7){
                        BwP.add(currentidx);
                        nbSol = 0;
                        for(Integer labi: BwP){
                            BwLabel s = BwLabels.get(labi);
                            if(!s.dominated){
                                nbSol++;
                            }
                        }
                    }
                }else{
                    for (i =userParam.nbClients+1; i > -1;i--) {
                        if((!current.vertexVisited[i]) && (userParam.dist[i][current.city] < userParam.verybig - 1e-6)){

                            //如果已经过充电站
                            if(current.passed>0){
                                boolean isStation = false;
                                //判断下一点是否为充电站
                                for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                    if(i == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站则可以去
                                if(!isStation){
                                    d = current.demand + userParam.demand[i];
                                    PhiB = current.PhiB+userParam.Phi[i][current.city];

                                    if(d<=userParam.capacity){
                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            tempEnergy = userParam.Phi[i][current.city]*userParam.demand[i]+userParam.beta[i][current.city] *(userParam.speed[h]*userParam.speed[h])
                                                    +userParam.gamma[i][current.city] /userParam.speed[h]+userParam.epsilon[i][current.city]
                                                    +current.PhiB*userParam.demand[i];
                                            aPower = current.aPower+current.PhiA*userParam.demand[i];
                                            bPower = current.bPower+tempEnergy;
                                            if((bPower<=userParam.battery) && (aPower<= userParam.battery)){
                                                idx = BwLabels.size();
                                                boolean[] newCust = new boolean[userParam.nbClients+2];
                                                System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                newCust[i] = true;
                                                PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();

                                                double leftPower = userParam.battery-bPower;
                                                double needPower = 0;
                                                if(aPower+ bPower>userParam.battery){
                                                    needPower = aPower+bPower-userParam.battery;
                                                }
                                                rTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                                double deltaTime = rTime - current.rechargeTime;
                                                double cost = current.cost+deltaTime+userParam.travelTime[h][i][current.city] -pi[i];
                                                BwLabels.add(new BwLabel(i,h,currentidx,cost,d,bPower,aPower,rTime,current.passed,false,newCust,PhiB,current.PhiA));
                                                labelNum++;
                                                if (!BwU.add((Integer) idx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    BwLabels.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                } else {
                                                    citySpeed2labels[i].add(idx);
                                                }
                                            }
                                        }
                                    }
                                }
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
                                    PhiB = current.PhiB+userParam.Phi[i][current.city];
                                    if(d<=userParam.capacity){
                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            tempEnergy = userParam.Phi[i][current.city] * userParam.demand[i]+userParam.beta[i][current.city] *(userParam.speed[h]*userParam.speed[h])
                                                    +userParam.gamma[i][current.city] /userParam.speed[h]+userParam.epsilon[i][current.city]
                                                    +current.PhiB*userParam.demand[i];
                                            bPower = tempEnergy+current.bPower;
                                            if(bPower<=userParam.battery){
                                                idx = BwLabels.size();
                                                boolean[] newCust = new boolean[userParam.nbClients+2];
                                                System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                newCust[i] = true;

                                                double cost = current.cost+userParam.travelTime[h][i][current.city]-pi[i];
                                                BwLabels.add(new BwLabel(i,h,currentidx,cost,d,bPower,current.aPower,0,current.passed,false,newCust,PhiB,current.PhiA));
                                                labelNum++;
                                                if (!BwU.add((Integer) idx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    BwLabels.get(idx).dominated = true; // => we can forget this label and keep only the other one
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
                                    PhiB = current.PhiB+userParam.Phi[i][current.city];
                                    for (int h = 0; h < userParam.speed.length; h++) {

                                        tempEnergy = userParam.Phi[i][current.city] * userParam.demand[i]+userParam.beta[i][current.city] *(userParam.speed[h]*userParam.speed[h]) +userParam.gamma[i][current.city]/userParam.speed[h]+userParam.epsilon[i][current.city];

                                        bPower = tempEnergy+current.bPower;

                                        if(bPower<=userParam.battery){

                                            idx = BwLabels.size();
                                            boolean[] newCust = new boolean[userParam.nbClients+2];
                                            System.arraycopy(current.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                            newCust[i] = true;
                                            double cost = current.cost+userParam.travelTime[h][i][current.city] -pi[i]+userParam.waitingTime[i]+userParam.varTheta;
                                            BwLabels.add(new BwLabel(i,h,currentidx,cost,d,0,bPower,0,(current.passed+1),false,newCust,0,PhiB));
                                            labelNum++;
                                            if (!BwU.add((Integer) idx)) {
                                                BwLabels.get(idx).dominated = true; // => we can forget this label and keep only the other one
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
        while ((i<nbRoute) && ((lab = BwP.pollFirst())!=null)){
            BwLabel s = BwLabels.get(lab);
            if(!s.dominated){
                if(s.cost<-1e-4){
                    Route newRoute = new Route();
                    newRoute.setCost(s.cost);

                    System.out.println("bPower: "+s.bPower);
                    System.out.println("aPower: "+s.aPower);
                    newRoute.addCity(s.city);
                    newRoute.setRechargeTime(s.rechargeTime);
                    newRoute.addSpeed(s.lSpeed);
                    int path = s.indexPrevLabel;
                    while (path>=0){
                        newRoute.addCity(BwLabels.get(path).city);
                        newRoute.addSpeed(BwLabels.get(path).lSpeed);
                        path = BwLabels.get(path).indexPrevLabel;
                    }
                    newRoute.switchPath();
                    newRoute.switchSpeedVec();

                    routes.add(newRoute);
                    i++;
                }
            }
        }
        System.out.println("labelnum:"+labelNum);
    }
}
