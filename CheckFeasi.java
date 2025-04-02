package FirstTry;

import java.util.List;

public class CheckFeasi {
    public int checkFeasi(List<Integer> route, Parameter userParam){
        double weight = userParam.capacity;
        double power = 0;
        int now = route.get(0);
        int before = 0;
        double mass = 0;
        power = userParam.Phi[0][now]*weight+userParam.beta[0][now]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[0][now]/userParam.speed[0]+userParam.epsilon[0][now];
        for (int i = 1; i < route.size(); i++) {
            weight = weight-userParam.demand[before];
            now = route.get(i);
            before = route.get(i-1);
            power = power + userParam.Phi[before][now]*weight+userParam.beta[before][now]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[before][now]/userParam.speed[0]+userParam.epsilon[before][now];
            mass+=userParam.demand[route.get(i)];
        }
        before = now;
        now = userParam.nbClients+1;
        power = power+userParam.Phi[before][now]*weight+userParam.beta[before][now]*(userParam.speed[0]*userParam.speed[0])+userParam.gamma[before][now]/userParam.speed[0]+userParam.epsilon[before][now];


        if (route.size() == 0 || mass>userParam.capacity){
            return -1;
        } else if (power>userParam.battery) {
            return 0;
        } else {
            return 1;
        }
    }
}
