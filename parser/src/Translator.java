import java.io.File;
import java.util.ArrayList;

public class Translator {
    private static ArrayList<String> regex = new ArrayList<>();
    private ArrayList<String> failed = new ArrayList<>();
    private boolean first = true;
    private boolean end = false;
    private Transitions newTr = new Transitions();
    private Transitions trRemove = new Transitions();
    private Transitions tempTransitions;
    private Transition t = new Transition();
    private Transition temp = new Transition();
    private Transitions in = new Transitions();
    private Transitions out = new Transitions();
    private Automaton automa;
    private Automaton automaton = new Automaton();
    private Automaton cleaned;
    JSONReadFromFile read = new JSONReadFromFile();

    public Translator(){
        //a = automaton;
    }

    public ArrayList<String> translateToRegex(String suite, String bench, int num, String order, String mod, String path){
        try {
            automa = new Automaton();
            automa = read.getAutomaton(suite+"/automata/tempAutomaton_"+bench, num);
            
            System.out.println("Parsing..."+num);
            end = false;
            //boolean "clean" has to be passed when translateToRegex is called
                
            automa = modifyAutomata(automa, false);
            /* 
            if(order.equals("degree")){
                //System.out.println("Degree minimization");
                States tempSt = new States(automa.getAllStates().getAll());
                Minimization min = new Minimization();
                automa.clearStates();
                automa.addStates(min.orderStates(tempSt, automa.getTransitions()));
            } else if (order.equals("bridge")){
                //System.out.println("Bridge minimization");
                States tempSt = new States(automa.getAllStates().getAll());
                Minimization min = new Minimization();
                automa.clearStates();
                automa.addStates(min.orderSerial(tempSt, automa.getTransitions()));
            } else if (order.equals("weight")){
                //System.out.println("Weight minimization");
                States tempSt = new States(automa.getAllStates().getAll());
                Minimization min = new Minimization();
                automa.clearStates();
                System.out.println("Prova");
                automa.addStates(min.orderWeight(tempSt, automa.getTransitions()));
            }
            */
            automa = removeStates(automa, order, mod, path, num);

            if(automa==null){
                return null;
            } else {
                if(automa.getTransitions().size() == 1){
                    regex.clear();
                    regex.add(automa.getTransitions().get(0).getInput());
                } 
                else{
                    System.out.println("Transitions not of size 1");
                    System.out.println("Size of transitions: "+automa.getTransitions().size());
                    //System.out.println("Automaton at the end of Translator: ");
                    //automa.showAutomatonNoInput();
                    end(path, num+"");
                } 
            }
            
            //automa.showAutomaton();
            //automa = null;
            cleanAll();
            automa = null;
            return regex;
            //output.closeFile();
        } catch (Throwable e) {
            System.out.println("End in exception");
            e.printStackTrace();
            end(path, num+"");
            return null;
        }
            
    }

