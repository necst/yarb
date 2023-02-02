import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MinimizationArr {

    HashMap<State, Integer> passStates = new HashMap<State, Integer>();
    private int numPaths;

    public MinimizationArr(){  }

    public States orderStates(States states, TransitionsArr tr){
        //for each state in states, count the number of trasitions entering and exiting from the considered state, order the states with
        //the ones with the highest number of in/out-coming edges as last in the states array
        ArrayList<State> ordered = new ArrayList<>();
        for(State s: states.getAll()){
            //System.out.println("Considered state: "+s.getState());
            //System.out.println("Entering for on states");
            if(ordered.size() == 0){
                //System.out.println("First state added to states: "+s.getState());
                ordered.add(s);
            } else {
                int i = 0;
                    while(tr.getCurr(s).size()+tr.getNext(s).size() < tr.getCurr(ordered.get(i)).size()+tr.getNext(ordered.get(i)).size() && i < ordered.size()-1){
                        i++;
                        //System.out.println("In while, i: "+i);
                    }
                    if(tr.getCurr(s).size()+tr.getNext(s).size() >= tr.getCurr(ordered.get(i)).size()+tr.getNext(ordered.get(i)).size()){
                        ordered.add((i+1), s);
                        //System.out.println("Inserting state "+s.getState()+" in ordered at: "+(i+1));
                    }
            }
            if(states.size() == ordered.size()){
                //System.out.println("Same size of array of states");
                states.clear();
                states.addAll(ordered);
            } else {
                //System.out.println("Not same size of array of states");
            }

        }
        //states.showStates();
        return states;
    }

    public States orderSerial(States states, TransitionsArr  tr){
        //System.out.println("Start of serial minimization");
        States ordered = new States();
        //consider all the states
        for(State s: states.getAll()){
            //find the entering and exiting transitions from the considered states
            //check that there is only one entering transition and only one exiting transition
            //the states that satisfy this condition are eliminated as first
            if(tr.getNext(s).size() == 1 && tr.getCurr(s).size() == 1 && !tr.getNext(s).equals(tr.getCurr(s))){
                ordered.add(s);
            }
        }
        states.removeStates(ordered);
        ordered.addAll(states);      

        return ordered;
    }

    public States orderBridge(States states, TransitionsArr tr) {
        States ordered = new States();
        States lasts = new States();
        State start = states.getById("start");
        States traversed = new States();
        //map the states in the global array passStates with 0
        for(State s: states.getAll()){
            passStates.put(s, 0);
        }
        //call followPath with start state, all the transizions and the already traversed states (empty array)
        followPath(start, tr, traversed);
        //at the end of recursion of followPath, check for each state if their value in passStates is != null and if it is equal to the number of total paths
        //if the number is equal, the corresponding state is a bridge state
        //all the bridge states have to be put as last in the ordered list to be returned
        System.out.println("End of followPath recursion");
        //iterate over passStates
        for(Map.Entry<State, Integer> set : passStates.entrySet()){
            if(set.getKey().getState().equals("start") || set.getKey().getState().equals("end")){
                ordered.add(set.getKey());
            } else {
                if(set.getValue() != null && set.getValue() == numPaths){
                    lasts.add(set.getKey());
                } else {
                    ordered.add(set.getKey());
                }
            }
        }
        ordered.addAll(lasts);
        System.out.println("List of states before: ");
        states.showStates();
        System.out.println("List of states after: ");
        ordered.showStates();
        return ordered;
    }

    public States orderWeight(States states, TransitionsArr  tr) {
        //System.out.println("Start of weight minimization");
        //System.out.println("Transitions present: ");
        //tr.showTransitions();
        States ordered = new States();
        int m = 0;
        int l = 0;
        int inI = 0;
        int outJ = 0;
        int loop = 0;
        ArrayList<Integer> weights = new ArrayList<>();
        //scorrere tutti gli stati, per ogni stato calcolare la weight
        //salvare weight in array e ordinare gli stati con weight crescente
        for(State s: states.getAll()){
            inI = 0;
            outJ = 0;
            loop = 0;
            boolean notStartEnd = s.getState().equals("start") || s.getState().equals("end");
            //System.out.println("notstartend: "+notStartEnd);
            if(notStartEnd){
                weights.add(1000000);
            } else {
            //System.out.println("State considered: "+s.getState());
            m = tr.getNextNoLoop(s).size();
            //System.out.println("Number of entering transitions: "+m);
            l = tr.getCurrNoLoop(s).size();
            //System.out.println("Number of exiting transitions: "+l);
            //scorrere la lista di edges that enter into state s (no loops) e sommare la lunghezza delle labels
            for(TransitionArr t : tr.getNextNoLoop(s).getAll()){
                if(!t.getInput().get(0).equals("empty")){
                    for(int i = 0; i <t.getInput().size(); i++){
                        inI = inI + t.getInput().get(i).length();
                    }
                    
                }
            }
            //System.out.println("Sum of sizes of entering transition labels: "+inI);
            for(TransitionArr  t : tr.getCurrNoLoop(s).getAll()){
                if(!t.getInput().get(0).equals("empty")){
                    for(int i = 0; i <t.getInput().size(); i++){
                        outJ = outJ + t.getInput().get(i).length();
                    }
                    
                }
            }
            //System.out.println("Sum of sizes of exiting transition labels: "+outJ);
            for(TransitionArr  t : tr.getLoop(s).getAll()){
                for(int i = 0; i <t.getInput().size(); i++){
                    loop = loop + t.getInput().get(i).length();
                }
            }
            
            //System.out.println("Sum of sizes of loop transition labels: "+loop);
            int w = (l-1)*inI +(m-1)*outJ +(m*l - 1)*loop;
            //System.out.println("Weight of state "+s.getState()+": "+w);
            weights.add(w);
        }
        }


        if(weights.size() == states.size()){
            int min = weights.get(0);
            //System.out.println("Size of weight: "+weights.size());
            int position = 0;
            int exit = weights.size();
            while(exit > 0){
                min = weights.get(0);
                position = 0;
            for(int i = 0; i < weights.size(); i++){
                if(weights.get(i) < min){
                    min = weights.get(i);
                    position = i;
                }
            }
            ordered.add(states.get(position));
            states.remove(position);
            weights.remove(position);
            exit--;
            //System.out.println("Size of weights: "+weights.size()+" Size of states: "+states.size());
            //System.out.println("exit: "+exit);
        }
        } else {
            //System.out.println("Weights and states has different lenght.");
        }
        //System.out.println("Ordered list of states: ");
        //ordered.showStates();
        return ordered;
    }

    private void followPath(State s, TransitionsArr tr, States traversed){
        System.out.println("Start of followPath for state: "+s.getState());
        if(!s.getState().equals("end")){
            System.out.println("Not an end state");
            //if the state s is already been traversed (is in list of traversed states) set its value in passStates to "null"
            if(!traversed.stateIsPresent(s)){
                System.out.println("List of traversed states:");
                traversed.showStates();
                System.out.println("State "+s.getState()+" is not present in traversed");
                traversed.add(s);
            } else {
                System.out.println("List of traversed states:");
                traversed.showStates();
                System.out.println("State "+s.getState()+" is already present in traversed");
                passStates.replace(s, null);
            }
            //check if the value of s in the array passStates is not "null"
            if(passStates.get(s)!=null){
                System.out.println("Value of state "+s.getState()+"before incrementing: "+passStates.get(s));
                //during the recursion count the number of times each state is traversed
                int newV = passStates.get(s) + 1;
                passStates.replace(s, newV);
                System.out.println("Value of state "+s.getState()+"after incrementing: "+passStates.get(s));
                //pass all the transitions with curr state equal to s
                for(TransitionArr t : tr.getCurr(s).getAll()){
                    //call followPath for the next state of each of the transitions, (if next state is != from the current state s, because self-loop is accepted)
                    if(!t.getNext().getState().equals(s.getState())){
                        System.out.println("Calling followPath for next state "+t.getNext().getState());
                        followPath(t.getNext(), tr, traversed);
                    }
                }

            }
        } else {
            System.out.println("End state ("+s.getState()+") incrementing the numPathds, before: "+numPaths);
            //when an end state is traversed then increment the numPaths
            numPaths++;
            System.out.println("After: "+numPaths);
        }
    }

}
