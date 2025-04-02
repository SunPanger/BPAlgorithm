package FirstTry;
import java.util.ArrayList;
public class Route implements Cloneable {
    public double cost;
    public double Q;
    public ArrayList<Integer> path;
    public ArrayList<Integer> speedVec;
    public double rechargeTime;
    public boolean removed = false;
    public Route(){
        this.path = new ArrayList<Integer>();
        this.speedVec = new ArrayList<Integer>();
        this.cost = 0.0;
        this.rechargeTime = 0.0;
    }
    public Route(int pathSize){
        this.path = new ArrayList<Integer>(pathSize);
        this.speedVec = new ArrayList<Integer>(pathSize);
        this.cost = 0.0;
        this.rechargeTime = 0.0;
    }
    public Route clone() throws CloneNotSupportedException{
        Route route = (Route) super.clone();
        route.path = (ArrayList<Integer>) path.clone();
        route.speedVec = (ArrayList<Integer>) speedVec.clone();
        return route;
    }
    public void removeCity(int city) {
        this.path.remove(Integer.valueOf(city));
    }
    public void addCity(int f_city, int city){
        int index = this.path.indexOf(f_city);
        this.path.add(index+1,city);
    }
    public void removeSpeed(int speed){
        this.speedVec.remove(Integer.valueOf(speed));
    }
    public void addSpeed(int f_city,int speed){
        int index = this.path.indexOf(f_city);
        this.speedVec.add(index+1,speed);
    }
    public void addCity(int city){this.path.add(city);}
    public void addSpeed(int speed){this.speedVec.add(speed);}
    public void setCost(double c){this.cost = c;}
    public void setRechargeTime(double r){this.rechargeTime = r;}
    public double getCost(){return this.cost;}
    public double getRechargeTime(){return this.rechargeTime;}
    public void setQ(double a){this.Q = a;}
    public double getQ(){return this.Q;}
    public ArrayList<Integer> getPath(){return this.path;}
    public ArrayList<Integer> getSpeedVec(){return this.speedVec;}
    public void switchPath(){
        Integer swap;
        int nb = path.size()/2;
        for (int i = 0; i < nb; i++) {
            swap = path.get(i);
            path.set(i,path.get(path.size()-1-i));
            path.set(path.size()-1-i,swap);
        }
    }
    public void switchSpeedVec(){
        Integer swap;
        int nb = path.size()/2;
        for (int i = 0; i < nb; i++) {
            swap = speedVec.get(i);
            speedVec.set(i,speedVec.get(path.size()-1-i));
            speedVec.set(path.size()-1-i,swap);
        }
    }
    public boolean contains(int i){
        return this.path.contains(i);
    }
    
    public void updateCost(double[][][] travelTime,double varTheta,double[] speed){
        this.cost=0;
        int current = 0;
        int i,next;
        for (int j = 1; j < path.size(); j++) {
            next = path.get(j);
            cost+= travelTime[speedVec.get(j)][current][next];

            current = next;
        }
        if(rechargeTime>0){
            cost+=rechargeTime;
            cost+=varTheta;
        }

    }

}