    private Automaton modifyAutomata(Automaton a, boolean clean) {
        automaton = new Automaton();
        
        //States start = new States();
        //States end = new States();
        //Transitions transitions = new Transitions();
        //transitions.addAll(a.getTransitions());
        //1. creare nuovo stato start (unico)
        //2. creare transition da nuovo start state a tutti i vecchi start states
        //3. aggiungere le nuove transitions alle transitions dell'automa
        //4. togliere tutti i vecchi start states da startStates
        //if(a.getStartStates().size() > 1){
        //Automaton cleaned;
        if(clean){
            cleaned = cleanAutomaton(a.getAllStates(), a.getTransitions(), a.getStartStates(), a.getEndStates());
        } else {
            cleaned = a;
        }
        //cleaned.showAutomaton();
        if(first){
            automaton.addStart(new State("start"));
            for(State s: cleaned.getStartStates().getAll()){
                automaton.addTransition(new Transition(automaton.getStartStates().getById("start"), "empty", s));
            }

            automaton.addTransitions(cleaned.getTransitions());
        //1. creare nuovo stato end (unico)
        //2. creare transition da tutti i vecchi end states verso il nuovo end state
        //3. aggiungere le nuove transitions alle transitions dell'automa
        //4. togliere tutti i vecchi start states da startStates
            automaton.addEnd(new State("end"));
            for(State s: cleaned.getEndStates().getAll()){
                automaton. addTransition(new Transition(s, "empty", automaton.getEndStates().getById("end")));
            }
        } else {
            automaton.addTransitions(cleaned.getTransitions());
        }
        
        //Se ci sono più transitions tra due stessi stati, sostituirle con una sola che presenta come Input l'unione (OR) tra i due valori di input
        tempTransitions = new Transitions();
        tempTransitions.addAll(automaton.getTransitions());
        while(tempTransitions.size()>0){
            //System.out.println("TempTransition: ");
            //tempTransitions.get(0).showTransition();
            for(int i = 0; i < automaton.getTransitions().size(); i++) {
            //scorriamo l'array, considerando ogni elemento
            //per ogni elemento cerchiamo nuovamente nell'array per cercare altri stati che presentano stesso current e next e "unire" gli input nella prima transizione, cancellando la seconda dalla lista
                if( ( tempTransitions.get(0).getCurrent().equals(automaton.getTransitions().get(i).getCurrent())) && ( tempTransitions.get(0).getNext().equals(automaton.getTransitions().get(i).getNext())) && ( !tempTransitions.get(0).getInput().equals(automaton.getTransitions().get(i).getInput()))){
                    //System.out.println("First transition to or: ");
                    //transitions.get(i).showTransition();
                    //System.out.println("Second transition to or: ");
                    //tempTransitions.get(0).showTransition();
                    t.set(automaton.getTransitions().get(i).getCurrent(), tempTransitions.get(0).getInput()+"|"+automaton.getTransitions().get(i).getInput(), automaton.getTransitions().get(i).getNext());
                    
                    temp.set(automaton.getTransitions().get(i).getCurrent(), automaton.getTransitions().get(i).getInput(), automaton.getTransitions().get(i).getNext());
                    //t.showTransition();
                    automaton.removeTransition(i);
                    automaton.addTransition(i, t);
                    
                    for(int j = 0; j < automaton.getTransitions().size(); j ++) {
                        if((automaton.getTransitions().get(j).getCurrent().equals(tempTransitions.get(0).getCurrent())) && (automaton.getTransitions().get(j).getInput().equals(tempTransitions.get(0).getInput())) && (automaton.getTransitions().get(j).getNext().equals(tempTransitions.get(0).getNext()))){
                            //System.out.println("Transition to remove: ");
                            //transitions.get(j).showTransition();
                            automaton.removeTransition(j);
                        }
                    }

                    //rimuove da tempTransition la transition uguale a quella considerata in transitions (così da evitare di ottenere lo stesso risultato due volte ma ribaltato: b|d e d|b) 
                    for(int j = 0; j < tempTransitions.size(); j++) {
                        if((temp.getCurrent().equals(tempTransitions.get(j).getCurrent())) && (temp.getInput().equals(tempTransitions.get(j).getInput())) && (temp.getNext().equals(tempTransitions.get(j).getNext()))){
                            //System.out.println("Transition in tempTransition to remove: ");
                            //tempTransitions.get(j).showTransition();
                            tempTransitions.remove(j);
                        }
                    }
                }
            }
            tempTransitions.remove(0);
        }
        
        States st = new States();
        st.addAll(cleaned.getAllStates());
        boolean endLoop = false;
        boolean addStart = true;
        boolean addEnd = true;
        while(!endLoop && addStart) {
            for(int i = 0; i < st.size(); i++){
                if(automaton.getStartStates().get(0).getState().equals(st.get(i).getState())){
                    addStart = false;
                }
            }
            endLoop = true;
        }
        endLoop = false;
        while(!endLoop && addEnd) {
            for(int i = 0; i < st.size(); i++){
                if(automaton.getEndStates().get(0).getState().equals(st.get(i).getState())){
                    addEnd = false;
                }
            }
            endLoop = true;
        }
        
        if(addStart){
            st.addAll(automaton.getStartStates());
        }
        if(addEnd){
            st.addAll(automaton.getEndStates());
        }
        automaton.clearStates();
        automaton.addStates(st);

        return automaton;
    }
    

