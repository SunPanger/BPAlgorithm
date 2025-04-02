package FirstTry;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Date day = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Start Time: "+df.format(day));
        Parameter instance = new Parameter();
        instance.initParams("F:\\dataset\\c101.txt");
        for (int i = 0; i < instance.nbClients+2; i++) {
            instance.demand[i] = instance.demand[i]*40;
        }
        for (int i = 0; i < instance.rechargeStations.length; i++) {
            instance.demand[instance.rechargeStations[i]] = 0;
        }

        instance.capacity =5000;
        double[] pi = new double[instance.nbClients+2];
        for (int i = 0; i < instance.nbClients+2; i++) {
            pi[i] = 1000;
        }
        for (int i = 0; i < instance.rechargeStations.length; i++) {
            pi[instance.rechargeStations[i]] = 0;
        }
        pi[0] = 0;
        double mu = 0;
        pi[instance.nbClients+1] = 0;
        instance.demand[instance.nbClients+1] = 0;
        //BranchBound BB = new BranchBound();
        ArrayList<Route> initRoutes = new ArrayList<>();
        ArrayList<Route> bestRoutes = new ArrayList<Route>();
        int nbroute = 1000;
        System.out.println("address of input: "+initRoutes);
        ESPPRC espprc = new ESPPRC();
        BWESPPRC bwespprc = new BWESPPRC();

        long start1 = System.currentTimeMillis();
        //espprc.shortestPathSpeedOpt(instance,initRoutes,nbroute,mu,pi);
        long end1 = System.currentTimeMillis();
        System.out.println(String.format("Total Time of mono: %d ms", end1- start1));


        //BiDirectional biespprc = new BiDirectional();
        long start2 = System.currentTimeMillis();
        //biespprc.BiDirectShortestPathSpeedOpt(instance,bestRoutes,nbroute,mu,pi);
        long end2 = System.currentTimeMillis();
        System.out.println(String.format("Total Time of bi: %d ms", end2- start2));




        int m,n;
        for (m = 0; m < 4; m++){
            instance.dist[3][m] = instance.verybig;
            instance.Phi[3][m] = instance.verybig;
            instance.gamma[3][m] = instance.verybig;
            instance.epsilon[3][m] = instance.verybig;
            instance.beta[3][m] = instance.verybig;
            for (int h  = 0; h < instance.speed.length; h++) {
                instance.travelTime[h][3][m] = instance.verybig;
            }
        }
        for (m++; m < instance.nbClients + 2; m++){
            instance.dist[3][m] = instance.verybig;
            instance.Phi[3][m] = instance.verybig;
            instance.gamma[3][m] = instance.verybig;
            instance.epsilon[3][m] = instance.verybig;
            instance.beta[3][m] = instance.verybig;
            for (int h  = 0; h < instance.speed.length; h++) {
                instance.travelTime[h][3][m] = instance.verybig;
            }
        }
        for (m = 0; m < 3; m++){
            instance.dist[m][4] = instance.verybig;
            instance.Phi[m][4] = instance.verybig;
            instance.gamma[m][4] = instance.verybig;
            instance.epsilon[m][4] = instance.verybig;
            instance.beta[m][4] = instance.verybig;
            for (int h  = 0; h < instance.speed.length; h++) {
                instance.travelTime[h][m][4] = instance.verybig;
            }
        }
        for (m++; m < instance.nbClients + 2; m++){
            instance.dist[m][4] = instance.verybig;
            instance.Phi[m][4] = instance.verybig;
            instance.gamma[m][4] = instance.verybig;
            instance.epsilon[m][4] = instance.verybig;
            instance.beta[m][4] = instance.verybig;
            for (int h  = 0; h < instance.speed.length; h++) {
                instance.travelTime[h][m][4] = instance.verybig;
            }
        }
        // forbid the edge in the opposite direction
        instance.dist[4][3] = instance.verybig;
        instance.Phi[4][3] =instance.verybig;
        instance.gamma[4][3] = instance.verybig;
        instance.epsilon[4][3] = instance.verybig;
        instance.beta[4][3] = instance.verybig;
        for (int h  = 0; h < instance.speed.length; h++) {
            instance.travelTime[h][4][3] = instance.verybig;
        }


        PiecewiseLinearChargingFunction plcf = new PiecewiseLinearChargingFunction();
        double needtime = plcf.RechargingFunction(10.444951305313412,10.444951305313412+17.427539557453343-instance.battery,instance.battery);
        System.out.println("needTime:"+needtime);

        //BB.BBNode(instance,initRoutes,null,bestRoutes,0,start);
        ColGen cg = new ColGen();
        double CGobj;
        int depth = 0;
        CGobj = cg.computeColGen(instance, initRoutes,depth);


        double optCost = 0;
        System.out.println();
        System.out.println("Solution >>>");
        double time = 0;

        /*for(Route bestRoute:bestRoutes){

            System.out.println(bestRoute.path);
            //System.out.println(bestRoute.speedVec);
            //System.out.println("Recharge time:"+bestRoute.rechargeTime);
            boolean same = true;

        }*/
        System.out.println("//////////////////////////");
        for(Route bestRoute:initRoutes){

            System.out.println(bestRoute.path);
            System.out.println(bestRoute.speedVec);
            System.out.println("Recharge time:"+bestRoute.rechargeTime);
            //optCost += bestRoute.cost;

        }
        //System.out.println("\n best Cost = "+optCost);
        ArrayList<Route> routeswESPPRC = new ArrayList<Route>();
        System.out.println("BW!!!!!!!!!!!!!!!!!!!!");


    }
}
