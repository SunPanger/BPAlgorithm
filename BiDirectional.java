package FirstTry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.TreeSet;
public class BiDirectional {

    Parameter userParam;
    ArrayList<fwLabel> fwLabels;
    ArrayList<bwLabel> bwLabels;
    ArrayList<pair> pairs;
    class bwLabel{
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
        public boolean[] explored;

        bwLabel(int a1, int a2, int a3,  double a5, double a6,double a7,double a8,double a9,int a10,boolean a11, boolean[] a12, double a13, double a14, boolean [] a15){
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
            explored = a15;
        }
    }


    class fwLabel{
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
        public boolean[] explored;

        fwLabel(int a1, int a2, int a3,  double a5, double a6,double a7,double a8,double a9,int a10,boolean a11, boolean[] a12, boolean[] a13){
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
            explored = a13;
        }
    }

    class pair{
        public int fwIndex;
        public int bwIndex;
        public double Diff;
        public double cost;
        public boolean dominated;
        double reChargeTime;
        public boolean[] vertexVisited;

        pair(int a1, int a2,double a3, double a4,boolean a5 ,double a6,boolean[] a7){
            fwIndex = a1;
            bwIndex = a2;
            Diff = a3;
            cost = a4;
            dominated = a5;
            reChargeTime = a6;
            vertexVisited = a7;
        }
    }


