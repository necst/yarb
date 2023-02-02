import java.util.ArrayList;

public class States {
    private ArrayList<State> states = new ArrayList<>();

    public States(ArrayList<State> s){
        states.addAll(s);
    }

    public States(){    }

    public boolean labelIsPresent(String s){
        for(State st: states){
            if(st.getState().equals(s)){
                //System.out.println("labelIsPresent true");
                return true;
            }
        }
        //System.out.println("labelIsPresent false");
        return false;
    }

    public boolean stateIsPresent(State s){
        for(State st: states){
            if(st.getState().equals(s.getState())){
                return true;
            }
        }
        return false;
    }

    public void add(State s){
        states.add(s);
    }

    public void addAll(States s){
        states.addAll(s.getAll());
    }

    public void addAll(ArrayList<State> s){
        states.addAll(s);
    }

    public void remove(int i){
        states.remove(i);
    }

    public void removeById(String id){
        States temp = new States(states);
        for(int i = 0; i <temp.size(); i++){
            if(temp.get(i).getState().equals(id)){
                states.remove(i);
            }
        }
    }

    public void removeByState(State s){
        States temp = new States(states);
        for(int i = 0; i <temp.size(); i++){
            if(temp.get(i).getState().equals(s.getState())){
                states.remove(i);
            }
        }
    }

    public void removeStates(States s){
        //States temp = new States(states);
        for(State st: s.getAll()){
            if(this.stateIsPresent(st)){
                this.removeByState(st);
            }
        }
    }

    public State get(int i){
        return states.get(i);
    }

    public State getById(String id) {
        return states.get(this.getIndexLabel(id));
    }

    public State getById(State s) {
        return states.get(this.getIndexLabel(s.getState()));
    }

    public ArrayList<State> getAll(){
        return states;
    }

    public Integer size(){
        return states.size();
    }

    public void clear(){
        states.clear();
        states.removeAll(states);
    }

    public State last(){
        return states.get(states.size() - 1);
    }

    //returns the index of the state with label id, returns -1 if not present
    public Integer getIndexLabel(String id) {
        int indexOfLast = -1;
        for(int i = 0; i < states.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(states.get(i).getState().equals(id)){
                indexOfLast = i;
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }

    public Integer getIndexState(State s) {
        int indexOfLast = -1;
        for(int i = 0; i < states.size(); i++){
            //System.out.println("Final states: "+states.get(i).getState());
            if(states.get(i).getState().equals(s.getState())){
                indexOfLast = i;
                //System.out.println("State considered: "+states.get(i).getState());
                //System.out.println("Id to find: "+id);
            }
        }
        return indexOfLast;
    }

    public void showStates(){
        System.out.println("States present: ");
        for(State s: states){
            System.out.println(s.getState());
        }
    }

    public void showStatesLine(){
        System.out.print("States present: ");
        for(State s: states){
            System.out.print(s.getState()+", ");
        }
        System.out.println("\n");
    }
}
