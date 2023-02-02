public class Transition {

    private State current;
    private String input;
    private State next;

    public Transition(State c, String i, State n){
        current = c;
        input = i;
        next = n;
    }

    public Transition(){    }

    public State getCurrent() {
        return current;
    }

    public String getInput() {
        return input;
    }

    public State getNext() {
        return next;
    }

    public void setCurrent(State current) {
        this.current = current;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setNext(State next) {
        this.next = next;
    }

    public void set(State c, String i, State n){
        this.setCurrent(c);
        this.setInput(i);
        this.setNext(n);
    }

    public void showTransition(){
        System.out.println("Current:"+getCurrent().getState());
        System.out.println("Input:"+getInput());
        System.out.println("Next:"+getNext().getState());
    }

    public void showStates(){
        System.out.println("Current:"+getCurrent().getState()+" Next:"+getNext().getState());
    }

    public String transitionStructure(int i){
        String tr = new String();
        tr = "{\n\"transition\" : \""+i+"\",\n";
        tr = tr+"\"current\" : \""+this.current.getState()+"\",\n";
        tr = tr+"\"input\" : \""+this.input+"\",\n";
        tr = tr+"\"next\": \""+this.next.getState()+"\"\n}\n";

        return tr;
    }

    public boolean areEqual(Transition t){
        if(t.getCurrent().getState().equals(this.getCurrent().getState()) && t.getNext().getState().equals(this.getNext().getState()) && t.getInput().equals(this.getInput())){
            return true;
        } else {
            return false;
        }
    }

}
