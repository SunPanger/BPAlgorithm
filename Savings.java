package FirstTry;
import java.util.ArrayList;
import java.util.List;

public class Savings {
    public ArrayList<Route> savings(ArrayList<Route> preRoutes, Parameter userParam){
        ArrayList<Route> result = new ArrayList<>();

        boolean flag = true;
        CheckFeasi cf = new CheckFeasi();
        while (flag){
            flag =false;
            if((preRoutes.size()%100)==0){
                System.out.println(preRoutes.size());
            }
            for (int i = 0; i < preRoutes.size(); i++) {
                if(preRoutes.get(i).removed == true)
                    continue;
                for (int j = 0; j < preRoutes.size(); j++) {
                    if(i==j){
                        continue;
                    }
                    if(preRoutes.get(j).removed == true){
                        continue;
                    }
                    Route r1 = preRoutes.get(i);
                    Route r2 = preRoutes.get(j);
                    int u = r1.path.get(r1.path.size()-1);
                    int v = r2.path.get(0);

                    if(userParam.dist[u][0]+userParam.dist[0][v] > userParam.dist[u][v]){
                        ArrayList<Integer> newPath = new ArrayList<>();
                        newPath.addAll(r1.path);
                        newPath.addAll(r2.path);
                        if(cf.checkFeasi(newPath,userParam) == 1){
                            Route newR = new Route();
                            newR.path = newPath;
                            preRoutes.add(newR);
                            preRoutes.get(i).removed =true;
                            preRoutes.get(j).removed = true;
                            flag =true;
                            break;
                        } else if (cf.checkFeasi(newPath,userParam) == 0) {
                            Route newR = new Route();
                            newR.path = newPath;
                            newR.rechargeTime = 100;
                            preRoutes.add(newR);
                            preRoutes.get(i).removed =true;
                            preRoutes.get(j).removed = true;
                            flag =true;
                            break;
                        }

                    }
                }
            }
        }
       // System.out.println("Preroutes.size:"+preRoutes.size());
        //System.out.println("address of preroutes:"+preRoutes);
        for (int i = 0; i < preRoutes.size(); i++) {
            //System.out.println(preRoutes.get(i).path);
            if(preRoutes.get(i).removed ==false){
                if(preRoutes.get(i).rechargeTime!=100){
                    Route newRoute = preRoutes.get(i);
                    int first = newRoute.path.get(0);
                    newRoute.addCity(userParam.nbClients+1);
                    newRoute.addCity(first,0);
                    newRoute.removeCity(first);
                    newRoute.addCity(0,first);
                    newRoute.speedVec.add(0);
                    for (int j = 1; j < newRoute.getPath().size(); j++) {
                        newRoute.speedVec.add(0);
                    }
                    result.add(newRoute);
                }else{
                    System.out.println("here /////////////");
                    int bestR=-1;
                    int bestPos=-1;
                    double minDist = userParam.verybig;
                    Route newRoute = preRoutes.get(i);
                    int first = newRoute.path.get(0);
                    newRoute.addCity(userParam.nbClients+1);
                    newRoute.addCity(first,0);
                    newRoute.removeCity(first);
                    newRoute.addCity(0,first);

                    for (int r = 0; r < userParam.rechargeStations.length; r++) {
                        for (int j = 1; j < newRoute.path.size()-2; j++) {
                            double tempDist = userParam.dist[newRoute.path.get(j)][userParam.rechargeStations[r]]+userParam.dist[userParam.rechargeStations[r]][newRoute.path.get(j+1)];
                            if(tempDist<minDist){
                                minDist = tempDist;
                                bestR = r;
                                bestPos = j;
                            }
                        }
                    }
                    newRoute.addCity(newRoute.path.get(bestPos),userParam.rechargeStations[bestR]);
                    newRoute.speedVec.add(0);
                    for (int j = 1; j < newRoute.getPath().size(); j++) {
                        newRoute.speedVec.add(0);
                    }

                    PiecewiseLinearChargingFunction PLC = new PiecewiseLinearChargingFunction();
                    double leftPower  = 0;
                    double weight = userParam.capacity;
                    double needPower = 0;
                    for (int j = 0;(j!=(bestPos+1)) && (j < newRoute.path.size()); j++) {
                        leftPower = leftPower + userParam.Phi[newRoute.path.get(j)][newRoute.path.get(j+1)]*weight+userParam.beta[newRoute.path.get(j)][newRoute.path.get(j+1)]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[newRoute.path.get(j)][newRoute.path.get(j+1)]/userParam.speed[0]+userParam.epsilon[newRoute.path.get(j)][newRoute.path.get(j+1)];
                        weight = weight-userParam.demand[newRoute.path.get(j+1)];
                    }
                    for (int j = bestPos+1; j < newRoute.path.size()-1; j++) {
                        needPower = needPower + userParam.Phi[newRoute.path.get(j)][newRoute.path.get(j+1)]*weight+userParam.beta[newRoute.path.get(j)][newRoute.path.get(j+1)]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[newRoute.path.get(j)][newRoute.path.get(j+1)]/userParam.speed[0]+userParam.epsilon[newRoute.path.get(j)][newRoute.path.get(j+1)];
                        weight = weight-userParam.demand[newRoute.path.get(j+1)];
                    }

                    leftPower = userParam.battery-leftPower;
                    needPower = needPower-leftPower;
                    double needTime = PLC.RechargingFunction(leftPower,needPower,userParam.battery);

                    newRoute.setRechargeTime(needTime);

                    result.add(newRoute);
                }

            }
        }
        preRoutes =result;
        System.out.println("address of result:"+result);
        return result;
    }



}

