import java.util.ArrayList;

public class TransitionArr {

    private State current;
    private ArrayList<String> input = new ArrayList<>();
    private State next;

    public TransitionArr(State c, String i, State n){
        current = c;
        input.add(i);
        next = n;
    }

    public TransitionArr(State c, ArrayList<String> i, State n){
        current = c;
        input.addAll(i);
        next = n;
    }

    public TransitionArr(){    }

    public State getCurrent() {
        return current;
    }

    public ArrayList<String> getInput() {
        return input;
    }

    public State getNext() {
        return next;
    }

    public void setCurrent(State c) {
        current = c;
    }

    public void setInput(ArrayList <String> i) {
        input = i;
    }

    public void setInput(String i) {
        input.clear();
        input.add(i);
    }

    public void addInput(String in) {
        input.add(in);
    }

    public void setNext(State n) {
        next = n;
    }

    public void set(State c, ArrayList<String> i, State n){
        this.setCurrent(c);
        this.setInput(i);
        this.setNext(n);
    }

    public void set(State c, String i, State n){
        this.setCurrent(c);
        this.setInput(i);
        this.setNext(n);
    }

    public boolean checkChar(String c){
        boolean pres = false;
        int i = 0;
        while(!pres && i < this.input.size()-1){
            pres = this.input.get(i).contains(c);
            i++;
        }
        return pres;
    }

    public void showTransition(){
        System.out.println("Current:"+getCurrent().getState());
        System.out.print("Input: ");
        for(int i = 0; i < getInput().size(); i++){
            System.out.println(getInput().get(i));
        }
        System.out.println("Next:"+getNext().getState());
    }

    public String transitionStructure(int i){
        String tr = new String();
        tr = "{\n\"transition\" : \""+i+"\",\n";
        tr = tr+"\"current\" : \""+this.current.getState()+"\",\n";
        tr = tr+"\"input\" : \"";
        for(int j = 0; j <input.size(); j++){
            tr = tr+input.get(j);
        }
        tr = tr+"\",\n";
        tr = tr+"\"next\": \""+this.next.getState()+"\"\n}\n";

        return tr;
    }

}