    class mybwLabelComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            bwLabel A = bwLabels.get(a);
            bwLabel B = bwLabels.get(b);
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
                            if (A.explored[i] != B.explored[i]) {
                                if (A.explored[i])
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

    class myfwLabelComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            fwLabel A = fwLabels.get(a);
            fwLabel B = fwLabels.get(b);
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
                    else {
                        int i = 0;
                        while (i < userParam.nbClients + 2) {
                            if (A.explored[i] != B.explored[i]) {
                                if (A.explored[i])
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

    class mypairComparator implements Comparator<Integer>{
        public int compare(Integer a, Integer b){
            pair A = pairs.get(a);
            pair B = pairs.get(b);
            if(A.cost-B.cost < -1e-7)
                return -1;
            else if(A.cost-B.cost>1e-7)
                return 1;
            else {
                int i = 1;
                while (i < userParam.nbClients + 1) {
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
        }
    }

    public void BiDirectShortestPathSpeedOpt(Parameter userParamArg, ArrayList<Route> routes, int nbRoute,double mu, double[] pi){
        double maxPi = 0;
        this.userParam = userParamArg;
        TreeSet<Integer> P = new TreeSet<Integer>(new mypairComparator());
        TreeSet<Integer> U = new TreeSet<Integer>(new mypairComparator());
        pair current;
        pairs = new ArrayList<pair>(2*userParam.nbClients);
        boolean[] Pcust = new boolean[userParam.nbClients+2];
        pairs.add(new pair(0,0,0,0,false,0,Pcust));
        U.add(0);
        ArrayList<Integer> idx2pair = new ArrayList<>();
        idx2pair.add(0);
        int pairCheckDom = 0;
        int idx;
        Integer currentidx;

        bwLabel bwcurrent;
        int bwi, bwj, bwidx,j,nbSol,maxSol;
        double bwd,PhiB = 0,PhiA = 0,d2,tempEnergy2,tempEnergy3;
        int[] bwcheckDom;
        double bwtempEnergy, bwaPower = 0, bwbPower = 0,bwrTime = 0;
        Integer bwcurrentidx;
        int bwlabelNum = 0;

        TreeSet<Integer> BwU = new TreeSet<Integer>(new mybwLabelComparator());
        TreeSet<Integer> BwP = new TreeSet<Integer>(new mybwLabelComparator());
        bwLabels = new ArrayList<bwLabel>(2*userParam.nbClients);
        boolean[] bwcust = new boolean[userParam.nbClients+2];
        bwcust[userParam.nbClients+1] = true;
        for (bwi = 0; bwi < userParam.nbClients+1; bwi++) {
            bwcust[bwi] = false;
        }
        bwLabels.add(new bwLabel(userParam.nbClients+1,0,-1,0,0,0,0,0,0,false, bwcust,0,0,bwcust));
        BwU.add(0);
        bwcheckDom = new int[userParam.nbClients+2];
        ArrayList<Integer>[] bwcitySpeed2labels = new ArrayList[userParam.nbClients+2];
        for (bwi = 0; bwi < userParam.nbClients+2; bwi++) {
            bwcitySpeed2labels[bwi] = new ArrayList<Integer>();
            bwcheckDom[bwi] = 0;
        }
        bwcitySpeed2labels[userParam.nbClients+1].add(0);
        nbSol = 0;
        maxSol = 2*nbRoute;
        for (int i = 0; i < userParam.nbClients+2; i++) {
            if(pi[i]>maxPi){
                maxPi = pi[i];
            }
        }
        long start1 = System.currentTimeMillis();
        while ((BwU.size()>0) ) {

            bwcurrentidx = BwU.pollFirst();
            bwcurrent = bwLabels.get(bwcurrentidx);
            int l1, l2;
            boolean pathSpeedDom;
            bwLabel la1, la2;
            ArrayList<Integer> cleaning = new ArrayList<Integer>();

            for (bwi = bwcheckDom[bwcurrent.city]; bwi < bwcitySpeed2labels[bwcurrent.city].size(); bwi++) {
                for (bwj = 0; bwj < bwi; bwj++) {
                    l1 = bwcitySpeed2labels[bwcurrent.city].get(bwi);
                    l2 = bwcitySpeed2labels[bwcurrent.city].get(bwj);
                    la1 = bwLabels.get(l1);
                    la2 = bwLabels.get(l2);
                    if (!(la1.dominated || la2.dominated)) {
                        pathSpeedDom = true;
                        for (int k = 0; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                            pathSpeedDom = (!la1.explored[k] || la2.explored[k]);
                        }
                        if (pathSpeedDom && (la1.cost <= la2.cost) && (la1.passed <= la2.passed) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower) && (la1.aPower <= la2.aPower) && (la1.PhiB <= la2.PhiB) && (la1.PhiA<=la2.PhiA)) {
                            bwLabels.get(l2).dominated = true;

                            BwU.remove((Integer) l2);
                            cleaning.add(l2);
                            pathSpeedDom = false;
                        }
                        pathSpeedDom = true;
                        for (int k = 0; pathSpeedDom && (k < userParam.nbClients + 1); k++) {
                            pathSpeedDom = (!la2.explored[k] || la1.explored[k]);
                        }
                        if (pathSpeedDom && (la2.cost <= la1.cost) && (la2.demand <= la1.demand) && (la2.passed <= la1.passed) && (la2.bPower <= la1.bPower) && (la2.aPower <= la1.aPower)&& (la2.PhiB <= la1.PhiB) && (la2.PhiA<=la1.PhiA)) {
                            bwLabels.get(l1).dominated = true;
                            BwU.remove(l1);
                            cleaning.add(l1);
                            bwj = bwcitySpeed2labels[bwcurrent.city].size();
                        }
                    }
                }
            }
            for (Integer c : cleaning)
                bwcitySpeed2labels[bwcurrent.city].remove((Integer) c);

            cleaning = null;
            bwcheckDom[bwcurrent.city] = bwcitySpeed2labels[bwcurrent.city].size();

            if(!bwcurrent.dominated){
                if(bwcurrent.demand >= userParam.capacity/2){

                    if(bwcurrent.cost<-1e-7){
                        BwP.add(bwcurrentidx);
                        nbSol = 0;
                        for(Integer labi: BwP){
                            bwLabel s = bwLabels.get(labi);
                            if(!s.dominated){
                                nbSol++;
                            }
                        }
                    }
                }else{
                    for (bwi =userParam.nbClients+1; bwi > -1; bwi--) {
                        if((!bwcurrent.explored[bwi]) && (userParam.dist[bwi][bwcurrent.city] < userParam.verybig - 1e-6)){

                            //如果已经过充电站
                            if(bwcurrent.passed>0){
                                boolean isStation = false;
                                //判断下一点是否为充电站
                                for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                    if(bwi == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站则可以去
                                if(!isStation){
                                    bwd = bwcurrent.demand + userParam.demand[bwi];
                                    PhiB = bwcurrent.PhiB+userParam.Phi[bwi][bwcurrent.city];

                                    if(bwd <=userParam.capacity){
                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            bwtempEnergy = userParam.Phi[bwi][bwcurrent.city]*userParam.demand[bwi]+userParam.beta[bwi][bwcurrent.city] *(userParam.speed[h]*userParam.speed[h])
                                                    +userParam.gamma[bwi][bwcurrent.city] /userParam.speed[h]+userParam.epsilon[bwi][bwcurrent.city]
                                                    + bwcurrent.PhiB*userParam.demand[bwi];
                                            bwaPower = bwcurrent.aPower+ bwcurrent.PhiA*userParam.demand[bwi];
                                            bwbPower = bwcurrent.bPower+ bwtempEnergy;
                                            if((bwbPower <=userParam.battery) && (bwaPower<= userParam.battery)){
                                                bwidx = bwLabels.size();
                                                boolean[] newCust1 = new boolean[userParam.nbClients+2];
                                                boolean[] newCust2 = new boolean[userParam.nbClients+2];
                                                System.arraycopy(bwcurrent.vertexVisited, 0, newCust1, 0, userParam.nbClients + 2);
                                                System.arraycopy(bwcurrent.explored, 0, newCust2, 0, userParam.nbClients + 2);
                                                newCust1[bwi] = true;
                                                newCust2[bwi] = true;
                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust2[j]){
                                                        d2 = bwd+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust2[j] = true;
                                                        }else{
                                                            tempEnergy2 = userParam.Phi[j][bwi]*userParam.demand[bwi]+userParam.beta[j][bwi] *(userParam.speed[0]*userParam.speed[0])
                                                                    +userParam.gamma[j][bwi] /userParam.speed[0]+userParam.epsilon[j][bwi]
                                                                    + PhiB*userParam.demand[j];
                                                            tempEnergy3 = bwcurrent.PhiA*userParam.demand[j];
                                                            if((tempEnergy2+bwbPower>userParam.battery) || (tempEnergy3+bwaPower>userParam.battery)){
                                                                newCust2[j] = true;
                                                            }
                                                        }
                                                    }
                                                }
                                                PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();

                                                double leftPower = userParam.battery- bwbPower;
                                                double needPower = 0;
                                                if(bwaPower+ bwbPower >userParam.battery){
                                                    needPower = bwaPower+ bwbPower -userParam.battery;
                                                }
                                                bwrTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                                double deltaTime = bwrTime - bwcurrent.rechargeTime;
                                                double cost = bwcurrent.cost+deltaTime+userParam.travelTime[h][bwi][bwcurrent.city] -pi[bwi];
                                                bwLabels.add(new bwLabel(bwi,h, bwcurrentidx,cost, bwd, bwbPower,bwaPower,bwrTime, bwcurrent.passed,false,newCust1,PhiB, bwcurrent.PhiA,newCust2));
                                                bwlabelNum++;
                                                if (!BwU.add((Integer) bwidx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    bwLabels.get(bwidx).dominated = true; // => we can forget this label and keep only the other one
                                                } else {
                                                    bwcitySpeed2labels[bwi].add(bwidx);
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
                                    if(bwi == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站
                                if(!isStation){

                                    bwd = bwcurrent.demand + userParam.demand[bwi];
                                    PhiB = bwcurrent.PhiB+userParam.Phi[bwi][bwcurrent.city];
                                    if(bwd <=userParam.capacity){
                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            bwtempEnergy = userParam.Phi[bwi][bwcurrent.city] * userParam.demand[bwi]+userParam.beta[bwi][bwcurrent.city] *(userParam.speed[h]*userParam.speed[h])
                                                    +userParam.gamma[bwi][bwcurrent.city] /userParam.speed[h]+userParam.epsilon[bwi][bwcurrent.city]
                                                    + bwcurrent.PhiB*userParam.demand[bwi];
                                            bwbPower = bwtempEnergy + bwcurrent.bPower;
                                            if(bwbPower <=userParam.battery){
                                                bwidx = bwLabels.size();
                                                boolean[] newCust1 = new boolean[userParam.nbClients+2];
                                                boolean[] newCust2 = new boolean[userParam.nbClients+2];
                                                System.arraycopy(bwcurrent.vertexVisited, 0, newCust1, 0, userParam.nbClients + 2);
                                                System.arraycopy(bwcurrent.explored, 0, newCust2, 0, userParam.nbClients + 2);
                                                newCust1[bwi] = true;
                                                newCust2[bwi] = true;
                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust2[j]){
                                                        d2 = bwd+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust2[j] = true;
                                                        }else{
                                                            tempEnergy2 = userParam.Phi[j][bwi] * userParam.demand[j]+userParam.beta[j][bwi] *(userParam.speed[0]*userParam.speed[0])
                                                                    +userParam.gamma[j][bwi] /userParam.speed[0]+userParam.epsilon[j][bwi]
                                                                    + PhiB*userParam.demand[j];
                                                            //mustEnergy2 = userParam.Phi[j][userParam.nbClients+1]*(userParam.capacity-d2)+userParam.beta[j][userParam.nbClients+1]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[j][userParam.nbClients+1]/userParam.speed[0]+userParam.epsilon[j][userParam.nbClients+1];
                                                            if(tempEnergy2+bwbPower>userParam.battery){
                                                                newCust2[j] = true;
                                                            }

                                                        }
                                                    }
                                                }

                                                double cost = bwcurrent.cost+userParam.travelTime[h][bwi][bwcurrent.city]-pi[bwi];
                                                bwLabels.add(new bwLabel(bwi,h, bwcurrentidx,cost, bwd, bwbPower, bwcurrent.aPower,0, bwcurrent.passed,false,newCust1,PhiB, bwcurrent.PhiA,newCust2));
                                                bwlabelNum++;
                                                if (!BwU.add((Integer) bwidx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    bwLabels.get(bwidx).dominated = true; // => we can forget this label and keep only the other one
                                                } else {
                                                    bwcitySpeed2labels[bwi].add(bwidx);
                                                }
                                            }
                                        }
                                    }
                                }else {
                                    //如果是充电站
                                    //看到这里了！！！！！！！！！！！！！！！！！！！！！！！！！
                                    //System.out.println("station");

                                    bwd = bwcurrent.demand;
                                    PhiB = bwcurrent.PhiB+userParam.Phi[bwi][bwcurrent.city];
                                    for (int h = 0; h < userParam.speed.length; h++) {

                                        bwtempEnergy = userParam.Phi[bwi][bwcurrent.city] * userParam.demand[bwi]+userParam.beta[bwi][bwcurrent.city] *(userParam.speed[h]*userParam.speed[h]) +userParam.gamma[bwi][bwcurrent.city]/userParam.speed[h]+userParam.epsilon[bwi][bwcurrent.city];

                                        bwbPower = bwtempEnergy + bwcurrent.bPower;

                                        if(bwbPower <=userParam.battery){

                                            bwidx = bwLabels.size();
                                            boolean[] newCust1 = new boolean[userParam.nbClients+2];
                                            boolean[] newCust2 = new boolean[userParam.nbClients+2];
                                            System.arraycopy(bwcurrent.vertexVisited, 0, newCust1, 0, userParam.nbClients + 2);
                                            System.arraycopy(bwcurrent.explored, 0, newCust2, 0, userParam.nbClients + 2);
                                            newCust1[bwi] = true;
                                            newCust2[bwi] = true;

                                            for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                                newCust2[userParam.rechargeStations[k]] =true;
                                            }
                                            for (j = 1; j < userParam.nbClients+1; j++) {
                                                if(!newCust2[j]){
                                                    d2 = bwd+userParam.demand[j];
                                                    if(d2>userParam.capacity){
                                                        newCust2[j] = true;
                                                    }else{
                                                        tempEnergy2 = userParam.Phi[j][bwi] * userParam.demand[j]+userParam.beta[j][bwi] *(userParam.speed[0]*userParam.speed[0])
                                                                +userParam.gamma[j][bwi] /userParam.speed[0]+userParam.epsilon[j][bwi]
                                                                + PhiB*userParam.demand[j];
                                                        tempEnergy3 = PhiB*userParam.demand[j];
                                                        if((tempEnergy2>userParam.battery) || (tempEnergy3+bwbPower>userParam.battery)){
                                                            newCust2[j] = true;
                                                        }

                                                    }
                                                }
                                            }

                                            double cost = bwcurrent.cost+userParam.travelTime[h][bwi][bwcurrent.city] -pi[bwi]+userParam.waitingTime[bwi]+userParam.varTheta;
                                            bwLabels.add(new bwLabel(bwi,h, bwcurrentidx,cost, bwd,0, bwbPower,0,(bwcurrent.passed+1),false,newCust1,0,PhiB,newCust2));
                                            bwlabelNum++;
                                            if (!BwU.add((Integer) bwidx)) {
                                                bwLabels.get(bwidx).dominated = true; // => we can forget this label and keep only the other one
                                            } else {
                                                bwcitySpeed2labels[bwi].add(bwidx);

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
        long end1 = System.currentTimeMillis();
        System.out.println(String.format("bw Time: %d ms", end1- start1));
        long start2 = System.currentTimeMillis();
        fwLabel fwcurrent;
        int fwi, fwj, fwidx;
        double fwd;
        int[] fwcheckDom;
        double  fwtempEnergy, fwaPower, fwbPower, fwrTime;
        Integer fwcurrentidx;

        int labelNum = 0;

        TreeSet<Integer> fwU = new TreeSet<Integer>(new myfwLabelComparator());
        TreeSet<Integer> fwP = new TreeSet<Integer>(new myfwLabelComparator());
        fwLabels = new ArrayList<fwLabel>(2*userParam.nbClients);
        boolean[] cust = new boolean[userParam.nbClients+2];
        cust[0] = true;
        for (fwi = 1; fwi < userParam.nbClients+2; fwi++) {
            cust[fwi] = false;
        }
        fwLabels.add(new fwLabel(0,0,-1,0,0,0,0,0,0,false,cust,cust));
        fwU.add(0);
        fwcheckDom = new int[userParam.nbClients+2];
        ArrayList<Integer>[] fwcitySpeed2labels = new ArrayList[userParam.nbClients+2];
        for (fwi = 0; fwi < userParam.nbClients+2; fwi++) {
            fwcitySpeed2labels[fwi] = new ArrayList<Integer>();
            fwcheckDom[fwi] = 0;
        }
        fwcitySpeed2labels[0].add(0);
        nbSol = 0;
        maxSol = 2*nbRoute;
        while ((fwU.size()>0) ) {

            fwcurrentidx = fwU.pollFirst();
            fwcurrent = fwLabels.get(fwcurrentidx);
            int l1, l2;
            boolean pathSpeedDom;
            fwLabel la1, la2;
            ArrayList<Integer> cleaning = new ArrayList<Integer>();

            /*if(fwcurrent.city == userParam.nbClients+1){
                for (fwi = fwcheckDom[fwcurrent.city]; fwi < fwcitySpeed2labels[fwcurrent.city].size(); fwi++) {
                    for (fwj = 0; fwj < fwi; fwj++) {
                        l1 = fwcitySpeed2labels[fwcurrent.city].get(fwi);
                        l2 = fwcitySpeed2labels[fwcurrent.city].get(fwj);
                        la1 = fwLabels.get(l1);
                        la2 = fwLabels.get(l2);
                        if (!(la1.dominated || la2.dominated)) {
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la1.cost <= la2.cost) ) {
                                fwLabels.get(l2).dominated = true;

                                fwU.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la2.cost <= la1.cost) ) {
                                fwLabels.get(l1).dominated = true;
                                fwU.remove(l1);
                                cleaning.add(l1);
                                fwj = fwcitySpeed2labels[fwcurrent.city].size();
                            }
                        }
                    }
                }
            }else {
                for (fwi = fwcheckDom[fwcurrent.city]; fwi < fwcitySpeed2labels[fwcurrent.city].size(); fwi++) {
                    for (fwj = 0; fwj < fwi; fwj++) {
                        l1 = fwcitySpeed2labels[fwcurrent.city].get(fwi);
                        l2 = fwcitySpeed2labels[fwcurrent.city].get(fwj);
                        la1 = fwLabels.get(l1);
                        la2 = fwLabels.get(l2);
                        if (!(la1.dominated || la2.dominated)) {
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la1.vertexVisited[k] || la2.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la1.cost <= la2.cost) && (la1.passed <= la2.passed) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower) && (la1.aPower <= la2.aPower)) {
                                fwLabels.get(l2).dominated = true;

                                fwU.remove((Integer) l2);
                                cleaning.add(l2);
                                pathSpeedDom = false;
                            }
                            pathSpeedDom = true;
                            for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                                pathSpeedDom = (!la2.vertexVisited[k] || la1.vertexVisited[k]);
                            }
                            if (pathSpeedDom && (la2.cost <= la1.cost) && (la2.demand <= la1.demand) && (la2.passed <= la1.passed) && (la2.bPower <= la1.bPower) && (la2.aPower <= la1.aPower)) {
                                fwLabels.get(l1).dominated = true;
                                fwU.remove(l1);
                                cleaning.add(l1);
                                fwj = fwcitySpeed2labels[fwcurrent.city].size();
                            }
                        }
                    }
                }
            }*/


            for (fwi = fwcheckDom[fwcurrent.city]; fwi < fwcitySpeed2labels[fwcurrent.city].size(); fwi++) {
                for (fwj = 0; fwj < fwi; fwj++) {
                    l1 = fwcitySpeed2labels[fwcurrent.city].get(fwi);
                    l2 = fwcitySpeed2labels[fwcurrent.city].get(fwj);
                    la1 = fwLabels.get(l1);
                    la2 = fwLabels.get(l2);
                    if (!(la1.dominated || la2.dominated)) {
                        pathSpeedDom = true;
                        for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                            pathSpeedDom = (!la1.explored[k] || la2.explored[k]);
                        }
                        if (pathSpeedDom && (la1.cost <= la2.cost) && (la1.passed <= la2.passed) && (la1.demand <= la2.demand) && (la1.bPower <= la2.bPower) && (la1.aPower <= la2.aPower)) {
                            fwLabels.get(l2).dominated = true;

                            fwU.remove((Integer) l2);
                            cleaning.add(l2);
                            pathSpeedDom = false;
                        }
                        pathSpeedDom = true;
                        for (int k = 1; pathSpeedDom && (k < userParam.nbClients + 2); k++) {
                            pathSpeedDom = (!la2.explored[k] || la1.explored[k]);
                        }
                        if (pathSpeedDom && (la2.cost <= la1.cost) && (la2.demand <= la1.demand) && (la2.passed <= la1.passed) && (la2.bPower <= la1.bPower) && (la2.aPower <= la1.aPower)) {
                            fwLabels.get(l1).dominated = true;
                            fwU.remove(l1);
                            cleaning.add(l1);
                            fwj = fwcitySpeed2labels[fwcurrent.city].size();
                        }
                    }
                }
            }


            for (Integer c : cleaning)
                fwcitySpeed2labels[fwcurrent.city].remove((Integer) c);

            cleaning = null;
            fwcheckDom[fwcurrent.city] = fwcitySpeed2labels[fwcurrent.city].size();

            if(!fwcurrent.dominated){
                if(fwcurrent.demand >= userParam.capacity/2){
                    fwcurrent.cost = fwcurrent.cost-mu;
                    if(fwcurrent.cost<-1e-7){
                        fwP.add(fwcurrentidx);
                        nbSol = 0;
                        for(Integer labi: fwP){
                            fwLabel s = fwLabels.get(labi);
                            if(!s.dominated){
                                nbSol++;
                            }
                        }
                    }
                }else{
                    for (fwi = 0; fwi < userParam.nbClients+2; fwi++) {

                        if((!fwcurrent.explored[fwi]) && (userParam.dist[fwcurrent.city][fwi] < userParam.verybig - 1e-6)){

                            //如果已经过充电站
                            if(fwcurrent.passed>0){
                                boolean isStation = false;
                                //判断下一点是否为充电站
                                for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                    if(fwi == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站则可以去
                                if(!isStation){
                                    fwd = fwcurrent.demand + userParam.demand[fwi];
                                    if(fwd <=userParam.capacity){
                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            fwtempEnergy = userParam.Phi[fwcurrent.city][fwi]* fwcurrent.demand+userParam.beta[fwcurrent.city][fwi]*(userParam.speed[h]*userParam.speed[h])+userParam.gamma[fwcurrent.city][fwi]/userParam.speed[h]+userParam.epsilon[fwcurrent.city][fwi];
                                            if(fwtempEnergy + fwcurrent.aPower<=userParam.battery){
                                                fwidx = fwLabels.size();
                                                boolean[] newCust1 = new boolean[userParam.nbClients+2];
                                                boolean[] newCust2 = new boolean[userParam.nbClients+2];
                                                System.arraycopy(fwcurrent.vertexVisited, 0, newCust1, 0, userParam.nbClients + 2);
                                                System.arraycopy(fwcurrent.explored, 0, newCust2, 0, userParam.nbClients + 2);
                                                newCust1[fwi] = true;
                                                newCust2[fwi] = true;
                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust2[j]){
                                                        d2 = fwd+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust2[j] = true;
                                                        }else{
                                                            tempEnergy2 = userParam.Phi[fwi][j]* fwd+userParam.beta[fwi][j]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[fwi][j]/userParam.speed[0]+userParam.epsilon[fwi][j];
                                                            if(tempEnergy2+fwtempEnergy+fwcurrent.aPower>userParam.battery){
                                                                newCust2[j] = true;
                                                            }
                                                        }
                                                    }
                                                }

                                                PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                fwaPower = fwcurrent.aPower+ fwtempEnergy;
                                                double leftPower = userParam.battery- fwcurrent.bPower;
                                                double needPower = 0;
                                                if(fwaPower + fwcurrent.bPower>userParam.battery){
                                                    needPower = fwaPower + fwcurrent.bPower-userParam.battery;
                                                }
                                                fwrTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                                double deltaTime = fwrTime - fwcurrent.rechargeTime;
                                                double cost = fwcurrent.cost+deltaTime+userParam.travelTime[h][fwcurrent.city][fwi]-pi[fwi];
                                                fwLabels.add(new fwLabel(fwi,h, fwcurrentidx,cost, fwd, fwcurrent.bPower, fwaPower, fwrTime, fwcurrent.passed,false,newCust1,newCust2));
                                                labelNum++;
                                                if (!fwU.add((Integer) fwidx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    fwLabels.get(fwidx).dominated = true; // => we can forget this label and keep only the other one
                                                } else {
                                                    fwcitySpeed2labels[fwi].add(fwidx);
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
                                    if(fwi == userParam.rechargeStations[k]){
                                        isStation = true;
                                        break;
                                    }
                                }
                                //如果不是充电站
                                if(!isStation){
                                    fwd = fwcurrent.demand + userParam.demand[fwi];
                                    if(fwd <=userParam.capacity){

                                        for (int h = 0; h < userParam.speed.length; h++) {
                                            fwtempEnergy = userParam.Phi[fwcurrent.city][fwi]* fwcurrent.demand+userParam.beta[fwcurrent.city][fwi]*(userParam.speed[h]*userParam.speed[h])+userParam.gamma[fwcurrent.city][fwi]/userParam.speed[h]+userParam.epsilon[fwcurrent.city][fwi];
                                            fwbPower = fwcurrent.bPower+ fwtempEnergy;
                                            if(fwbPower<=userParam.battery){
                                                fwidx = fwLabels.size();
                                                boolean[] newCust1 = new boolean[userParam.nbClients+2];
                                                boolean[] newCust2 = new boolean[userParam.nbClients+2];
                                                System.arraycopy(fwcurrent.vertexVisited, 0, newCust1, 0, userParam.nbClients + 2);
                                                System.arraycopy(fwcurrent.explored, 0, newCust2, 0, userParam.nbClients + 2);
                                                newCust1[fwi] = true;
                                                newCust2[fwi] = true;

                                                for (j = 1; j < userParam.nbClients+1; j++) {
                                                    if(!newCust2[j]){
                                                        d2 = fwd+userParam.demand[j];
                                                        if(d2>userParam.capacity){
                                                            newCust2[j] = true;
                                                        }else{
                                                            tempEnergy2 = userParam.Phi[fwi][j]* fwcurrent.demand+userParam.beta[fwi][j]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[fwi][j]/userParam.speed[0]+userParam.epsilon[fwi][j];
                                                            //mustEnergy2 = userParam.Phi[j][userParam.nbClients+1]*(userParam.capacity-d2)+userParam.beta[j][userParam.nbClients+1]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[j][userParam.nbClients+1]/userParam.speed[0]+userParam.epsilon[j][userParam.nbClients+1];
                                                            if(tempEnergy2+fwbPower>userParam.battery){
                                                                newCust2[j] = true;
                                                            }

                                                        }
                                                    }
                                                }

                                                double cost = fwcurrent.cost+userParam.travelTime[h][fwcurrent.city][fwi]-pi[fwi];
                                                fwLabels.add(new fwLabel(fwi,h, fwcurrentidx,cost, fwd, fwbPower, fwcurrent.aPower, 0, fwcurrent.passed,false,newCust1,newCust2));
                                                labelNum++;
                                                if (!fwU.add((Integer) fwidx)) {
                                                    // only happens if there exists already a label at this vertex with the same cost, time and demand and visiting the same cities before
                                                    // It can happen with some paths where the order of the cities is permuted
                                                    fwLabels.get(fwidx).dominated = true; // => we can forget this label and keep only the other one
                                                } else {
                                                    fwcitySpeed2labels[fwi].add(fwidx);
                                                }
                                            }
                                        }
                                    }
                                }else {
                                    //如果是充电站
                                    //看到这里了！！！！！！！！！！！！！！！！！！！！！！！！！
                                    //System.out.println("station");
                                    fwd = fwcurrent.demand;

                                    for (int h = 0; h < userParam.speed.length; h++) {
                                        fwtempEnergy = userParam.Phi[fwcurrent.city][fwi]* fwcurrent.demand+userParam.beta[fwcurrent.city][fwi]*(userParam.speed[h]*userParam.speed[h])+userParam.gamma[fwcurrent.city][fwi]/userParam.speed[h]+userParam.epsilon[fwcurrent.city][fwi];
                                        fwbPower = fwcurrent.bPower+ fwtempEnergy;
                                        if(fwbPower<=userParam.battery){
                                            fwidx = fwLabels.size();
                                            boolean[] newCust1 = new boolean[userParam.nbClients+2];
                                            boolean[] newCust2 = new boolean[userParam.nbClients+2];
                                            System.arraycopy(fwcurrent.vertexVisited, 0, newCust1, 0, userParam.nbClients + 2);
                                            System.arraycopy(fwcurrent.explored, 0, newCust2, 0, userParam.nbClients + 2);
                                            newCust1[fwi] = true;
                                            newCust2[fwi] = true;

                                            for (int k = 0; k < userParam.rechargeStations.length; k++) {
                                                newCust2[userParam.rechargeStations[k]] =true;
                                            }
                                            for (j = 1; j < userParam.nbClients+1; j++) {
                                                if(!newCust2[j]){
                                                    d2 = fwd+userParam.demand[j];
                                                    if(d2>userParam.capacity){
                                                        newCust2[j] = true;
                                                    }else{
                                                        tempEnergy2 = userParam.Phi[fwi][j]* fwcurrent.demand+userParam.beta[fwi][j]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[fwi][j]/userParam.speed[0]+userParam.epsilon[fwi][j];
                                                        if(tempEnergy2+fwcurrent.aPower>userParam.battery){
                                                            newCust2[j] = true;
                                                        }
                                                    }
                                                }
                                            }

                                            double cost = fwcurrent.cost+userParam.travelTime[h][fwcurrent.city][fwi]-pi[fwi]+userParam.waitingTime[fwi]+userParam.varTheta;
                                            fwLabels.add(new fwLabel(fwi,h, fwcurrentidx,cost, fwd, fwbPower, fwcurrent.aPower, 0,(fwcurrent.passed+1),false,newCust1,newCust2));
                                            labelNum++;
                                            if (!fwU.add((Integer) fwidx)) {
                                                fwLabels.get(fwidx).dominated = true; // => we can forget this label and keep only the other one
                                            } else {
                                                fwcitySpeed2labels[fwi].add(fwidx);
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
        long end2 = System.currentTimeMillis();
        System.out.println(String.format("fw Time: %d ms", end2- start2));
        long start3 = System.currentTimeMillis();



        Integer fwlab;
        fwi = 0;
        System.out.println("BwP.size: "+BwP.size());
        System.out.println("fwP.size: "+fwP.size());
        double UB = userParam.verybig;
        double tempUB = 0;
        while ((fwi < nbRoute) && ((fwlab = fwP.pollFirst())!= null)){
            fwLabel fw = fwLabels.get(fwlab);
            if(!fw.dominated){
                if(fw.cost+userParam.minCost[fw.city] -maxPi+bwLabels.get(BwP.first()).cost<UB){
                    for (Integer bwlab:BwP) {
                        boolean canJoin = true;
                        bwLabel bw1 = bwLabels.get(bwlab);
                        bwLabel bw2;
                        bw2 = bwLabels.get(bw1.indexPrevLabel);
                        if(fw.cost+bw2.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]<UB){
                            if(!bw2.dominated && (fw.demand+bw2.demand<=userParam.capacity) && (fw.passed+bw2.passed<=1)){
                                canJoin = true;
                                for (int l = 1; canJoin &&(l < userParam.nbClients+1); l++) {
                                    if(fw.vertexVisited[l] && bw2.vertexVisited[l]){
                                        canJoin = false;
                                    }
                                }
                                if(canJoin){
                                    if( (fw.passed >0)  ){
                                        double naPower = userParam.Phi[fw.city][bw2.city]* fw.demand+userParam.beta[fw.city][bw2.city]*(userParam.speed[bw1.lSpeed]*userParam.speed[bw1.lSpeed])
                                                +userParam.gamma[fw.city][bw2.city]/userParam.speed[bw1.lSpeed]+userParam.epsilon[fw.city][bw2.city]
                                                +fw.aPower+bw2.bPower+bw2.PhiB*fw.demand;
                                        double nrTime = 0;
                                        if((naPower <= userParam.battery) ){
                                            double leftPower = userParam.battery- fw.bPower;
                                            double needPower = 0;
                                            if(fw.bPower+ naPower >userParam.battery){
                                                PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                needPower = fw.bPower+ naPower -userParam.battery;
                                                nrTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                            }
                                            tempUB = fw.cost+bw2.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]-fw.rechargeTime+nrTime;
                                            if(tempUB<UB){
                                                UB = tempUB;
                                                boolean[] newCust = new boolean[userParam.nbClients+2];
                                                System.arraycopy(fw.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                for (int i = 0; i < userParam.nbClients+2; i++) {
                                                    if(bw2.vertexVisited[i]){
                                                        newCust[i] = true;
                                                    }
                                                }
                                                idx = pairs.size();
                                                pairs.add(new pair(fwlab,bw1.indexPrevLabel,fw.demand-bw2.demand,tempUB,false,nrTime,newCust));
                                                if (!U.add((Integer) idx)) {
                                                    pairs.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                    //System.out.println("Dominated!!!");
                                                } else {
                                                    idx2pair.add(idx);
                                                }
                                            }

                                        }
                                    } else if ( (bw2.passed>0) ) {

                                        double nbPower = fw.bPower+bw2.bPower+fw.demand*bw2.PhiB
                                                +userParam.Phi[fw.city][bw2.city]* fw.demand+userParam.beta[fw.city][bw2.city]*(userParam.speed[bw1.lSpeed]*userParam.speed[bw1.lSpeed])
                                                +userParam.gamma[fw.city][bw2.city]/userParam.speed[bw1.lSpeed]+userParam.epsilon[fw.city][bw2.city];
                                        double naPower = bw2.aPower+fw.demand*bw2.PhiA;

                                        if((naPower<=userParam.battery) && (nbPower<=userParam.battery)){
                                            double nrTime = 0;
                                            if(nbPower+naPower> userParam.battery){
                                                PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                double leftPower = userParam.battery-nbPower;
                                                double needPower = 0;
                                                needPower = nbPower+naPower-userParam.battery;
                                                nrTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                            }
                                            tempUB = fw.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]+nrTime-bw2.rechargeTime;
                                            if(tempUB<UB){
                                                UB = tempUB;
                                                boolean[] newCust = new boolean[userParam.nbClients+2];
                                                System.arraycopy(fw.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                for (int i = 0; i < userParam.nbClients+2; i++) {
                                                    if(bw2.vertexVisited[i]){
                                                        newCust[i] = true;
                                                    }
                                                }
                                                idx = pairs.size();

                                                pairs.add(new pair(fwlab,bw1.indexPrevLabel,fw.demand-bw2.demand,tempUB,false,nrTime,newCust));

                                                if (!U.add((Integer) idx)) {
                                                    pairs.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                    //System.out.println("Dominated!!!");
                                                } else {
                                                    idx2pair.add(idx);
                                                }
                                            }

                                        }
                                    } else{
                                        double nbPower = fw.demand*bw2.PhiB+bw2.bPower+fw.bPower
                                                +userParam.Phi[fw.city][bw2.city]* fw.demand+userParam.beta[fw.city][bw2.city]*(userParam.speed[bw1.lSpeed]*userParam.speed[bw1.lSpeed])
                                                +userParam.gamma[fw.city][bw2.city]/userParam.speed[bw1.lSpeed]+userParam.epsilon[fw.city][bw2.city];
                                        if((nbPower<=userParam.battery) ){
                                            tempUB = fw.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]+bw2.cost;
                                            if(tempUB<UB){
                                                UB = tempUB;
                                                boolean[] newCust = new boolean[userParam.nbClients+2];
                                                System.arraycopy(fw.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                for (int i = 0; i < userParam.nbClients+2; i++) {
                                                    if(bw2.vertexVisited[i]){
                                                        newCust[i] = true;
                                                    }
                                                }
                                                idx = pairs.size();

                                                pairs.add(new pair(fwlab,bw1.indexPrevLabel,fw.demand-bw2.demand,tempUB,false,0,newCust));

                                                if (!U.add((Integer) idx)) {
                                                    pairs.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                    //System.out.println("Dominated!!!");
                                                } else {
                                                    idx2pair.add(idx);
                                                }
                                            }

                                        }
                                    }
                                }
                            }
                            if(bw2.indexPrevLabel>=0){
                                do{
                                    bw1 = null;
                                    bw1 = bw2;
                                    //System.out.println(bw2.indexPrevLabel);
                                    bw2 = bwLabels.get(bw2.indexPrevLabel);
                                    if(fw.cost+bw2.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city] < UB){
                                        if(!bw2.dominated && (fw.demand+bw2.demand<=userParam.capacity) && (fw.passed+bw2.passed<=1)){
                                            canJoin = true;
                                            for (int l = 1; canJoin &&(l < userParam.nbClients+1); l++) {
                                                if(fw.vertexVisited[l] && bw2.vertexVisited[l]){
                                                    canJoin = false;
                                                }
                                            }
                                            if(canJoin){
                                                if( (fw.passed >0)  ){
                                                    double naPower = userParam.Phi[fw.city][bw2.city]* fw.demand+userParam.beta[fw.city][bw2.city]*(userParam.speed[bw1.lSpeed]*userParam.speed[bw1.lSpeed])
                                                            +userParam.gamma[fw.city][bw2.city]/userParam.speed[bw1.lSpeed]+userParam.epsilon[fw.city][bw2.city]
                                                            +fw.aPower+bw2.bPower+bw2.PhiB*fw.demand;
                                                    double nrTime = 0;
                                                    if((naPower <= userParam.battery) ){
                                                        double leftPower = userParam.battery- fw.bPower;
                                                        double needPower = 0;
                                                        if(fw.bPower+ naPower >userParam.battery){
                                                            PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                            needPower = fw.bPower+ naPower -userParam.battery;
                                                            nrTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                                        }
                                                        tempUB = fw.cost+bw2.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]-fw.rechargeTime+nrTime;
                                                        if(tempUB<UB){
                                                            UB = tempUB;
                                                            boolean[] newCust = new boolean[userParam.nbClients+2];
                                                            System.arraycopy(fw.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                            for (int i = 0; i < userParam.nbClients+2; i++) {
                                                                if(bw2.vertexVisited[i]){
                                                                    newCust[i] = true;
                                                                }
                                                            }
                                                            idx = pairs.size();
                                                            pairs.add(new pair(fwlab,bw1.indexPrevLabel,fw.demand-bw2.demand,tempUB,false,nrTime,newCust));
                                                            if (!U.add((Integer) idx)) {
                                                                pairs.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                                //System.out.println("Dominated!!!");
                                                            } else {
                                                                idx2pair.add(idx);
                                                            }
                                                        }

                                                    }
                                                } else if ( (bw2.passed>0) ) {

                                                    double nbPower = fw.bPower+bw2.bPower+fw.demand*bw2.PhiB
                                                            +userParam.Phi[fw.city][bw2.city]* fw.demand+userParam.beta[fw.city][bw2.city]*(userParam.speed[bw1.lSpeed]*userParam.speed[bw1.lSpeed])
                                                            +userParam.gamma[fw.city][bw2.city]/userParam.speed[bw1.lSpeed]+userParam.epsilon[fw.city][bw2.city];
                                                    double naPower = bw2.aPower+fw.demand*bw2.PhiA;

                                                    if((naPower<=userParam.battery) && (nbPower<=userParam.battery)){
                                                        double nrTime = 0;
                                                        if(nbPower+naPower> userParam.battery){
                                                            PiecewiseLinearChargingFunction plf = new PiecewiseLinearChargingFunction();
                                                            double leftPower = userParam.battery-nbPower;
                                                            double needPower = 0;
                                                            needPower = nbPower+naPower-userParam.battery;
                                                            nrTime = plf.RechargingFunction(leftPower,needPower,userParam.battery);
                                                        }
                                                        tempUB = fw.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]+nrTime-bw2.rechargeTime;
                                                        if(tempUB <UB){
                                                            UB = tempUB;
                                                            boolean[] newCust = new boolean[userParam.nbClients+2];
                                                            System.arraycopy(fw.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                            for (int i = 0; i < userParam.nbClients+2; i++) {
                                                                if(bw2.vertexVisited[i]){
                                                                    newCust[i] = true;
                                                                }
                                                            }
                                                            idx = pairs.size();
                                                            pairs.add(new pair(fwlab,bw1.indexPrevLabel,fw.demand-bw2.demand,tempUB,false,nrTime,newCust));
                                                            if (!U.add((Integer) idx)) {
                                                                pairs.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                                //System.out.println("Dominated!!!");
                                                            } else {
                                                                idx2pair.add(idx);
                                                            }
                                                        }

                                                    }
                                                } else{
                                                    double nbPower = fw.demand*bw2.PhiB+bw2.bPower+fw.bPower
                                                            +userParam.Phi[fw.city][bw2.city]* fw.demand+userParam.beta[fw.city][bw2.city]*(userParam.speed[bw1.lSpeed]*userParam.speed[bw1.lSpeed])
                                                            +userParam.gamma[fw.city][bw2.city]/userParam.speed[bw1.lSpeed]+userParam.epsilon[fw.city][bw2.city];
                                                    if((nbPower<=userParam.battery) ){
                                                        tempUB = fw.cost+userParam.travelTime[bw1.lSpeed][fw.city][bw2.city]+bw2.cost;
                                                        if(tempUB <UB){
                                                            boolean[] newCust = new boolean[userParam.nbClients+2];
                                                            System.arraycopy(fw.vertexVisited, 0, newCust, 0, userParam.nbClients + 2);
                                                            for (int i = 0; i < userParam.nbClients+2; i++) {
                                                                if(bw2.vertexVisited[i]){
                                                                    newCust[i] = true;
                                                                }
                                                            }
                                                            idx = pairs.size();
                                                            pairs.add(new pair(fwlab,bw1.indexPrevLabel,fw.demand-bw2.demand,tempUB,false,0,newCust));
                                                            if (!U.add((Integer) idx)) {
                                                                pairs.get(idx).dominated = true; // => we can forget this label and keep only the other one
                                                                //System.out.println("Dominated!!!");
                                                            } else {
                                                                idx2pair.add(idx);
                                                            }
                                                        }

                                                    }
                                                }
                                            }
                                        }
                                    }
                                }while (bw2.indexPrevLabel>=0 && !canJoin);
                            }
                        }
                    }
                }else {
                    fw.dominated = true;
                }
            }
        }
        long end3 = System.currentTimeMillis();
        System.out.println(String.format("merge Time: %d ms", end3- start3));
        System.out.println("U.size: "+U.size());
        int i = 0;
        int num = 0;
        while (U.size()>0){
            num++;
            currentidx = U.pollFirst();
            current = pairs.get(currentidx);
            if(!current.dominated){
                fwLabel fwL = fwLabels.get(current.fwIndex);
                bwLabel bwL = bwLabels.get(current.bwIndex);
                if(current.cost<-1e-4 && (!fwL.dominated) && (!bwL.dominated)){
                    Route newRoute = new Route();
                    newRoute.setCost(current.cost);
                    newRoute.addCity(fwL.city);
                    //System.out.println("pair.rechargetime: "+current.reChargeTime+"; fw.rechargeTime: "+fwL.rechargeTime+"bw.rechargetime: "+ bwL.rechargeTime+";fw.passed: "+fwL.passed+";bw.passed: "+bwL.passed);
                    newRoute.setRechargeTime(current.reChargeTime);
                    newRoute.addSpeed(fwL.lSpeed);
                    int path = fwL.indexPrevLabel;
                    while (path>=0){
                        newRoute.addCity(fwLabels.get(path).city);
                        newRoute.addSpeed(fwLabels.get(path).lSpeed);
                        path = fwLabels.get(path).indexPrevLabel;
                    }
                    newRoute.switchPath();
                    newRoute.switchSpeedVec();
                    newRoute.addCity(bwL.city);
                    newRoute.addSpeed(bwL.lSpeed);
                    path = bwL.indexPrevLabel;
                    while (path>=0){
                        newRoute.addCity(bwLabels.get(path).city);
                        newRoute.addSpeed(bwLabels.get(path).lSpeed);
                        path = bwLabels.get(path).indexPrevLabel;
                    }
                    System.out.println("newRoute: "+newRoute.getPath());
                    routes.add(newRoute);
                    i++;
                }
            }

        }

        System.out.println("labelnum:"+ bwlabelNum);
    }

}
