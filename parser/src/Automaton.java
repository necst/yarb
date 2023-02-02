public class Automaton {
    private States states = new States();
    private States startStates = new States();
    private States endStates = new States();
    private Transitions transitions = new Transitions();
    
    public Automaton(boolean auto) {
        states.add(new State("0"));
        states.add(new State("1"));
        states.add(new State("2"));
        states.add(new State("3"));
        states.add(new State("4"));
        startStates.add(states.get(0));
        startStates.add(states.get(1));
        endStates.add(states.get(2));
        endStates.add(states.get(3));
        transitions.add(new Transition(states.get(0), "a", states.get(1)));
        transitions.add(new Transition(states.get(1), "b", states.get(2)));
        transitions.add(new Transition(states.get(1), "b", states.get(1)));
        transitions.add(new Transition(states.get(0), "b", states.get(2)));
        transitions.add(new Transition(states.get(0), "d", states.get(2)));
        transitions.add(new Transition(states.get(3), "c", states.get(0)));
        transitions.add(new Transition(states.get(3), "c", states.get(2)));
        transitions.add(new Transition(states.get(2), "c", states.get(4)));
        transitions.add(new Transition(states.get(1), "c", states.get(4)));

    }

    public Automaton(){

    }

    public Automaton(States start, States states, States end, Transitions tr) {
        setStartStates(start);
        setEndStates(end);
        setStates(states);
        setTransitions(tr);
    }

    public void showAutomaton(){
        for(Transition t: transitions.getAll()) {
            System.out.println("Current: "+t.getCurrent().getState()+" Input: "+t.getInput()+" Next: "+t.getNext().getState()); 
        }
        System.out.println("States of the automaton: ");
        for(State s: states.getAll()) {
            System.out.println(s.getState()); 
        }
        System.out.println("Start states of the automaton: ");
        for(State s: startStates.getAll()) {
            System.out.println(s.getState()); 
        }
        System.out.println("End states of the automaton: ");
        for(State s: endStates.getAll()) {
            System.out.println(s.getState()); 
        }
    }

    public void showAutomatonNoInput(){
        for(Transition t: transitions.getAll()) {
            System.out.println("Current: "+t.getCurrent().getState()+" Next: "+t.getNext().getState()); 
        }
        System.out.println("States of the automaton: ");
        for(State s: states.getAll()) {
            System.out.println(s.getState()); 
        }
        System.out.println("Start states of the automaton: ");
        for(State s: startStates.getAll()) {
            System.out.println(s.getState()); 
        }
        System.out.println("End states of the automaton: ");
        for(State s: endStates.getAll()) {
            System.out.println(s.getState()); 
        }
    }

    public States getStartStates(){
        return startStates;
    }

    public void setStates(States s) {
        states.clear();
        states.addAll(s);
    }

    public void setStartStates(States starts) {
        startStates.clear();
        startStates.addAll(starts);
    }

    public void setEndStates(States ends) {
        endStates.clear();
        endStates.addAll(ends);
    }
    
    public void setTransitions(Transitions tr) {
        transitions.clear();
        transitions.addAll(tr);
    }

    public void setAutomaton(States s, States starts, States ends, Transitions tr){
        states.clear();
        startStates.clear();
        endStates.clear();
        transitions.clear();
        states.addAll(s);
        startStates.addAll(starts);
        endStates.addAll(ends);
        transitions.addAll(tr);
    }

    public Transitions getTransitions(){
        return transitions;
    }

    public States getAllStates(){
        return states;
    }

    public State getState(int pos){
        return states.get(pos);
    }

    public States getEndStates() {
        return endStates;
    }

    public void removeTransition(int i){
        transitions.remove(i);
    }

    public void removeTransition(Transition t) {
        transitions.remove(t);
    }

    public void removeState(State s){
        States temp = new States(states.getAll());
        for(int i = 0; i < temp.size(); i++){
            if(temp.get(i).getState().equals(s.getState())) states.remove(i);
        }
    }

    public void addState(State s){
        states.add(s);
    }

    public void addStates(States s){
        states.addAll(s);
    }

    public void clearStates(){
        states.clear();
    }

    public void addStart(State s){
        startStates.add(s);
    }

    public void addStart(States s){
        startStates.addAll(s);
    }
    
    public void addEnd(State s){
        endStates.add(s);
    }

    public void addEnd(States s){
        endStates.addAll(s);
    }

    public void addTransition(Transition t){
        transitions.add(t);
    }

    public void addTransition(int i, Transition t){
        transitions.add(i, t);
    }

    public void addTransitions(Transitions t){
        transitions.addAll(t);
    }

    public void clearTransitions(){
        transitions.clear();
    }

    //return true if the start state is present
    public boolean checkStart(State s){
        if(startStates.labelIsPresent(s.getState())){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkStart(String s){
        if(startStates.labelIsPresent(s)){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkEnd(State s){
        if(endStates.labelIsPresent(s.getState())){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkEnd(String s){
        if(endStates.labelIsPresent(s)){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkState(State s){
        if(states.labelIsPresent(s.getState())){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkState(String s){
        if(states.labelIsPresent(s)){
            return true;
        } else {
            return false;
        }
    }

    public boolean checkTransition(Transition t){
        for(Transition tr : transitions.getAll()){
            if(tr.getCurrent().getState().equals(t.getCurrent().getState()) && tr.getInput().equals(t.getInput()) && tr.getNext().getState().equals(t.getNext().getState())){
                return true;
            } 
        }
        return false;
    }

    public boolean checkTransition(State curr, String input, State next){
        for(Transition tr : transitions.getAll()){
            if(tr.getCurrent().getState().equals(curr.getState()) && tr.getInput().equals(input) && tr.getNext().getState().equals(next.getState())){
                return true;
            } 
        }
        return false;
    }

    public boolean checkTransition(String curr, String input, String next){
        for(Transition tr : transitions.getAll()){
            if(tr.getCurrent().getState().equals(curr) && tr.getInput().equals(input) && tr.getNext().getState().equals(next)){
                return true;
            } 
        }
        return false;
    }

    public void clear(){
        states.clear();
        startStates.clear();
        endStates.clear();
        transitions.clear();
    }

    public String automatonStructure(int n){
        String structure = new String();
        structure = "{\"automaton\" : "+n+",\n\"states\" : [\n";
        for(int i = 0; i < states.size(); i++){
            if(i<states.size()-1) structure = structure+"\""+states.get(i).getState()+"\", ";
            else structure = structure+"\""+states.get(i).getState()+"\"\n";
        }
        structure = structure +"], \n\"start\" : [\n";
        for(int i = 0; i < startStates.size(); i++){
            if(i<startStates.size()-1) structure = structure+"\""+startStates.get(i).getState()+"\", ";
            else structure = structure+"\""+startStates.get(i).getState()+"\"\n";
        }
        structure = structure +"], \n\"end\" : [\n";
        for(int i = 0; i < endStates.size(); i++){
            if(i<endStates.size()-1) structure = structure+"\""+endStates.get(i).getState()+"\",";
            else structure = structure+"\""+endStates.get(i).getState()+"\"\n";
        }
        structure = structure +"], \n\"transitions\" : [\n";
        for(int i = 0; i <transitions.size(); i++){
            structure = structure+"{\n\"id\" : \""+i+"\",\n";
            structure = structure+"\"current\" : \""+transitions.get(i).getCurrent().getState()+"\",\n";
            structure = structure+"\"input\" : \""+transitions.get(i).getInput()+"\",\n";
            if(i<transitions.size()-1) structure = structure+"\"next\": \""+transitions.get(i).getNext().getState()+"\"\n},\n";
            else structure = structure+"\"next\": \""+transitions.get(i).getNext().getState()+"\"\n}\n";
        }
        structure = structure +"]\n}";
        return structure;
        
    }

    public String transitionsStructure(){
        String structure = new String();
        for(int i = 0; i <transitions.size(); i++){
            structure = structure +"{\"transition\" : "+i+", \n";
            structure = structure+"\"current\" : \""+transitions.get(i).getCurrent().getState()+"\",\n";
            structure = structure+"\"input\" : \""+transitions.get(i).getInput()+"\",\n";
            if(i<transitions.size()-1) structure = structure+"\"next\": \""+transitions.get(i).getNext().getState()+"\"\n},\n";
            else structure = structure+"\"next\": \""+transitions.get(i).getNext().getState()+"\"\n}\n";
        }
        return structure;
        
    }

    public String transitionsStructure(State s){
        String structure = new String();
        for(int i = 0; i <transitions.size(); i++){
            if(transitions.get(i).getCurrent().getState().equals(s.getState()) || transitions.get(i).getNext().getState().equals(s.getState())){
                structure = "{\"transition\" : "+i+", \n";
                structure = structure+"\"current\" : \""+transitions.get(i).getCurrent().getState()+"\",\n";
                structure = structure+"\"input\" : \""+transitions.get(i).getInput()+"\",\n";
                if(i<transitions.size()-1) structure = structure+"\"next\": \""+transitions.get(i).getNext().getState()+"\"\n},\n";
                else structure = structure+"\"next\": \""+transitions.get(i).getNext().getState()+"\"\n}\n";
            }
        }
        return structure;   
    }

}
