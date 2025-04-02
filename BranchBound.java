package FirstTry;
import java.io.IOException;
import java.util.ArrayList;

public class BranchBound {
    double lowerbound;
    double upperbound;

    public BranchBound() {
        lowerbound = -1E10;
        upperbound = 1E10;

    }

    class treeBB {
        // this is a linked tree list recording all the branching during Branch and Bound
        treeBB father; // link to the node processed before branching
        treeBB son0; // link to the son on the left of the tree (edge=0; first processed) => need it to compute the global lowerbound
        int branchFrom; // we branch on edges between cities => city origin of the edge
        int branchTo; // we branch on edges between cities => city destination of the edge
        int branchValue; // we branch on edges between cities => value of the branching (remove edge=0; set edge=1)
        double lowestValue; // lower bound on the solution if we start from this node (i.e. looking only down for this tree)
        boolean toplevel; // to compute the global lowerBound, need to know if everything above has been considered
        boolean[][] Branchable; //if this edge can be branched
    }

    public void EdgesBasedOnBranching(Parameter userParam, treeBB branching,
                                      boolean recur) {
        int i;
        if (branching.father != null) { // stop before root node
            if (branching.branchValue == 0) { // forbid this edge (in this direction)
                // associate a very large distance to this edge to make it unattractive
                userParam.dist[branching.branchFrom][branching.branchTo] = userParam.verybig;
                userParam.Phi[branching.branchFrom][branching.branchTo] = userParam.verybig;
                userParam.gamma[branching.branchFrom][branching.branchTo] = userParam.verybig;
                userParam.epsilon[branching.branchFrom][branching.branchTo] = userParam.verybig;
                userParam.beta[branching.branchFrom][branching.branchTo] = userParam.verybig;
                for (int h  = 0; h < userParam.speed.length; h++) {
                    userParam.travelTime[h][branching.branchFrom][branching.branchTo] = userParam.verybig;
                }
            } else {
                // impose this edge (in this direction)
                // associate a very large and unattractive distance to all edges
                // starting from "branchFrom" excepted the one leading to "branchTo"
                // and excepted when we start from depot (several vehicles)
                if (branching.branchFrom != 0) {
                    for (i = 0; i < branching.branchTo; i++){
                        userParam.dist[branching.branchFrom][i] = userParam.verybig;
                        userParam.Phi[branching.branchFrom][i] = userParam.verybig;
                        userParam.gamma[branching.branchFrom][i] = userParam.verybig;
                        userParam.epsilon[branching.branchFrom][i] = userParam.verybig;
                        userParam.beta[branching.branchFrom][i] = userParam.verybig;
                        for (int h  = 0; h < userParam.speed.length; h++) {
                            userParam.travelTime[h][branching.branchFrom][i] = userParam.verybig;
                        }
                    }
                    for (i++; i < userParam.nbClients + 2; i++){
                        userParam.dist[branching.branchFrom][i] = userParam.verybig;
                        userParam.Phi[branching.branchFrom][i] = userParam.verybig;
                        userParam.gamma[branching.branchFrom][i] = userParam.verybig;
                        userParam.epsilon[branching.branchFrom][i] = userParam.verybig;
                        userParam.beta[branching.branchFrom][i] = userParam.verybig;
                        for (int h  = 0; h < userParam.speed.length; h++) {
                            userParam.travelTime[h][branching.branchFrom][i] = userParam.verybig;
                        }
                    }

                }
                // associate a very large and unattractive distance to all edges ending
                // at "branchTo" excepted the one starting from "branchFrom"
                // and excepted when the destination is the depot (several vehicles)
                if (branching.branchTo != userParam.nbClients + 1) {
                    for (i = 0; i < branching.branchFrom; i++){
                        userParam.dist[i][branching.branchTo] = userParam.verybig;
                        userParam.Phi[i][branching.branchTo] = userParam.verybig;
                        userParam.gamma[i][branching.branchTo] = userParam.verybig;
                        userParam.epsilon[i][branching.branchTo] = userParam.verybig;
                        userParam.beta[i][branching.branchTo] = userParam.verybig;
                        for (int h  = 0; h < userParam.speed.length; h++) {
                            userParam.travelTime[h][i][branching.branchTo] = userParam.verybig;
                        }
                    }
                    for (i++; i < userParam.nbClients + 2; i++){
                        userParam.dist[i][branching.branchTo] = userParam.verybig;
                        userParam.Phi[i][branching.branchTo] = userParam.verybig;
                        userParam.gamma[i][branching.branchTo] = userParam.verybig;
                        userParam.epsilon[i][branching.branchTo] = userParam.verybig;
                        userParam.beta[i][branching.branchTo] = userParam.verybig;
                        for (int h  = 0; h < userParam.speed.length; h++) {
                            userParam.travelTime[h][i][branching.branchTo] = userParam.verybig;
                        }
                    }
                }
                // forbid the edge in the opposite direction
                userParam.dist[branching.branchTo][branching.branchFrom] = userParam.verybig;
                userParam.Phi[branching.branchTo][branching.branchFrom] = userParam.verybig;
                userParam.gamma[branching.branchTo][branching.branchFrom] = userParam.verybig;
                userParam.epsilon[branching.branchTo][branching.branchFrom] = userParam.verybig;
                userParam.beta[branching.branchTo][branching.branchFrom] = userParam.verybig;
                for (int h  = 0; h < userParam.speed.length; h++) {
                    userParam.travelTime[h][branching.branchTo][branching.branchFrom] = userParam.verybig;
                }
            }
            if (recur) EdgesBasedOnBranching(userParam, branching.father, true);
        }
    }