    private Automaton removeStates(Automaton a, String order, String mod, String path, int num) {
        trRemove = new Transitions();
        trRemove.addAll(a.getTransitions());

        States startS = new States(a.getStartStates().getAll());
        States endS = new States(a.getEndStates().getAll());

        States st = new States();
        if(order.equals("degree")){
            //System.out.println("Degree minimization");
            Minimization min = new Minimization();
            st.addAll(min.orderStates(a.getAllStates(), a.getTransitions()));
        } else if (order.equals("serial")){
            //System.out.println("Bridge minimization");
            Minimization min = new Minimization();
            st.addAll(min.orderSerial(a.getAllStates(), a.getTransitions()));
        } else if (order.equals("bridge")){
            //System.out.println("Weight minimization");
            Minimization min = new Minimization();
            st.addAll(min.orderBridge(a.getAllStates(), a.getTransitions()));
            //System.out.println("End of orderBridge");
        }else if (order.equals("weight")){
            //System.out.println("Weight minimization");
            Minimization min = new Minimization();
            st.addAll(min.orderWeight(a.getAllStates(), a.getTransitions()));
        } else {
            st.addAll(a.getAllStates());
        }
        
        Transitions tempTr = new Transitions();
        Transitions loops = new Transitions();
        String loopLabel = new String();
        boolean ok;
        if(mod.equals("once")) ok = true;
        else if(mod.equals("iter")) ok = false;
        else ok = true;
        
        if(ok){
            int pos = 0;
            while(trRemove.size() > 1) {
                if(end){
                    return null;
                } else {
                    State s = st.get(pos);
                    System.out.println("Considered state: "+s.getState()+" automaton: "+num);
                    st.showStates();
                    System.out.println("Size of trRemove: "+trRemove.size());
                    //trRemove.showStatesT();
                    if(s.getState() != "start" && s.getState() != "end"){

                        //for each state: check if there is a loop
                        if(checkLoop(trRemove, s)){
                            //System.out.println("Enter checkloop for state "+s.getState());
                            loops.addAll(getLoop(trRemove, s));        
                            
                            while(loops.size() > 0) {
                                if(loops.size() == 1) {
                                    loopLabel = loopLabel + loops.get(0).getInput();
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                } else {
                                    loopLabel = loopLabel + loops.get(0).getInput()+"|";
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                }
                            }
                            loops.addAll(getLoop(trRemove, s));
                            if(loopLabel.length() == 1) {
                                if(loopLabel.equals("*")){
                                    loopLabel = "("+loopLabel+")*";
                                } else {
                                    loopLabel = loopLabel+"*";
                                }
                            } else if (loopLabel.length() > 1) {
                                loopLabel = "("+loopLabel+")*";
                            }

                            //delete incoming and outgoing transitions into/from considered state and create new transitions with merged labels
                            tempTr.clear();
                            tempTr.addAll(trRemove);
                            trRemove.clear();
                            trRemove.addAll(eliminateParallelTransitions(eliminateStateWithLoop(tempTr, loopLabel, s, path, num), path, num));
                            st.removeByState(s);
                        } else {
                            tempTr.clear();
                            tempTr.addAll(trRemove);
                            trRemove.clear();
                            trRemove.addAll(eliminateParallelTransitions(eliminateState(tempTr, s, path, num), path, num));
                            st.removeByState(s);

                        }
                    } else {
                        //System.out.println("Line 297");
                        if(st.size() > 2){
                            while((st.get(pos).getState().equals("start") || st.get(pos).getState().equals("end")) && pos < st.size()-1){
                                if(pos < st.size()) pos++;
                                else pos = 0;
                                //System.out.println("pos: "+pos);
                            }
                        } else {
                            trRemove = checkEqualTransitions(trRemove);
                            //try to add the checkloop thing, senza rimuovere lo stato (start or end che sia)
                            //mettere la distinzione tra start, end o non start/end con la distinzione che se non è start/end allora lo stato viene eliminato, se no si fa il parallel transition ma non si elimina lo stato
                                while(trRemove.size() >= 2){
                                    //System.out.println("line 398");
                                    //trRemove.showStatesT();
                                    tempTr.clear();
                                    tempTr.addAll(trRemove);
                                    trRemove.clear();
                                    trRemove.addAll(eliminateParallelTransitions(tempTr, path, num));
                                }
                            
                        }
                        
                        
                    }
                    loops.clear();
                }
                
                
            }
        } else {
            int pos = 0;
            while(trRemove.size() > 1){
                if(end){
                    return null;
                } else {
                    if(order.equals("degree")){
                        System.out.println("Degree minimization for automaton "+num);
                        Minimization min = new Minimization();
                        States tempS = new States(st.getAll());
                        st.clear();
                        st.addAll(min.orderStates(tempS, trRemove));
                        tempS.clear();
                    } else if (order.equals("serial")){
                        System.out.println("Serial minimization for automato "+num);
                        Minimization min = new Minimization();
                        States tempS = new States(st.getAll());
                        st.clear();
                        st.addAll(min.orderSerial(tempS, trRemove));
                        tempS.clear();
                    } else if (order.equals("bridge")){
                        System.out.println("Bridge minimization for automaton "+num);
                        Minimization min = new Minimization();
                        States tempS = new States(st.getAll());
                        st.clear();
                        st.addAll(min.orderBridge(tempS, trRemove));
                        tempS.clear();
                    } else if (order.equals("weight")){
                        System.out.println("Weight minimization for automaton "+num);
                        Minimization min = new Minimization();
                        States tempS = new States(st.getAll());
                        st.clear();
                        st.addAll(min.orderWeight(tempS, trRemove));
                        tempS.clear();
                    }

                    if(st.size() > 2){
                        while((st.get(pos).getState().equals("start") || st.get(pos).getState().equals("end")) && st.size() > 2){
                            if(pos == st.size() - 1){
                                pos = 0;
                            } else {
                                pos++;
                            } 
                        }
                    } else {
                        trRemove = checkEqualTransitions(trRemove);
                    }                 

                    State s = st.get(pos);
                    System.out.println("Considered state: "+s.getState());

                    if(s.getState() != "start" && s.getState() != "end"){
                        
                        if(checkLoop(trRemove, s)){
                            loops.addAll(getLoop(trRemove, s));
                            
                            while(loops.size() > 0) {
                                if(loops.size() == 1) {
                                    loopLabel = loopLabel + loops.get(0).getInput();
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                } else {
                                    loopLabel = loopLabel + loops.get(0).getInput()+"|";
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                }
                            }
                            loops.addAll(getLoop(trRemove, s));
                            if(loopLabel.length() == 1) {
                                if(loopLabel.equals("*")){
                                    loopLabel = "("+loopLabel+")*";
                                } else {
                                    loopLabel = loopLabel+"*";
                                }
                            } else if (loopLabel.length() > 1) {
                                loopLabel = "("+loopLabel+")*";
                            }

                            //delete incoming and outgoing transitions in/from the considered state and create new transitions with combined labels
                            tempTr.clear();
                            tempTr.addAll(trRemove);
                            trRemove.clear();
                            trRemove.addAll(eliminateParallelTransitions(eliminateStateWithLoop(tempTr, loopLabel, s, path, num), path, num));
                            st.removeByState(s);
                        
                        } else {
                            tempTr.clear();
                            tempTr.addAll(trRemove);
                            trRemove.clear();
                            trRemove.addAll(eliminateParallelTransitions(eliminateState(tempTr, s, path, num), path, num));
                            st.removeByState(s);

                        }
                    } else {
                        System.out.println("Start or end state");  
                    }
                    loops.clear();
                }
                if(trRemove.size() >= 2 && st.size() == 2){
                    while(trRemove.size() >= 2){
                        System.out.println("line 433");
                        //trRemove.showStatesT();
                        tempTr.clear();
                        tempTr.addAll(trRemove);
                        trRemove.clear();
                        trRemove.addAll(eliminateParallelTransitions(tempTr, path, num));
                    }
                    
                }
                
            }
        }
        //System.out.println("Size of trRemove: "+trRemove.size()+" Size of st: "+st.size());
        /*if(trRemove.size() >=2 && st.size() == 2){
            while(trRemove.size() >= 2){
                //System.out.println("line 398");
                //trRemove.showStatesT();
                tempTr.clear();
                tempTr.addAll(trRemove);
                trRemove.clear();
                trRemove.addAll(eliminateParallelTransitions(tempTr, path, num));
            }
            
        }*/
        //System.out.println("Size of trRemove: "+trRemove.size()+" Size of st: "+st.size());

        automaton.clear();
        automaton.addStates(st);
        automaton.addStart(startS);
        automaton.addEnd(endS);
        automaton.addTransitions(trRemove);
        trRemove.clear();
        return automaton;
    }

