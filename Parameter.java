package FirstTry;
import java.lang.Math;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class Parameter {
    public int nbVehicle;
    public int nbClients;
    public int capacity;
    public int[] Clients;
    public int[] rechargeStations;
    public long TimeLimit;
    public double battery;
    public double curbMass;
    public double frontArea;
    public double airDragCoef;
    public double rollFrictionCoef;
    public double auxilPower;
    public double gravConst;
    public double airDensity;
    public double efficiency;
    public double[][] cost;
    public double[][] distBase;
    public double[][] dist;
    public double[][] edges;
    public double[][] roadtTheta;
    public double[] posx, posy, demand, wval;
    public double[][][] travelTime;
    public double[][][] travelTimeBase;
    public double[][] Phi;
    public double[][] PhiBase;
    public double [][] beta;
    public double [][] betaBase;
    public double [][] gamma;
    public double [][] gammaBase;
    public double[][] epsilon;
    public double[][] epsilonBase;
    public double[] waitingTime;
    public double varTheta;
    public double verybig;
    public double[] speed;
    public double gap;
    public double maxlength;
    public double minSpeed;
    public double maxSpeed;
    public int speedLength;
    public double speedInterval;
    public double[] minCost;

    String[] citieslab;

    public Parameter(){
        gap = 0.00000000001;
        TimeLimit = 3600000;
        nbClients = 10;
        maxSpeed = 33.3;
        nbVehicle = 2;
        verybig = 1E10;
        rechargeStations = new int[]{1};
        Clients = new int[nbClients-rechargeStations.length];
        waitingTime = new double[]{150,200,300};
        varTheta = 0.1;
        battery = 70;
        curbMass = 6350;
        frontArea = 3.912;
        airDragCoef = 0.7;
        rollFrictionCoef = 0.01;
        auxilPower = 1575;
        gravConst = 9.81;
        airDensity = 1.2041;
        efficiency = 1.3175;
        speedLength = 1;
        minSpeed = 10;
        speed = new double[speedLength+1];
        speedInterval = (maxSpeed-minSpeed)/speedLength;
        for (int i = 0; i < speedLength+1; i++) {
            speed[i] = minSpeed+speedInterval*i;
        }

        //speed = new double[1];
        //speed[0] = 33.3;
        int reNum = rechargeStations.length;
        int cNum = 0;
        for (int i = 1; i < nbClients+1; i++) {
            boolean isRecharge =false;
            for (int j = 0; j < reNum; j++) {
                if(i==rechargeStations[j]){
                    isRecharge =true;
                    continue;
                }
            }
            if(!isRecharge){
                Clients[cNum] = i;
                cNum++;
            }
        }



    }
    public void initParams(String inputPath) throws IOException{
        int i,j;
        try{
            BufferedReader br = new BufferedReader(new FileReader(inputPath));
            String line = new String();
            for (i = 0; i < 5; i++)
                line = br.readLine();
            String[] tokens = line.split("\\s+");
            //nbVehicle = Integer.parseInt(tokens[1]);
            //capacity = Integer.parseInt(tokens[2]);

            citieslab = new String[nbClients+2];
            demand = new double[nbClients+2];
            posx = new double[nbClients+2];
            posy = new double[nbClients+2];
            roadtTheta = new double[nbClients+2][nbClients+2];
            distBase = new double[nbClients+2][nbClients+2];
            cost = new double[nbClients+2][nbClients+2];
            dist = new double[nbClients+2][nbClients+2];
            Phi = new double[nbClients+2][nbClients+2];
            PhiBase = new double[nbClients+2][nbClients+2];
            beta = new double[nbClients+2][nbClients+2];
            betaBase = new double[nbClients+2][nbClients+2];
            gamma = new double[nbClients+2][nbClients+2];
            gammaBase = new double[nbClients+2][nbClients+2];
            epsilon = new double[nbClients+2][nbClients+2];
            epsilonBase = new double[nbClients+2][nbClients+2];
            travelTime = new double[speed.length][nbClients+2][nbClients+2];
            travelTimeBase = new double[speed.length][nbClients+2][nbClients+2];
            waitingTime = new double[nbClients+2];
            minCost = new double[nbClients+2];

            for ( i = 0; i <4 ; i++) {
                line = br.readLine();
            }
            for ( i = 0; i < nbClients+1; i++) {
                line = br.readLine();
                tokens = line.split("\\s+");
                citieslab[i] = tokens[1];
                posx[i] = Double.parseDouble(tokens[2]);
                posy[i] = Double.parseDouble(tokens[3]);
                demand[i] = Double.parseDouble(tokens[4]);
            }
            br.close();
            citieslab[nbClients+1] = citieslab[0];
            demand[nbClients+1] = 0.0;
            posx[nbClients+1] = posx[0];
            posy[nbClients+1] = posy[0];
            double max;
            double min;
            maxlength = 0.0;

            for (int k = 1; k < nbClients+2; k++) {
                for (int l = 0; l < rechargeStations.length; l++) {
                    if(k == rechargeStations[l]){
                        demand[k] = 0;
                        break;
                    }
                }
            }



            for (i = 0; i < nbClients+2; i++) {
                max = 0.0;

                for ( j = 0; j < nbClients+2; j++) {
                    distBase[i][j] = 1000*((int) (10 * Math
                            .sqrt((posx[i] - posx[j]) * (posx[i] - posx[j])
                                    + (posy[i] - posy[j]) * (posy[i] - posy[j])))) / 10.0;
                    // truncate to get the same results as in Solomon
                    if (max < distBase[i][j]) max = distBase[i][j];
                }
                maxlength += max;
            }
            for (i = 0; i < nbClients+2; i++) {
                distBase[i][0] = verybig;
                distBase[nbClients+1][i] = verybig;
                distBase[i][i] = verybig;
            }
            distBase[0][nbClients+1] = 0;
            distBase[nbClients+1][0] = 0;

            for (int k = 0; k < rechargeStations.length; k++) {
                for (int l = 0; l < rechargeStations.length; l++) {
                    distBase[rechargeStations[k]][rechargeStations[l]] = verybig;
                }
            }


            for (i = 0; i < nbClients+2; i++) {
                min = verybig;
                for (j = 0; j < nbClients+2; j++) {
                    dist[i][j] = distBase[i][j];
                    Phi[i][j] = gravConst*(Math.sin(roadtTheta[i][j])+rollFrictionCoef*Math.cos(roadtTheta[i][j]))*dist[i][j]/(3.6e6*efficiency);
                    PhiBase[i][j] = Phi[i][j];

                    beta[i][j] = 0.5*frontArea*airDragCoef*airDensity*dist[i][j]/(3.6e6*efficiency);
                    betaBase[i][j] = beta[i][j];

                    gamma[i][j] = dist[i][j]*auxilPower/(3.6e6*efficiency);
                    gammaBase[i][j] = gamma[i][j];

                    epsilon[i][j] = curbMass*gravConst*(Math.sin(roadtTheta[i][j])+Math.cos(roadtTheta[i][j])*rollFrictionCoef)*dist[i][j]/(3.6e6*efficiency);
                    epsilonBase[i][j] = epsilon[i][j];
                    if(dist[i][j]<min){
                        min = dist[i][j];
                    }
                }
                minCost[i] = min/maxSpeed;
            }
            for (int h  = 0; h < speed.length; h++) {
                for (i  = 0; i < nbClients+2; i++) {
                    for (j = 0; j < nbClients+2; j++) {
                        travelTime[h][i][j] = dist[i][j]/speed[h];
                        travelTimeBase[h][i][j] = travelTime[h][i][j];
                    }
                }
            }
            for (j = 0; j <nbClients+2 ;j ++) {
                cost[0][j] = dist[0][j];
                cost[j][nbClients+1] = dist[j][nbClients+1];
            }

        }catch (IOException e){
            System.err.println("Error: "+e);
        }
        wval = new double[nbClients+2];
        for (i = 1; i < nbClients+2; i++) {
            wval[i] = 0.0;
            edges = new double[nbClients+2][nbClients+2];
        }
    }
}