import java.util.ArrayList;

public class Transitions {
    private ArrayList<Transition> transitions = new ArrayList<>();

    public Transitions(ArrayList<Transition> t){
        transitions.addAll(t);
    }

    public Transitions(Transitions t){
        for(Transition tr: t.getAll()){
            transitions.add(tr);
        }
    }

    public Transitions(){    }

    public ArrayList<Transition> getTransitions(){
        return transitions;
    }

    public boolean labelIsPresent(String s){
        for(Transition t: transitions){
            if(t.getInput().equals(s)){
                //System.out.println("labelIsPresent true");
                return true;
            }
        }
        //System.out.println("labelIsPresent false");
        return false;
    }

    public boolean currentIsPresent(State s){
        for(Transition t: transitions){
            if(t.getCurrent().getState().equals(s.getState())){
                return true;
            }
        }
        return false;
    }

    public boolean nextIsPresent(State s){
        for(Transition t: transitions){
            if(t.getNext().getState().equals(s.getState())){
                return true;
            }
        }
        return false;
    }
/*
    public Transition transitionWithCurrent(State s){
        for(Transition t: transitions){
            if(t.getCurrent().equals(s)){
                return t;
            }
        }
        return null;
    }
*/
    public void add(Transition t){
        transitions.add(t);
    }

    public void add(int i, Transition t){
        transitions.add(i, t);
    }

    public void addAll(Transitions ts){
        transitions.addAll(ts.getAll());
    }

    public void remove(int i){
        transitions.remove(i);
    }

    public boolean remove(Transition t){
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
        for(Transition t: this.getById(id).getAll()){
            transitions.remove(t);
        }
    }

    public void removeCurr(State s){
        Transitions temp = new Transitions(transitions);
        for(Transition t: temp.getAll()){
            if(t.getCurrent().getState().equals(s.getState())) transitions.remove(t);
        }
    }

    public void removeCurr(String id){
        Transitions temp = new Transitions(transitions);
        for(Transition t: temp.getAll()){
            if(t.getCurrent().getState().equals(id)) transitions.remove(t);
        }
    }

    //remove all the transitions with curr or next state equal to s
    public void removeByState(State s){
        for(Transition t: this.getById(s.getState()).getAll()){
            transitions.remove(t);
        }
    }

    public void removeNext(String id){
        Transitions temp = new Transitions(transitions);
        for(Transition t: temp.getAll()){
            if(t.getNext().getState().equals(id)) transitions.remove(t);
        }
        }

    public void removeNext(State s){
        Transitions temp = new Transitions(transitions);
        for(Transition t: temp.getAll()){
            if(t.getNext().getState().equals(s.getState())) transitions.remove(t);
        }
    }

    public Transition get(int i){
        return transitions.get(i);
    }

    //get all the transitions with id as curr or next state
    public Transitions getById(String id) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexState(id)){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public Transitions getById(State s) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexState(s.getState())){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    //returns all the transitions that has curr state with name "id"
    public Transitions getCurr(String id) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexCurr(id)){
                tr.add(transitions.get(i));
            }
        return tr;
    }
    public Transitions getCurr(State s) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexCurr(s.getState())){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public Transitions getCurrNoLoop(String id) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexCurr(id)){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }
    public Transitions getCurrNoLoop(State s) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexCurr(s.getState())){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }


    //returns all the transitions that has next state with name "id"
    public Transitions getNext(String id) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexNext(id)){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public Transitions getNext(State s) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexNext(s.getState())){
                tr.add(transitions.get(i));
            }
        return tr;
    }

    public Transitions getNextNoLoop(String id) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexNext(id)){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }

    public Transitions getNextNoLoop(State s) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexNext(s.getState())){
                if(transitions.get(i).getCurrent() != transitions.get(i).getNext()){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }

    public Transitions getLoop(String id) {
        Transitions tr = new Transitions();
            for(int i : this.getIndexCurr(id)){
                if(transitions.get(i).getCurrent().equals(transitions.get(i).getNext())){
                    tr.add(transitions.get(i));
                }
            }
        return tr;
    }
    public Transitions getLoop(State s) {
        Transitions tr = new Transitions();
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

    public boolean isPresent(Transition t){
        for(Transition tr : transitions){
            if(tr.getCurrent().getState().equals(t.getCurrent().getState()) && tr.getInput().equals(t.getInput()) && tr.getNext().getState().equals(t.getNext().getState())){
                return true;
            }
        }
        return false;
    }

    public boolean isPresent(State s, String i, State n){
        for(Transition tr : transitions){
            if(tr.getCurrent().getState().equals(s.getState()) && tr.getInput().equals(i) && tr.getNext().getState().equals(n.getState())){
                return true;
            }
        }
        return false;
    }
    public boolean isPresent(String s, String i, String n){
        for(Transition tr : transitions){
            if(tr.getCurrent().getState().equals(s) && tr.getInput().equals(i) && tr.getNext().getState().equals(n)){
                return true;
            }
        }
        return false;
    }

    //returns the arrayList of transitions (used woth iterators)
    public ArrayList<Transition> getAll(){
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
    public Transition last(){
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
        for(Transition t: transitions){
            t.showTransition();
        }
    }

    public void showStatesT(){
        System.out.println("States of transitions present: ");
        for(Transition t: transitions){
            t.showStates();
        }
    }


    
}