    private boolean checkLoop(Transitions tr, State state) {
        boolean loopIsPresent = false;
        for(Transition i : tr.getAll()) {
            if((i.getNext().getState().equals(state.getState()) ) && (i.getCurrent().getState().equals(state.getState()))) {
                loopIsPresent = true;
            }
        }
        return loopIsPresent;
    }

    private Transitions getLoop(Transitions tr, State state) {
        Transitions loops = new Transitions();
        for(Transition i : tr.getAll()) {
            if(i.getCurrent().getState().equals(i.getNext().getState()) && i.getCurrent().getState().equals(state.getState()))
            loops.add(i);
        }
        return loops;
    }

    private Transitions eliminateStateWithLoop(Transitions tr, String loopLabel, State state, String path, int num){
        in.clear();
        out.clear();
        newTr = new Transitions();
        //delete also the transitions corresponding to loop(s)
        for(Transition i : tr.getAll()) {
            if(i.getNext().getState().equals(state.getState())){
                in.add(i);
            } else if(i.getCurrent().getState().equals(state.getState())){
                out.add(i);
            }
        }
        //for each transition presents in "in" and in "out" create a new transition from current state in "in" to next state in "out"
        //then add them to the list tr that will be added to transitions
        for(Transition i : in.getAll()){
            for(Transition o : out.getAll()){
                if(i.getInput().equals("empty")){
                    newTr.add(new Transition(i.getCurrent(), loopLabel+o.getInput(), o.getNext()));
                } else if(o.getInput().equals("empty")){
                    newTr.add(new Transition(i.getCurrent(), i.getInput()+loopLabel, o.getNext()));
                } else{
                    //System.out.println("Size of final string at 522: "+(i.getInput().length()+loopLabel.length()+o.getInput().length()));
                    //System.out.println("Max size: "+Integer.MAX_VALUE);
                    boolean val = (i.getInput().length()+loopLabel.length()+o.getInput().length())*2 < 0;
                    //System.out.println("Check length: "+val);
                    if(val){
                        end(path, ""+num);
                    } else {
                        if(i.getInput().contains("|") && !o.getInput().contains("|")){
                            newTr.add(new Transition(i.getCurrent(), "("+i.getInput()+")"+loopLabel+o.getInput(), o.getNext()));
                        } else if(o.getInput().contains("|") && !i.getInput().contains("|")){
                            newTr.add(new Transition(i.getCurrent(), i.getInput()+loopLabel+"("+o.getInput()+")", o.getNext()));
                        } else if(i.getInput().contains("|") && o.getInput().contains("|")){
                            newTr.add(new Transition(i.getCurrent(), "("+i.getInput()+")"+loopLabel+"("+o.getInput()+")", o.getNext()));
                        } else {
                            //System.out.println("Eliminate state with loop with no |");
                            newTr.add(new Transition(i.getCurrent(), i.getInput()+loopLabel+o.getInput(), o.getNext()));
                        }
                        newTr.add(new Transition(i.getCurrent(), i.getInput()+loopLabel+o.getInput(), o.getNext()));
                        //newTr.showTransitions();
                    }
                }
            }
        }

        //remove all the entering and exiting transitions from the considered state
        for(Transition i : in.getAll()){
            tr.remove(i);
        }

        for(Transition o : out.getAll()){
            tr.remove(o);
        }

        tr.addAll(newTr);
        newTr.clear();
        in.clear();
        out.clear();
        //tr.showTransitions();
        return tr;
    }