    public boolean BBNode(Parameter userParam, ArrayList<Route> routes,
                          treeBB branching, ArrayList<Route> bestRoutes, int depth,long start)
            throws IOException {
        // userParam (input) : all the parameters provided by the users (cities,
        // roads...)
        // routes (input) : all (but we could decide to keep only a subset) the
        // routes considered up to now (to initialize the Column generation process)
        // branching (input): BB branching context information for the current node
        // to process (branching edge var, branching value, branching from...)
        // bestRoutes (output): best solution encountered
        int i, j, bestEdge1, bestEdge2, prevcity, city, bestVal;
        double coef, bestObj, change, CGobj;
        boolean feasible;
        try {
            long now = System.currentTimeMillis();
            // check first that we need to solve this node. Not the case if we have
            // already found a solution within the gap precision
            if ((upperbound - lowerbound) / upperbound < userParam.gap || (now-start)>= userParam.TimeLimit)
                return true;

            // init
            if (branching == null) { // root node - first call
                // first call - root node
                ArrayList<Route> tempRoutes2 = new ArrayList<>();
                for (int k = 0; k < userParam.Clients.length; k++) {
                    ArrayList<Integer> temp = new ArrayList<Integer>(1);
                    temp.add(userParam.Clients[k]);
                    Route tempRoute = new Route();
                    tempRoute.path = temp;
                    tempRoutes2.add(tempRoute);
                }
                Savings Save = new Savings();
                double tempBound = 0;
                tempRoutes2 = Save.savings(tempRoutes2,userParam);
                for (int l = 0; l < tempRoutes2.size(); l++) {
                    tempRoutes2.get(l).updateCost(userParam.travelTimeBase, userParam.varTheta,userParam.speed);
                    tempBound = tempBound+tempRoutes2.get(l).getCost();
                }
                upperbound = tempBound;
                treeBB newNode = new treeBB();
                newNode.father = null;
                newNode.toplevel = true;
                newNode.branchFrom = -1;
                newNode.branchTo = -1;
                newNode.branchValue = -1;
                newNode.son0 = null;
                newNode.Branchable = new boolean[userParam.nbClients+2][userParam.nbClients+2];
                for (int k = 0; k < userParam.nbClients+2; k++) {
                    for (int l = 0; l < userParam.nbClients+2; l++) {
                        newNode.Branchable[k][l] =true;
                    }
                }
                branching = newNode;

            }

            // display some local info
            if (branching.branchValue < 1) {
                System.out.println("\nEdge from " + branching.branchFrom + " to "
                        + branching.branchTo + ": forbid");
            } else {
                System.out.println("\nEdge from " + branching.branchFrom + " to "
                        + branching.branchTo + ": set");
            }
            int MB = 1024 * 1024;
            Runtime runtime = Runtime.getRuntime();
            System.out.print("Java Memory=> Total:" + (runtime.totalMemory() / MB)
                    + " Max:" + (runtime.maxMemory() / MB) + " Used:"
                    + ((runtime.totalMemory() - runtime.freeMemory()) / MB) + " Free: "
                    + runtime.freeMemory() / MB);

            // Compute a solution for this node using Column generation
            ColGen CG = new ColGen();

            CGobj = CG.computeColGen(userParam, routes,depth);
            branching.lowestValue = CGobj;
            // feasible ? Does a solution exist?
            if ((CGobj > 2 * userParam.maxlength) || (CGobj < -1e-6)) {
                // can only be true when the routes in the solution include forbidden edges (can happen when the BB set branching values)
                System.out.println("RELAX INFEASIBLE | Lower bound: " + lowerbound
                        + " | Upper bound: " + upperbound + " | Gap: "
                        + ((upperbound - lowerbound) / upperbound) + " | BB Depth: "
                        + depth + " | " + routes.size() + " routes"+" time: "+(start-now)/1000+"s");
                return true; // stop this branch
            }



            // update the global lowerBound when required
            if ((branching.father != null) && (branching.father.son0 != null)
                    && branching.father.toplevel ) {
                // all nodes above and on the left have been processed=> we can compute
                // a new lowerBound
                lowerbound = Math.min(branching.lowestValue, branching.father.son0.lowestValue);
                branching.toplevel = true;
            } else if (branching.father == null ) {
                // root node
                lowerbound = CGobj;
            }

            if (branching.lowestValue > upperbound) {
                CG = null;
                System.out.println("CUT | Lower bound: " + lowerbound
                        + " | Upper bound: " + upperbound + " | Gap: "
                        + ((upperbound - lowerbound) / upperbound) + " | BB Depth: "
                        + depth + " | Local CG cost: " + CGobj + " | " + routes.size()
                        + " routes"+" time: "+(start-now)/1000+"s");
                return true; // cut this useless branch
            } else {
                // ///////////////////////////////////////////////////////////////////////////
                // check the (integer) feasibility. Otherwise search for a branching
                // variable
                feasible = true;
                bestEdge1 = -1;
                bestEdge2 = -1;
                bestObj = -1.0;
                bestVal = 0;

                // transform the path variable (of the CG model) into edges variables
                for (i = 0; i < userParam.nbClients + 2; i++) {
                    java.util.Arrays.fill(userParam.edges[i], 0.0);
                }
                for (Route r : routes) {
                    if (r.getQ() > 1e-6) {
                        // we consider only the routes in the current local solution
                        ArrayList<Integer> path = r.getPath(); // get back the sequence of
                        // cities (path for this route)
                        prevcity = 0;
                        for (i = 1; i < path.size(); i++) {
                            city = path.get(i);
                            userParam.edges[prevcity][city] += r.getQ(); // convert into edges
                            prevcity = city;
                        }
                    }
                }

                // find a fractional edge
                for (i = 0; i < userParam.nbClients + 2; i++) {
                    for (j = 0; j < userParam.nbClients + 2; j++) {
                        coef = userParam.edges[i][j];
                            /*if(0<coef&&coef<1){
                                System.out.println("i:"+i+",j:"+j+"coef:"+coef);
                            }
                            if(coef>1){
                                System.out.println("i:"+i+",j:"+j+",coef:"+coef);
                            }*/
                        if ((coef > 1e-6) && ((coef < 0.99999999999) || (coef > 1.0000000001))) {
                            //if ((coef > 1e-6) && ((coef < 1))) {
                            // this route has a fractional coefficient in the solution =>
                            // should we branch on this one?
                            feasible = false;
                            // what if we impose this route in the solution? Q=1
                            // keep the ref of the edge which should lead to the largest change
                            change = Math.min(coef, Math.abs(1.0 - coef));
                            change *= routes.get(i).getCost();
                            //if ((change > bestObj)  ) {
                            if ((change > bestObj) && branching.Branchable[i][j] ) {
                                bestEdge1 = i;
                                bestEdge2 = j;
                                bestObj = change;
                                bestVal = (Math.abs(1.0 - coef) > coef) ? 0 : 1;
                            }
                        }
                    }
                }

                if (feasible) {
                    if (branching.lowestValue < upperbound) { // new incumbant feasible solution!
                        upperbound = branching.lowestValue;
                        bestRoutes.clear();
                        for (Route r : routes) {
                            if (r.getQ() > 1e-6) {
                                Route optim = new Route();
                                optim.setCost(r.getCost());
                                optim.path = r.getPath();
                                optim.speedVec = r.getSpeedVec();
                                optim.setQ(r.getQ());
                                optim.setRechargeTime(r.rechargeTime);
                                bestRoutes.add(optim);
                            }
                        }
                        System.out.println("OPT | Lower bound: " + lowerbound
                                + " | Upper bound: " + upperbound + " | Gap: "
                                + ((upperbound - lowerbound) / upperbound) + " | BB Depth: "
                                + depth + " | Local CG cost: " + CGobj + " | " + routes.size()
                                + " routes"+" time: "+(start-now)/1000+"s");
                        System.out.flush();
                    } else {
                        System.out.println("FEAS | Lower bound: " + lowerbound
                                + " | Upper bound: " + upperbound + " | Gap: "
                                + ((upperbound - lowerbound) / upperbound) + " | BB Depth: "
                                + depth + " | Local CG cost: " + CGobj + " | " + routes.size()
                                + " routes"+" time: "+(start-now)/1000+"s");
                    }
                    return true;
                } else {
                    System.out.println("INTEG INFEAS | Lower bound: " + lowerbound
                            + " | Upper bound: " + upperbound + " | Gap: "
                            + ((upperbound - lowerbound) / upperbound) + " | BB Depth: "
                            + depth + " | Local CG cost: " + CGobj + " | " + routes.size()
                            + " routes"+" time: "+(start-now)/1000+"s");
                    System.out.flush();
                    // ///////////////////////////////////////////////////////////
                    // branching (diving strategy)

                    // first branch -> set edges[bestEdge1][bestEdge2]=0
                    // record the branching information in a tree list
                    treeBB newNode1 = new treeBB();
                    newNode1.father = branching;
                    newNode1.branchFrom = bestEdge1;
                    newNode1.branchTo = bestEdge2;
                    newNode1.branchValue = bestVal; // first version was not with bestVal
                    // but with 0
                    newNode1.lowestValue = -1E10;
                    newNode1.son0 = null;
                    branching.Branchable[bestEdge1][bestEdge2] =false;
                    boolean[][] newBranchable = new boolean[userParam.nbClients+2][userParam.nbClients+2];
                    for (int k = 0; k < userParam.nbClients+2; k++) {
                        System.arraycopy(branching.Branchable[k], 0, newBranchable[k], 0, userParam.nbClients + 2);
                    }
                    newNode1.Branchable = newBranchable;
                    newBranchable =null;
                    // branching on edges[bestEdge1][bestEdge2]=0

                    EdgesBasedOnBranching(userParam, newNode1, false);

                    // the initial lp for the CG contains all the routes of the previous
                    // solution less(去掉分支的边) the routes containing this arc
                    ArrayList<Route> nodeRoutes = new ArrayList<Route>();
                    for (Route r : routes) {
                        ArrayList<Integer> path = r.getPath();
                        boolean accept = true;
                        if (path.size() > 3) { // we must keep trivial routes
                            // Depot-City-Depot in the set to ensure
                            // feasibility of the CG
                            prevcity = 0;
                            for (j = 1; accept && (j < path.size()); j++) {
                                city = path.get(j);
                                if ((prevcity == bestEdge1) && (city == bestEdge2))
                                    accept = false;
                                prevcity = city;
                            }
                        }
                        if (accept) nodeRoutes.add(r);
                    }

                    boolean ok;
                    ok = BBNode(userParam, nodeRoutes, newNode1, bestRoutes, depth + 1,start);
                    nodeRoutes = null; // free memory
                    if (!ok) {
                        return false;
                    }

                    branching.son0 = newNode1;
                    if(newNode1.lowestValue<0){
                        System.out.println("////////////////////////////////////");
                        System.out.println(newNode1.father.lowestValue);
                    }

                    // second branch -> set edges[bestEdge1][bestEdge2]=1
                    // record the branching information in a tree list
                    treeBB newNode2 = new treeBB();
                    newNode2.father = branching;
                    newNode2.branchFrom = bestEdge1;
                    newNode2.branchTo = bestEdge2;
                    newNode2.branchValue = 1 - bestVal; // first version: always 1
                    newNode2.lowestValue = -1E10;
                    newNode2.son0 = null;
                    branching.Branchable[bestEdge1][bestEdge2] =false;
                    newBranchable = new boolean[userParam.nbClients+2][userParam.nbClients+2];
                    for (int k = 0; k < userParam.nbClients+2; k++) {
                        System.arraycopy(branching.Branchable[k], 0, newBranchable[k], 0, userParam.nbClients + 2);
                    }
                    newNode2.Branchable = newBranchable;
                    newBranchable = null;
                    // branching on edges[bestEdge1][bestEdge2]=1
                    // second branching=>need to reinitialize the dist matrix
                    for (i = 0; i < userParam.nbClients + 2; i++) {
                        System.arraycopy(userParam.distBase[i], 0, userParam.dist[i], 0,
                                userParam.nbClients + 2);
                        System.arraycopy(userParam.PhiBase[i],0,userParam.Phi[i],0,userParam.nbClients+2);
                        System.arraycopy(userParam.gammaBase[i],0,userParam.gamma[i],0,userParam.nbClients+2);
                        System.arraycopy(userParam.epsilonBase[i],0,userParam.epsilon[i],0,userParam.nbClients+2);
                        System.arraycopy(userParam.betaBase[i],0,userParam.beta[i],0,userParam.nbClients+2);
                        for (int h = 0; h < userParam.speed.length; h++) {
                            System.arraycopy(userParam.travelTimeBase[h][i],0,userParam.travelTime[h][i],0,userParam.nbClients+2);
                        }
                    }
                    //reinitialize了因此需要recur递归一下
                    EdgesBasedOnBranching(userParam, newNode2, true);
                    // the initial lp for the CG contains all the routes of the previous
                    // solution less the routes incompatible with this arc
                    ArrayList<Route> nodeRoutes2 = new ArrayList<Route>();
                    for (Route r : routes) {
                        ArrayList<Integer> path = r.getPath();
                        boolean accept = true;
                        if (path.size() > 3) { // we must keep trivial routes
                            // Depot-City-Depot in the set to ensure
                            // feasibility of the CG
                            prevcity = 0;
                            for (i = 1; accept && (i < path.size()); i++) {
                                city = path.get(i);
                                if (userParam.dist[prevcity][city] >= userParam.verybig - 1E-6) accept = false;
                                prevcity = city;
                            }
                        }
                        if (accept) nodeRoutes2.add(r);
                    }
                    ok = BBNode(userParam, nodeRoutes2, newNode2, bestRoutes, depth + 1,start);
                    nodeRoutes2 = null;

                    // update lowest feasible value of this node
                    branching.lowestValue = Math.min(newNode1.lowestValue, newNode2.lowestValue);

                    return ok;
                }
            }


        } catch (IOException e) {
            System.err.println("Error: " + e);
        }
        return false;
    }

}
