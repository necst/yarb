import java.util.ArrayList;

public class TransitionsArr {
    private ArrayList<TransitionArr> transitions = new ArrayList<>();

    public TransitionsArr(ArrayList<TransitionArr> t){
        transitions.addAll(t);
    }

    public TransitionsArr(){    }

    public ArrayList<TransitionArr> getTransitions(){
        return transitions;
    }

    public boolean labelIsPresent(String s){
        for(TransitionArr t: transitions){
            if(t.getInput().size() == 1 && t.getInput().get(0).equals(s)){
                //System.out.println("labelIsPresent true");
                return true;
            }
        }
        //System.out.println("labelIsPresent false");
        return false;
    }

    public boolean labelIsPresent(ArrayList<String> s){
        for(TransitionArr t: transitions){
            if(t.getInput().size() == s.size()){
                boolean equal = true;
                int i = 0;
                while(equal && i < t.getInput().size()-1){
                    if(!t.getInput().get(i).equals(s.get(i))){
                        equal = false;
                    }
                    i++;
                }
                if(equal) return true;  
                //System.out.println("labelIsPresent true");
            }
        }
        //System.out.println("labelIsPresent false");
        return false;
    }

    public boolean currentIsPresent(State s){
        for(TransitionArr t: transitions){
            if(t.getCurrent().getState().equals(s.getState())){
                return true;
            }
        }
        return false;
    }

    public boolean nextIsPresent(State s){
        for(TransitionArr t: transitions){
            if(t.getNext().getState().equals(s.getState())){
                return true;
            }
        }
        return false;
    }
/*
    public TransitionArr transitionWithCurrent(State s){
        for(TransitionArr t: transitions){
            if(t.getCurrent().equals(s)){
                return t;
            }
        }
        return null;
    }
*/
    public void add(TransitionArr t){
        transitions.add(t);
    }

    public void add(int i, TransitionArr t){
        transitions.add(i, t);
    }

    public void addAll(TransitionsArr ts){
        transitions.addAll(ts.getAll());
    }

    public void remove(int i){
        transitions.remove(i);
    }

    public boolean remove(TransitionArr t){
        for(int i = 0; i < transitions.size(); i++){
            if(transitions.get(i).getCurrent().getState().equals(t.getCurrent().getState()) && transitions.get(i).getInput().equals(t.getInput()) && transitions.get(i).getNext().getState().equals(t.getNext().getState())){
                transitions.remove(i);
                return true;
            }
        }
        return false;
    }

    //remove all the transitions with curr or next state with name "id"
    public void removeById(String id){
        //remove all the transitions with curr or next state with id as name
        for(TransitionArr t: this.getById(id).getAll()){
            transitions.remove(t);
        }
    }

    public void removeCurr(State s){
        TransitionsArr temp = new TransitionsArr(transitions);
        for(TransitionArr t: temp.getAll()){
            if(t.getCurrent().getState().equals(s.getState())) transitions.remove(t);
        }
    }

    public void removeCurr(String id){
        TransitionsArr temp = new TransitionsArr(transitions);
        for(TransitionArr t: temp.getAll()){
            if(t.getCurrent().getState().equals(id)) transitions.remove(t);
        }
    }

    //remove all the transitions with curr or next state equal to s
    public void removeByState(State s){
        for(TransitionArr t: this.getById(s.getState()).getAll()){
            transitions.remove(t);
        }
    }

    public void removeNext(String id){
        TransitionsArr temp = new TransitionsArr(transitions);
        for(TransitionArr t: temp.getAll()){
            if(t.getNext().getState().equals(id)) transitions.remove(t);
        }
        }

    public void removeNext(State s){
        TransitionsArr temp = new TransitionsArr(transitions);
        for(TransitionArr t: temp.getAll()){
            if(t.getNext().getState().equals(s.getState())) transitions.remove(t);
        }
    }

    public TransitionArr get(int i){
        return transitions.get(i);
    }