    private Transitions eliminateState(Transitions tr, State state, String path, int num){
        newTr = new Transitions();
        in.clear();
        out.clear();
        //delete also the transitions corresponding to loop(s)
        for(Transition i : tr.getAll()) {
            if(i.getNext().getState().equals(state.getState())){
                in.add(i);
            } else if(i.getCurrent().getState().equals(state.getState())){
                out.add(i);
            }
        }
        //for each transition presents in "in" and in "out" create a new transition from current state in "in" to next state in "out"
        //then add them to the list tr that will be added to transitions
        for(Transition i : in.getAll()){
            for(Transition o : out.getAll()){
                if(i.getInput().equals("empty")){
                    newTr.add(new Transition(i.getCurrent(), o.getInput(), o.getNext()));
                    
                } else if(o.getInput().equals("empty")){
                    newTr.add(new Transition(i.getCurrent(), i.getInput(), o.getNext()));
                } else {
                    //System.out.println("Size of final string at 612: "+(i.getInput().length()+o.getInput().length()));
                    //System.out.println("Max size: "+Integer.MAX_VALUE);
                    boolean val = ((Integer.sum(i.getInput().length(),o.getInput().length()))*2) < 0;
                    //System.out.println("Check length: "+val);
                    //if string lenght is > 1 and at least one "|" is present, then put the parentheses
                    if(val){
                        end(path, ""+num);
                    } else {
                        if(i.getInput().contains("|") && !o.getInput().contains("|")){
                            newTr.add(new Transition(i.getCurrent(), "("+i.getInput()+")"+o.getInput(), o.getNext()));
                        } else if(o.getInput().contains("|") && !i.getInput().contains("|")){
                            newTr.add(new Transition(i.getCurrent(), i.getInput()+"("+o.getInput()+")", o.getNext()));
                        } else if(i.getInput().contains("|") && o.getInput().contains("|")) {
                            newTr.add(new Transition(i.getCurrent(), "("+i.getInput()+")"+"("+o.getInput()+")", o.getNext()));
                        } else {
                            newTr.add(new Transition(i.getCurrent(), i.getInput()+o.getInput(), o.getNext()));
                        }
                        
                    }
                }
                
            }
        }

        //delete all the incoming and outgoing transitions from the considered state
        for(Transition i : in.getAll()){
            tr.remove(i);
        }
        for(Transition i : out.getAll()){
            tr.remove(i);
        }

        tr.addAll(newTr);

        newTr.clear();
        in.clear();
        out.clear();

        return tr;
    }

