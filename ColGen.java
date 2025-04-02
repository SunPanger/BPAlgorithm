package FirstTry;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.io.IOException;
import java.util.List;
import gurobi.*;
public class ColGen {
    static class GRBNumVarArray{
        int _num = 0;
        GRBVar[] _array = new GRBVar[32];
        void add(GRBVar ivar){
            if(_num >= _array.length){
                GRBVar[] array = new GRBVar[2* _array.length];
                System.arraycopy(_array,0,array,0,_num);
                _array = array;
            }
            _array[_num++] = ivar;
        }
        GRBVar getElement(int i){return _array[i];}
        int getSize(){return _num;}
    }
    public double computeColGen(Parameter userParam, ArrayList<Route> routes,int depth)
        throws IOException{
        int nIter,colNum=0,nbRoute;
        double cost,obj;
        double[] pi = new double[userParam.Clients.length];
        double mu;
        boolean oncemore;
        nbRoute = 200;
        try{
            GRBEnv env = new GRBEnv();
            GRBModel master = new GRBModel(env);
            GRBConstr[] setPartitioning = new GRBConstr[userParam.Clients.length];
            GRBConstr[] vehicleNum = new GRBConstr[1];
            //创建变量
            GRBNumVarArray lambda = new GRBNumVarArray();
            //生成初始解
            ArrayList<Route> tempRoutes = new ArrayList<>();
            if(routes.size()<1){
                for (int k = 0; k < userParam.Clients.length; k++) {
                    ArrayList<Integer> temp = new ArrayList<Integer>(1);
                    temp.add(userParam.Clients[k]);
                    Route tempRoute = new Route();
                    tempRoute.path = temp;
                    tempRoutes.add(tempRoute);
                }

                Savings Save = new Savings();
                tempRoutes = Save.savings(tempRoutes,userParam);
                for (int i = 0; i < tempRoutes.size(); i++) {
                    routes.add(tempRoutes.get(i));
                }
                System.out.println("initial routes:");
                for (int i = 0; i < routes.size(); i++) {
                    System.out.println("route["+i+"]: "+routes.get(i).path);
                }
                colNum = routes.size();

            }

            int v =0;
            //计算每条路径的成本
            for (Route r : routes) {
                r.updateCost(userParam.travelTime,userParam.varTheta,userParam.speed);
                //System.out.println("r.cost: "+r.getCost());
                lambda.add(master.addVar(0,1,0,GRB.CONTINUOUS,"lambda"+v));
                v++;
            }
            //生成目标函数
            GRBLinExpr expr = new GRBLinExpr();
            for (int k = 0; k < routes.size(); k++) {
                expr.addTerm(routes.get(k).cost, lambda.getElement(k));
            }
            master.setObjective(expr,GRB.MINIMIZE);
            master.update();
            //添加setPartitioning约束
            for (int k = 0; k < userParam.Clients.length; k++) {
                expr = new GRBLinExpr();
                for (int l = 0; l < routes.size(); l++) {
                    if(routes.get(l).contains(userParam.Clients[k])){
                        expr.addTerm(1, lambda.getElement(l));
                    }
                }
                setPartitioning[k] = master.addConstr(expr,GRB.EQUAL,1,"setPartitioning"+k);
            }
            //添加车辆数量约束
            expr = new GRBLinExpr();
            for (int k = 0; k < routes.size(); k++) {
                expr.addTerm(1,lambda.getElement(k));
            }
            vehicleNum[0] = master.addConstr(expr,GRB.LESS_EQUAL,userParam.nbVehicle,"vehicleNum");
            master.update();
            //关闭log输出
            master.set("OutputFlag","0");
            //开始列生成
            oncemore = true;
            nIter = 0;
            int previ = -1;
            double[] prevobj = new double[100];
            DecimalFormat df = new DecimalFormat("#0000.00");
            while (oncemore ){
                double[] allpi = new double[userParam.nbClients+2];
                oncemore =false;
                nIter++;
                master.optimize();

                /*if(depth>196){
                    for (int i = 0; i < lambda.getSize(); i++) {
                        if(lambda.getElement(i).get(GRB.DoubleAttr.X)>0){
                            System.out.println(i+","+lambda.getElement(i).get(GRB.DoubleAttr.X));
                            System.out.println(routes.get(i).getPath());
                        }
                    }

                }*/
                int optStatus = master.get(GRB.IntAttr.Status);
                if(optStatus == GRB.Status.OPTIMAL){
                    prevobj[(++previ) % 100] = master.get(GRB.DoubleAttr.ObjVal);
                }else {
                    System.out.println("CG: relaxation infeasible!");
                    return 1E10;
                }
                //获得对偶变量
                for (int j = 0; j < userParam.Clients.length; j++) {
                    pi[j] = setPartitioning[j].get(GRB.DoubleAttr.Pi);
                }
                for (int j = 0; j < userParam.nbClients+2; j++) {
                    for (int k = 0; k < userParam.Clients.length; k++) {
                        if(j == userParam.Clients[k]){
                            allpi[j] = pi[k];
                        }
                    }
                }
                mu = vehicleNum[0].get(GRB.DoubleAttr.Pi);
                if(mu>0){
                    System.out.println("mu:   "+mu);
                }
                //生成新路径
                ESPPRC espprc = new ESPPRC();
                ArrayList<Route> routesESPPRC = new ArrayList<Route>();
                espprc.shortestPathSpeedOpt(userParam,routesESPPRC,nbRoute,mu,allpi);
                espprc = null;

                //添加新的列
                if(routesESPPRC.size()>0){
                    /*for (int i = 0; i < routesESPPRC.size(); i++) {
                        if(routesESPPRC.get(i).rechargeTime>0){
                            System.out.println("Recharge is done");
                        }
                    }*/
                    for (int i = 0; i < routesESPPRC.size(); i++) {
                        System.out.println(routesESPPRC.get(i).getPath());
                        System.out.println(routesESPPRC.get(i).getCost());
                    }
                    oncemore = true;
                    for(Route r:routesESPPRC){
                        ArrayList<Integer> route = r.getPath();

                        GRBColumn col = new GRBColumn();
                        for (int j = 1; j <route.size()-1 ; j++) {
                            for (int k = 0; k < userParam.Clients.length; k++) {
                                if(route.get(j) == userParam.Clients[k]){
                                    //将列与集合覆盖约束关联

                                    col.addTerm(1,setPartitioning[k]);
                                    continue;
                                }
                            }
                        }
                        //将列与车辆数量约束关联
                        col.addTerm(1,vehicleNum[0]);
                        r.updateCost(userParam.travelTime,userParam.varTheta,userParam.speed);
                        colNum++;
                        GRBVar newvar = master.addVar(0,1,r.cost,GRB.CONTINUOUS,col,"lambda"+routes.size());
                        lambda.add(newvar);
                        routes.add(r);
                        master.update();
                    }
                    System.out.print("\nCG Iter " + previ + " Current cost: "
                            + df.format(prevobj[previ % 100]) + " " + routes.size()
                            + " routes");
                    System.out.flush();
                    System.out.println();
                }
                routesESPPRC = null;
            }

            for (int i = 0; i < lambda.getSize(); i++) {
                //System.out.println("size:"+lambda.getSize());
                //System.out.println(lambda.getElement(i).get(GRB.DoubleAttr.X));
                routes.get(i).setQ(lambda.getElement(i).get(GRB.DoubleAttr.X));
                //if(routes.get(i).path.size()<2){
                    //System.out.println("route:"+i+","+routes.get(i).path);
                    //System.out.println("route:"+i+","+routes.get(i).speedVec);
                    //System.out.println();
                //}


            }
            obj = master.get(GRB.DoubleAttr.ObjVal);
            return obj;
        }catch (GRBException e){
            System.out.println("Error code:" +e.getErrorCode() +"."+e.getMessage());

        }
        return 1E10;
    }
}