    //get all the transitions with id as curr or next state
    public TransitionsArr getById(String id) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexState(id)){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public TransitionsArr getById(State s) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexState(s.getState())){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    //returns all the transitions that has curr state with name "id"
    public TransitionsArr getCurr(String id) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexCurr(id)){
                tr.add(transitions.get(i));
            }
        return tr;
    }
    public TransitionsArr getCurr(State s) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexCurr(s.getState())){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public TransitionsArr getCurrNoLoop(String id) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexCurr(id)){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }
    public TransitionsArr getCurrNoLoop(State s) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexCurr(s.getState())){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }


    //returns all the transitions that has next state with name "id"
    public TransitionsArr getNext(String id) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexNext(id)){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public TransitionsArr getNext(State s) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexNext(s.getState())){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public TransitionsArr getNextNoLoop(String id) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexNext(id)){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }

    public TransitionsArr getNextNoLoop(State s) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexNext(s.getState())){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }

    public TransitionsArr getLoop(String id) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexCurr(id)){
                if(transitions.get(i).getCurrent().equals(transitions.get(i).getNext())){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }
    public TransitionsArr getLoop(State s) {
        TransitionsArr tr = new TransitionsArr();
            for(int i : this.getIndexCurr(s.getState())){
                if(transitions.get(i).getCurrent().equals(transitions.get(i).getNext())){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }

    public boolean isCurr(String id){
        if(this.getCurr(id).size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean isCurr(State s){
        if(this.getCurr(s.getState()).size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean isNext(String id){
        if(this.getNext(id).size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean isNext(State s){
        if(this.getNext(s.getState()).size() > 0){
            return true;
        } else {
            return false;
        }
    }

    public boolean isPresent(TransitionArr t){
        for(TransitionArr tr : transitions){
            if(tr.getCurrent().getState().equals(t.getCurrent().getState()) && tr.getInput().equals(t.getInput()) && tr.getNext().getState().equals(t.getNext().getState())){
                return true;
            }
        }
        return false;
    }

    public boolean isPresent(State s, String i, State n){
        for(TransitionArr tr : transitions){
            if(tr.getCurrent().getState().equals(s.getState()) && tr.getInput().size() == 1 && tr.getNext().getState().equals(n.getState())){
                if(tr.getInput().get(0).equals(i)) return true;
            }
        }
        return false;
    }
    public boolean isPresent(String s, String i, String n){
        for(TransitionArr tr : transitions){
            if(tr.getCurrent().getState().equals(s) && tr.getInput().size() == 1 && tr.getNext().getState().equals(n)){
                if(tr.getInput().get(0).equals(i)) return true;
            }
        }
        return false;
    }

    public boolean isPresent(State s, ArrayList<String> i, State n){
        for(TransitionArr tr : transitions){
            if(tr.getCurrent().getState().equals(s.getState()) && tr.getInput().size() == i.size() && tr.getNext().getState().equals(n.getState())){
                boolean equal = true;
                int j = 0;
                while(equal && j < tr.getInput().size()-1){
                    if(!tr.getInput().get(j).equals(i.get(j))) equal = false;
                    j++;
                }
                if(equal) return true;
            }
        }
        return false;
    }
    public boolean isPresent(String s, ArrayList<String> i, String n){
        for(TransitionArr tr : transitions){
            if(tr.getCurrent().getState().equals(s) && tr.getInput().size() == i.size() && tr.getNext().getState().equals(n)){
                boolean equal = true;
                int j = 0;
                while(equal && j < tr.getInput().size()-1){
                    if(!tr.getInput().get(j).equals(i.get(j))) equal = false;
                    j++;
                }
                if(equal) return true;
            }
        }
        return false;
    }

    //returns the arrayList of transitions (used woth iterators)
    public ArrayList<TransitionArr> getAll(){
        return transitions;
    }

    public Integer size(){
        return transitions.size();
    }

    public void clear(){
        transitions.clear();
        transitions.removeAll(transitions);
    }

    //returns the last transition present in transitions
    public TransitionArr last(){
        return transitions.get(transitions.size() - 1);
    }

    //returns the index of the transitions with "id" as curr or next state
    public ArrayList<Integer> getIndexState(String id) {
        ArrayList<Integer> indexOfLast = new ArrayList<>();
        for(int i = 0; i < transitions.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(transitions.get(i).getCurrent().getState().equals(id) || transitions.get(i).getNext().getState().equals(id)){
                indexOfLast.add(i);
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }
    //returns the index of the transitions with s as curr or next state
    public ArrayList<Integer> getIndexState(State s) {
        ArrayList<Integer> indexOfLast = new ArrayList<>();
        for(int i = 0; i < transitions.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(transitions.get(i).getCurrent().getState().equals(s.getState()) || transitions.get(i).getNext().getState().equals(s.getState())){
                indexOfLast.add(i);
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }

    //returns the index of the transitions with curr state with name "id"
    public ArrayList<Integer> getIndexCurr(String id) {
        ArrayList<Integer> indexOfLast = new ArrayList<>();
        for(int i = 0; i < transitions.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(transitions.get(i).getCurrent().getState().equals(id)){
                indexOfLast.add(i);
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }

    //returns the index of the transitions with s as curr state
    public ArrayList<Integer> getIndexCurr(State s) {
        ArrayList<Integer> indexOfLast = new ArrayList<>();
        for(int i = 0; i < transitions.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(transitions.get(i).getCurrent().getState().equals(s.getState())){
                indexOfLast.add(i);
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }

    //returns the index of the transitions with next state with name "id"
    public ArrayList<Integer> getIndexNext(String id) {
        ArrayList<Integer> indexOfLast = new ArrayList<>();
        for(int i = 0; i < transitions.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(transitions.get(i).getNext().getState().equals(id)){
                indexOfLast.add(i);
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }

    //returns the index of the transitions with s as next state
    public ArrayList<Integer> getIndexNext(State s) {
        ArrayList<Integer> indexOfLast = new ArrayList<>();
        for(int i = 0; i < transitions.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(transitions.get(i).getNext().getState().equals(s.getState())){
                indexOfLast.add(i);
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    } 

    //print the transitions present in the arrayList transitions
    public void showTransitions(){
        System.out.println("Transitions present: ");
        for(TransitionArr t: transitions){
            t.showTransition();
        }
    }


    
}