    private Transitions eliminateParallelTransitions(Transitions tr, String path, int num) {
        tempTransitions = new Transitions();
        tempTransitions.addAll(tr);
        //System.out.println("EliminateParallel on num: "+num);
        //System.out.println(tempTransitions.size());
        while(tempTransitions.size() > 0){
            //System.out.println("Enters while: ");
            for(int i = 0; i < tr.size(); i++) {
                //System.out.println("Automaton: "+num+"Size of tr: "+tr.size()+"; iteration: "+i+"; Size of tempTransitions: "+tempTransitions.size());
                //scan the array
                //for each element look for other transitions in the same array that have same current and next state as the considered element in order to merge their inputs into the first transition, while deleting the second one from the array
                //System.out.println("current state ("+tempTransitions.get(0).getCurrent().getState()+") in tempTransition 0 equal current ("+tr.get(i).getCurrent().getState()+") of i in tr ("+num+")? "+tempTransitions.get(0).getCurrent().getState().equals(tr.get(i).getCurrent().getState()));
                //System.out.println("next state ("+tempTransitions.get(0).getNext().getState()+") in tempTransition 0 equal next of i in tr? "+tempTransitions.get(0).getNext().getState().equals(tr.get(i).getNext().getState()));
                //System.out.println("input of tempTransition 0 different from input of i in tr? "+(tempTransitions.get(0).getInput() != tr.get(i).getInput()));
                if( ( tempTransitions.get(0).getCurrent().getState().equals(tr.get(i).getCurrent().getState())) && ( tempTransitions.get(0).getNext().getState().equals(tr.get(i).getNext().getState()))){  
                    if(( !tempTransitions.get(0).getInput().equals(tr.get(i).getInput()) )){
                        //System.out.println("Entering first if in while");
                        //System.out.println("Size of final string at 676: "+(tempTransitions.get(0).getInput().length()+tr.get(i).getInput().length()));
                        boolean val = Integer.sum(tempTransitions.get(0).getInput().length(), tr.get(i).getInput().length())*2 < 0;
                        //System.out.println("Check length: "+val);
                        if(tempTransitions.get(0).getInput().equals("empty")){
                            t = new Transition(tr.get(i).getCurrent(), tr.get(i).getInput(), tr.get(i).getNext());
                            //System.out.println("First empty");
                        } else if(tr.get(i).getInput().equals("empty")){
                            //System.out.println("Second empty");
                            t = new Transition(tr.get(i).getCurrent(), tempTransitions.get(0).getInput(), tr.get(i).getNext());
                        } else {
                            //System.out.println("None of the empties");
                            if(val){
                                end(path, ""+num);
                            } else {
                                //System.out.println("Not end branch");
                                if(tr.get(i).getInput().contains("|")){
                                    //System.out.println("Enter first");
                                    if(tempTransitions.get(0).getInput().contains("|")){
                                        //System.out.println("Enter first inner if first");
                                        t = new Transition(tr.get(i).getCurrent(), "("+tempTransitions.get(0).getInput()+")|("+tr.get(i).getInput()+")", tr.get(i).getNext());
                                    } else {
                                        //System.out.println("Enter first inner if second");
                                        t = new Transition(tr.get(i).getCurrent(), tempTransitions.get(0).getInput()+"|("+tr.get(i).getInput()+")", tr.get(i).getNext());
                                    }
                                } else {
                                    //System.out.println("Enter second");
                                    if(tempTransitions.get(0).getInput().contains("|")){
                                        //System.out.println("Enter second inner if first");
                                        t = new Transition(tr.get(i).getCurrent(), "("+tempTransitions.get(0).getInput()+")|"+tr.get(i).getInput(), tr.get(i).getNext());
                                    } else {
                                        //System.out.println("Enter second inner if second");
                                        t = new Transition(tr.get(i).getCurrent(), tempTransitions.get(0).getInput()+"|"+tr.get(i).getInput(), tr.get(i).getNext());
                                    }
                                }
                            }

                        }

                        temp = new Transition(tr.get(i).getCurrent(), tr.get(i).getInput(), tr.get(i).getNext());
                        tr.remove(i);
                        tr.add(i, t);
                        
                        if(tr.isPresent(tempTransitions.get(0))){
                            tr.remove(tempTransitions.get(0));
                        }
                        if(tempTransitions.isPresent(temp)){
                            tempTransitions.remove(temp);
                        }
                    } else {
                        Transitions tempTran = new Transitions();
                        tempTran.addAll(tr); 
                        for(int j = tempTran.size() -1; j >= 0; j--){
                            if(i != j && tempTran.get(j).getCurrent().getState().equals(tempTran.get(i).getCurrent().getState()) && tempTran.get(j).getNext().getState().equals(tempTran.get(i).getNext().getState()) && tempTran.get(j).getInput().equals(tempTran.get(i).getInput())){
                                tr.remove(j);
                            }
                        }
                        //System.out.println("Same input? "+tempTransitions.get(0).getInput().equals(tr.get(i).getInput()));
                        //tempTransitions.remove(0);
                        //temp = new Transition(tr.get(i).getCurrent(), tr.get(i).getInput(), tr.get(i).getNext());
                        
                        //if tempTransition.get(0) is present int tr, and it is not present at i, then remove it
                        //if(tr.isPresent(tempTransitions.get(0))){
                        //    tr.remove(tempTransitions.get(0));
                        //}

                        //tr.add(i,temp);

                        //if(tempTransitions.isPresent(temp)){
                        //    tempTransitions.remove(temp);
                        //}

                        //add the transition as a new one, poi eliminare una transizione, vedi come funziona prima
                        //add the elimination of the repeted transition as done in the other if case
                    }
                }
                
            }
            tempTransitions.remove(0);
            
        }
        //System.out.println("Size of tr: "+tr.size());

        return tr;
    }

