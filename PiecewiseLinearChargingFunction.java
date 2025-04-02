package FirstTry;

public class PiecewiseLinearChargingFunction {
    public double RechargingFunction(double leftPower,double needPower,double battery){
        //System.out.println("input:"+leftPower);
        if(needPower>0){
            double L1 = 0.2*battery;
            double L2 = 0.8*battery;
            double L3 = 1*battery;
            double time;
            double v1 = 11;
            double v2 = 22;
            double v3 = 44;
            double target = leftPower+needPower;
            if(leftPower<=L1){
                if(target<=L1){
                    time = needPower/v1;
                    return time * 3600;
                }
                else if((target >L1) && (target<=L2)){
                    time = (L1-leftPower)/v1+(target)/v2;
                    return time*3600;
                }
                else {
                    if((target>=L2) && (target<=L3)){
                        time = (L1-leftPower)/v1+(L2-L1)/v2+(target-L2)/v3;
                        return time*3600;
                    }
                    else {
                        System.out.println("charge error!,targetPower = "+target+",leftPower:"+leftPower+",needPower:"+needPower);
                        return 1E10;
                    }
                }
            } else if ((leftPower>L1)&&(leftPower<=L2)) {
                if(target<=L2){
                    time = needPower/v2;
                    return time*3600;
                } else if ((target>L2)&&(target<=L3)) {
                    time = (L2-leftPower)/v2+(target-L2)/v3;
                    return time*3600;
                }else {
                    System.out.println("charge error!,targetPower = "+target+",leftPower:"+leftPower+",needPower:"+needPower);
                    return 1E10;
                }
            }else {
                if((leftPower>L2) && (leftPower<=L3)){
                    if(target<=L3){
                        time = needPower/v3;
                        return time*3600;
                    }
                    else {
                        System.out.println("charge error!,targetPower = "+target+",leftPower:"+leftPower+",needPower:"+needPower);
                        return 1E10;
                    }
                }else {
                    System.out.println("charge error!,targetPower = "+target+",leftPower:"+leftPower+",needPower:"+needPower);
                    return 1E10;
                }
            }
        }else {
            return 0;
        }
    }
}