    private Automaton cleanAutomaton(States states, Transitions transitions, States start, States end){
        boolean accessible;
        boolean exit;
        //clean all the states that does not have any ingoing transition, != start state
        ArrayList<Integer> statestoDelete = new ArrayList<>();
        ArrayList<Integer> toDelete = new ArrayList<>();
        int pos = 0;
        for(int i = 0; i < states.size(); i++){
            accessible = false;
            exit = false;
            while(!accessible && !exit){
                for(Transition t: transitions.getAll()){
                    if(t.getNext().getState().equals(states.get(i).getState())){
                        accessible = true;
                    }
                }
                exit = true;

                
            }
            //delete all the transitions from that state
            if(!accessible && !start.stateIsPresent(states.get(i))){
                
                for(int j = 0; j < transitions.size(); j++){
                    if(transitions.get(j).getCurrent().getState().equals(states.get(i).getState())){
                        toDelete.add(j);
                    }
                }
                
                for(int in = toDelete.size() -1; in >= 0; in--){
                    pos = toDelete.get(in);
                    transitions.remove(pos);
                }
                toDelete.clear();
                statestoDelete.add(i);
            }
        }

        for(int in = statestoDelete.size() -1; in >= 0; in--){
            pos = statestoDelete.get(in);
            states.remove(pos);
        }
        statestoDelete.clear();
        
        //clean all the states that does not have any outgoing transition, != end state
        for(int i = 0; i < states.size(); i++){
            accessible = false;
            exit = false;
            while(!accessible && !exit){
                for(Transition t: transitions.getAll()){
                    if(t.getCurrent().getState().equals(states.get(i).getState())){
                        accessible = true;
                    }
                }
                exit = true;
            }
            //delete all the transitions to that state
            if(!accessible && !end.stateIsPresent(states.get(i))){
                for(int j = 0; j < transitions.size(); j++){
                    if(transitions.get(j).getNext().getState().equals(states.get(i).getState())){
                        toDelete.add(j);
                    }
                }
                for(int in = toDelete.size() -1; in >= 0; in--){
                    pos = toDelete.get(in);
                    transitions.remove(pos);
                }
                toDelete.clear();
                statestoDelete.add(i);
            }
        }

        for(int in = statestoDelete.size() -1; in >= 0; in--){
            pos = statestoDelete.get(in);
            states.remove(pos);
        }
        statestoDelete = null;

        return new Automaton(start, states, end, transitions);
    }

    private Transitions checkEqualTransitions(Transitions tr){
        boolean c = true;
        int i = 0;
        while(i < tr.size() && c){
            //System.out.println("i in checkEqual: "+i);
            int j = 0;
            while(j < tr.size() && c){
                //System.out.println("j in checkEqual: "+j);
                if(i != j){
                    if(tr.get(i).getCurrent().getState().equals(tr.get(j).getCurrent().getState()) && tr.get(i).getInput().equals(tr.get(j).getInput()) && tr.get(i).getNext().getState().equals(tr.get(j).getNext().getState())){
                        tr.remove(j);
                        c = false;
                    }
                }
                j++;
            }
            i++;
        }
        return tr;
    }

    private void cleanAll(){
        newTr= null;
        newTr = new Transitions();
        trRemove = null;
        trRemove = new Transitions();
        t = null;
        temp = null;
        in = null;
        in = new Transitions();
        out = null;
        out = new Transitions();
        automaton = null;
        automaton = new Automaton();
        cleaned = null;
        cleaned = new Automaton();
    }

    private void end(String path, String num){
        //System.out.println("End is invoked for automaton "+num);
        //get the values in the file and put them in failed ArrayList
        File check = new File("../output/"+path+".json");
        if(check.exists()){
            failed.clear();
            JSONReadFromFile read = new JSONReadFromFile();
            failed.addAll(read.getAutomataNum(path));
        }
        if(!failed.contains(num)){
            failed.add(num);
            writeFailed(path);
        }
        //end the translation for this automaton
        end = true;
    }

    private void writeFailed(String path){
        GenerateOutput gen = new GenerateOutput(path, "json");
        gen.writeFileRow("{\"automata\": [");
        for(int i = 0; i <failed.size(); i++){
            if(i == 0){
                gen.writeFileRow("\""+failed.get(i)+"\"");
            } else {
                gen.writeFileRow(", \""+failed.get(i)+"\"");
            }
        }
        gen.writeFile("]");
        gen.writeFile("}");
        gen.closeFile();
    }
}
