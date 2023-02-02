import java.util.ArrayList;

public class TranslatorArray {
    //private static Automaton a;
    private static ArrayList<String> regex = new ArrayList<>();
    private boolean first = true;
    //private TransitionsArr in = new Transitions();
    //private TransitionsArr out = new Transitions();
    private TransitionsArr newTr = new TransitionsArr();
    private TransitionsArr trRemove = new TransitionsArr();
    private TransitionsArr tempTransitions;
    ArrayList<String> tempArr = new ArrayList<>();
    //private TransitionsArr tempTr = new Transitions();
    //private TransitionsArr tempT = new Transitions();
    private TransitionArr t = new TransitionArr();
    private TransitionArr temp = new TransitionArr();
    private TransitionsArr in = new TransitionsArr();
    private TransitionsArr out = new TransitionsArr();
    private AutomatonArr automa;
    private AutomatonArr automaton = new AutomatonArr();
    private AutomatonArr cleaned;
    //private int count = 0;
    //private boolean yes = false;
    //private TransitionsArr tempTran = new TransitionsArr();
    //private static ArrayListTransition> transitions = new AArrayList();

    public TranslatorArray(){
        //a = automaton;
    }

    public ArrayList<String> translateToRegex(AutomatonArr a, int num, String order, String mod){
        
        automa = new AutomatonArr();
        System.out.println("Parsing..."+num);
        //GenerateOutput output = new GenerateOutput("translateToRegex_toomany", "txt");
        if(a.getTransitions().size() >= 2000){
        //    System.out.println("Too many transitions");
        //    output.writeFile("automaton "+num);
        //    output.writeFile("num of transitions: "+a.getTransitions().size());
        } else {
            //boolean "clean" has to be passed when translateToRegex is called
        //a.showAutomaton();
        //AT EACH ROUND: GET ONLY THE STATES WITH THE CONSIDERED STATE, THEN SAVE THEM INTO A FREE TEMP FILE
        //SAVE ONTO THIS FILE ALSO THE TRANSITIONS WITHOUT THE CONSIDERED STATE
        //REPEAT UNTIL THERE IS ONLY ONE TRANSITION LEFT
        //a.showAutomaton();
        automa = modifyAutomata(a, false);
        /*
        if(order.equals("degree")){
            //System.out.println("Degree minimization");
            States tempSt = new States(automa.getAllStates().getAll());
            MinimizationArr min = new MinimizationArr();
            automa.clearStates();
            automa.addStates(min.orderStates(tempSt, automa.getTransitions()));
        } else if (order.equals("bridge")){
            //System.out.println("Bridge minimization");
            States tempSt = new States(automa.getAllStates().getAll());
            MinimizationArr min = new MinimizationArr();
            automa.clearStates();
            automa.addStates(min.orderSerial(tempSt, automa.getTransitions()));
        } else if (order.equals("weight")){
            //System.out.println("Weight minimization");
            States tempSt = new States(automa.getAllStates().getAll());
            MinimizationArr min = new MinimizationArr();
            automa.clearStates();
            //System.out.println("Prova");
            automa.addStates(min.orderWeight(tempSt, automa.getTransitions()));
        }
        */
        automa = removeStates(automa, order, mod);
        //System.out.println("Automaton at 70:");
        //automa.showAutomaton();
        //System.out.println("Size of transitions of automaton: "+automa.getTransitions().size());

        if(automa.getTransitions().size() == 1){
            regex.clear();
            regex.addAll(automa.getTransitions().get(0).getInput());
        } 
        else{
            System.out.println("Transitions not of size 1");
            System.out.println("Size of transitions: "+automa.getTransitions().size());
            System.out.println("Automaton at the end of Translator: ");
            automa.showAutomaton();
        } 
        //automa.showAutomaton();
        //automa = null;
        cleanAll();
        automa = null;
        
        }
        //output.closeFile();
        return regex;
    }

    private AutomatonArr modifyAutomata(AutomatonArr a, boolean clean) {
        automaton = new AutomatonArr();
        
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
                automaton.addTransition(new TransitionArr(automaton.getStartStates().getById("start"), "empty", s));
            }

            automaton.addTransitions(cleaned.getTransitions());
        //1. creare nuovo stato end (unico)
        //2. creare transition da tutti i vecchi end states verso il nuovo end state
        //3. aggiungere le nuove transitions alle transitions dell'automa
        //4. togliere tutti i vecchi start states da startStates
            automaton.addEnd(new State("end"));
            for(State s: cleaned.getEndStates().getAll()){
                automaton. addTransition(new TransitionArr(s, "empty", automaton.getEndStates().getById("end")));
            }
            //first = false;
        } else {
            automaton.addTransitions(cleaned.getTransitions());
        }
        
        //Se ci sono più transitions tra due stessi stati, sostituirle con una sola che presenta come Input l'unione (OR) tra i due valori di input
        tempTransitions = new TransitionsArr();
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
                    if(tempTransitions.get(0).getInput().size() == 1 && automaton.getTransitions().get(i).getInput().size() == 1){
                        t.set(automaton.getTransitions().get(i).getCurrent(), tempTransitions.get(0).getInput().get(0)+"|"+automaton.getTransitions().get(i).getInput().get(0), automaton.getTransitions().get(i).getNext());
                        temp.set(automaton.getTransitions().get(i).getCurrent(), automaton.getTransitions().get(i).getInput().get(0), automaton.getTransitions().get(i).getNext());
                    } else {
                        ArrayList<String> inp = new ArrayList<>();
                        inp.addAll(tempTransitions.get(0).getInput());
                        inp.add("|");
                        inp.addAll(automaton.getTransitions().get(i).getInput());
                        
                        
                        t.set(automaton.getTransitions().get(i).getCurrent(), inp, automaton.getTransitions().get(i).getNext());
                        temp.set(automaton.getTransitions().get(i).getCurrent(), automaton.getTransitions().get(i).getInput(), automaton.getTransitions().get(i).getNext());
                    }
                    
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
            //System.out.println("Line 185");
            for(int i = 0; i < st.size(); i++){
                //System.out.println("Number of start states line 120 translator: "+ start.size());
                //System.out.println("Start state line 120 translator: "+ start.get(0).getState());
                //System.out.println("State line 120 translator: "+ st.get(i).getState());
                if(automaton.getStartStates().get(0).getState().equals(st.get(i).getState())){
                    addStart = false;
                }
            }
            endLoop = true;
        }
        endLoop = false;
        while(!endLoop && addEnd) {
            //System.out.println("Line 198");
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
        /*for(State s : st){
            System.out.println("States at end of modify: "+s.getState());
        }*/
        //Automaton auto = new Automaton(start, st, end, transitions);
        //System.out.println("Automaton at the end of modify: ");
        //auto.showAutomaton();
        automaton.clearStates();
        automaton.addStates(st);

        //automaton.showAutomaton();

        //WRITE THE TRANSITIONS INTO A TEMP FILE (temp_1)
        //GenerateOutput writeTemp = new GenerateOutput("temp_1", "json");
        //writeTemp.writeFile("{\"transitions\" : [");
        //writeTemp.writeFile(automaton.transitionsStructure());
        //writeTemp.writeFile("]\n}");
        //writeTemp.closeFile();
        //automaton.showAutomaton();
        return automaton;
    }
    

    private AutomatonArr removeStates(AutomatonArr a, String order, String mod) {
        //System.out.println("Start states at 238: ");
        //a.getStartStates().showStates();
        //System.out.println("End states at 238: ");
        //a.getEndStates().showStates();
        States startS = new States(a.getStartStates().getAll());
        States endS = new States(a.getEndStates().getAll());

        
        trRemove = new TransitionsArr();
        trRemove.addAll(a.getTransitions());
        //a.getAllStates().showStates();
        //System.out.println("Transitions at start of removeStates: (194)");
        //tr.showTransitions();

        States st = new States();
        if(order.equals("degree")){
            //System.out.println("Degree minimization");
            MinimizationArr min = new MinimizationArr();
            st.addAll(min.orderStates(a.getAllStates(), a.getTransitions()));
        } else if (order.equals("bridge")){
            //System.out.println("Bridge minimization");
            MinimizationArr min = new MinimizationArr();
            st.addAll(min.orderSerial(a.getAllStates(), a.getTransitions()));
        } else if (order.equals("weight")){
            //System.out.println("Weight minimization");
            MinimizationArr min = new MinimizationArr();
            st.addAll(min.orderWeight(a.getAllStates(), a.getTransitions()));
        } else {
            st.addAll(a.getAllStates());
        }
        
        //transitions.addAll(a.getTransitions());
        //System.out.println("removeLoop entered");
        TransitionsArr tempTr = new TransitionsArr();
        TransitionsArr loops = new TransitionsArr();
        ArrayList<String> loopLabel = new ArrayList<>();
        boolean ok;
        if(mod.equals("once")) ok = true;
        else if(mod.equals("iter")) ok = false;
        else ok = true;
        
        if(ok){
        //System.out.println("Size of st: "+st.size());
        while(st.size() >= 3) {
            //System.out.println("Line 271");
            //System.out.println("Size of st: "+st.size());
            State s = st.get(0);
            //System.out.println("State considered at 275: "+st.get(0).getState());
            //System.out.println("State different from start or end: "+(s.getState()!="start")+(s.getState()!="end"));
            if(s.getState() != "start" && s.getState() != "end"){
                //if(s.getState().equals("root--m1--OC_1b") ) {System.out.println("Before removing state"); tr.showTransitions();}
                //for each state: check if there is a loop
                //System.out.println("State considered: "+s.getState());
                
                if(checkLoop(trRemove, s)){
                    loops.addAll(getLoop(trRemove, s));
                    //System.out.println("Checkloop entered");
                
                
                    while(loops.size() > 0) {
                        //System.out.println("Line 285");
                        if(loops.size() == 1) {
                            if(loops.get(0).getInput().size() == 1){
                                if(loopLabel.size() == 1){
                                    String temp = loopLabel.get(0);
                                    if((temp.length()+loops.get(0).getInput().get(0).length()) < Integer.MAX_VALUE){
                                        loopLabel.remove(0);
                                        loopLabel.add(temp + loops.get(0).getInput().get(0));
                                        trRemove.remove(loops.get(0));
                                        loops.remove(0);
                                    } else {
                                        loopLabel.add(loops.get(0).getInput().get(0));
                                        trRemove.remove(loops.get(0));
                                        loops.remove(0);
                                    }
                                } else {
                                    loopLabel.add(loops.get(0).getInput().get(0));
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                }
                            } else {
                                //loops at 0 has input size > 1
                                //loopLabel add the elements of loops 0
                                loopLabel.addAll(loops.get(0).getInput());
                                trRemove.remove(loops.get(0));
                                loops.remove(0);
                            }
                            
                        } else {
                            if(loops.get(0).getInput().size() == 1){
                                if(loopLabel.size() == 1){
                                    String temp = loopLabel.get(0);
                                    if((temp.length()+loops.get(0).getInput().get(0).length() +1) < Integer.MAX_VALUE){
                                        loopLabel.remove(0);
                                        loopLabel.add(temp + loops.get(0).getInput().get(0)+"|");
                                        trRemove.remove(loops.get(0));
                                        loops.remove(0);
                                    } else {
                                        loopLabel.add(loops.get(0).getInput().get(0)+"|");
                                        trRemove.remove(loops.get(0));
                                        loops.remove(0);
                                    }
                                } else {
                                    loopLabel.add(loops.get(0).getInput().get(0)+"|");
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                }
                            } else {
                                //loops at 0 has input size > 1
                                //loopLabel add the elements of loops 0
                                loopLabel.addAll(loops.get(0).getInput());
                                loopLabel.add("|");
                                trRemove.remove(loops.get(0));
                                loops.remove(0);
                            }
                        }
                    }
                    loops.addAll(getLoop(trRemove, s));
                    if(loopLabel.size() == 1) {
                        if(loopLabel.get(0).length() == 1) {
                            String temp = loopLabel.get(0);
                            if(loopLabel.get(0).equals("*")){
                                //System.out.println("Looplabel: "+loopLabel);
                                loopLabel.remove(0);
                                loopLabel.add("("+temp+")*");
                            } else {
                                loopLabel.remove(0);
                                loopLabel.add(temp+"*");
                            }
                        } else if (loopLabel.get(0).length() > 1) {
                            String temp = loopLabel.get(0);
                            loopLabel.remove(0);
                            loopLabel.add("("+temp+")*");
                        }
                    } else {
                        //loopLabel has size > 1, put parentheses before and after the add the *
                        loopLabel.add(0, "(");
                        loopLabel.add(")*");
                    }
                    

                    //bisogna cancellare le transitions entranti e uscenti dal nodo considerato e creare nuove transizioni con unione delle labels
                    tempTr.clear();
                    tempTr.addAll(trRemove);
                    trRemove.clear();
                    trRemove.addAll(eliminateParallelTransitions(eliminateStateWithLoop(tempTr, loopLabel, s)));
                    st.removeByState(s);
                    //System.out.println("Line 372");
            
                } else {
                    tempTr.clear();
                    tempTr.addAll(trRemove);
                    trRemove.clear();
                    trRemove.addAll(eliminateParallelTransitions(eliminateState(tempTr, s)));
                    st.removeByState(s);
                    //se non ci sono loop allora unisco le label degli incoming and exiting edges
                    //System.out.println("Line 381");
                }
            }
            loops.clear();
            //st.showStates();
        }
        //System.out.println("End of while");
        } else {
            int pos = 0;
            while(st.size()>=3){
                //System.out.println("Line 389");
                //System.out.println("Size of st: "+st.size());
                if(order.equals("degree")){
                    System.out.println("Degree minimization");
                    MinimizationArr min = new MinimizationArr();
                    States tempS = new States(st.getAll());
                    st.clear();
                    st.addAll(min.orderStates(tempS, trRemove));
                    tempS.clear();
                } else if (order.equals("bridge")){
                    System.out.println("Bridge minimization");
                    MinimizationArr min = new MinimizationArr();
                    States tempS = new States(st.getAll());
                    st.clear();
                    st.addAll(min.orderSerial(tempS, trRemove));
                    tempS.clear();
                } else if (order.equals("weight")){
                    System.out.println("Weight minimization");
                    MinimizationArr min = new MinimizationArr();
                    //st.showStates();
                    States tempS = new States(st.getAll());
                    st.clear();
                    st.addAll(min.orderWeight(tempS, trRemove));
                    tempS.clear();
                }
                if(st.get(pos).getState() != "start" && st.get(pos).getState() != "end"){
                    //System.out.println("State considered: "+st.get(pos).getState());
                    //if(s.getState().equals("root--m1--OC_1b") ) {System.out.println("Before removing state"); tr.showTransitions();}
                    //for each state: check if there is a loop
                    //System.out.println("State considered: "+s.getState());
                    
                    if(checkLoop(trRemove, st.get(0))){
                        loops.addAll(getLoop(trRemove, st.get(0)));
                        //System.out.println("Checkloop entered");
                        
                        
                        while(loops.size() > 0) {
                            //System.out.println("Line 426");
                            if(loops.size() == 1) {
                                if(loops.get(0).getInput().size() == 1){
                                    if(loopLabel.size() == 1){
                                        String temp = loopLabel.get(0);
                                        if((temp.length()+loops.get(0).getInput().get(0).length()) < Integer.MAX_VALUE){
                                            loopLabel.remove(0);
                                            loopLabel.add(temp + loops.get(0).getInput().get(0));
                                            trRemove.remove(loops.get(0));
                                            loops.remove(0);
                                        } else {
                                            loopLabel.add(loops.get(0).getInput().get(0));
                                            trRemove.remove(loops.get(0));
                                            loops.remove(0);
                                        }
                                    } else {
                                        loopLabel.add(loops.get(0).getInput().get(0));
                                        trRemove.remove(loops.get(0));
                                        loops.remove(0);
                                    }
                                } else {
                                    //loops at 0 has input size > 1
                                    //loopLabel add the elements of loops 0
                                    loopLabel.addAll(loops.get(0).getInput());
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                }
                                
                            } else {
                                if(loops.get(0).getInput().size() == 1){
                                    if(loopLabel.size() == 1){
                                        String temp = loopLabel.get(0);
                                        if((temp.length()+loops.get(0).getInput().get(0).length() +1) < Integer.MAX_VALUE){
                                            loopLabel.remove(0);
                                            loopLabel.add(temp + loops.get(0).getInput().get(0)+"|");
                                            trRemove.remove(loops.get(0));
                                            loops.remove(0);
                                        } else {
                                            loopLabel.add(loops.get(0).getInput().get(0)+"|");
                                            trRemove.remove(loops.get(0));
                                            loops.remove(0);
                                        }
                                    } else {
                                        loopLabel.add(loops.get(0).getInput().get(0)+"|");
                                        trRemove.remove(loops.get(0));
                                        loops.remove(0);
                                    }
                                } else {
                                    //loops at 0 has input size > 1
                                    //loopLabel add the elements of loops 0
                                    loopLabel.addAll(loops.get(0).getInput());
                                    loopLabel.add("|");
                                    trRemove.remove(loops.get(0));
                                    loops.remove(0);
                                }
                            }
                        }
                        loops.addAll(getLoop(trRemove, st.get(0)));
                        if(loopLabel.size() == 1) {
                            if(loopLabel.get(0).length() == 1) {
                                String temp = loopLabel.get(0);
                                if(loopLabel.get(0).equals("*")){
                                    //System.out.println("Looplabel: "+loopLabel);
                                    loopLabel.remove(0);
                                    loopLabel.add("("+temp+")*");
                                } else {
                                    loopLabel.remove(0);
                                    loopLabel.add(temp+"*");
                                }
                            } else if (loopLabel.get(0).length() > 1) {
                                String temp = loopLabel.get(0);
                                loopLabel.remove(0);
                                loopLabel.add("("+temp+")*");
                            }
                        } else {
                            //loopLabel has size > 1, put parentheses before and after the add the *
                            loopLabel.add(0, "(");
                            loopLabel.add(")*");
                        }

                        //bisogna cancellare le transitions entranti e uscenti dal nodo considerato e creare nuove transizioni con unione delle labels
                        tempTr.clear();
                        tempTr.addAll(trRemove);
                        trRemove.clear();
                        trRemove.addAll(eliminateParallelTransitions(eliminateStateWithLoop(tempTr, loopLabel, st.get(0))));
                        st.remove(0);
                        //System.out.println("Line 512");
                    
                    } else{
                        tempTr.clear();
                        tempTr.addAll(trRemove);
                        trRemove.clear();
                        trRemove.addAll(eliminateParallelTransitions(eliminateState(tempTr, st.get(0))));
                        st.remove(0);
                        //se non ci sono loop allora unisco le label degli incoming and exiting edges
                        //System.out.println("Line 521");
                    }
                } else {
                    while((st.get(pos).getState().equals("start") || st.get(pos).getState().equals("end")) && pos < st.size()-1){
                        //System.out.println("Line 524");
                        pos++;
                    }
                    /*boolean end = false;
                    while(!end){
                        if(pos < st.size()-1) {
                            pos++;
                            if(!st.get(pos).getState().equals("start") && !st.get(pos).getState().equals("end")){
                                end = true;
                            } else {
                                if(pos==st.size()-1) {
                                    end = true;
                                    System.out.println("Size of states in while end: "+st.size());
                                }
                            }
                        }
                    }*/
                    
                }
                loops.clear();
            }
        }
            

        automaton.clear();
        automaton.addStates(st);
        automaton.addStart(startS);
        automaton.addEnd(endS);
        automaton.addTransitions(trRemove);
        trRemove.clear();
        //System.out.println("Automaton at 560");
        //automaton.showAutomaton();
        return automaton;
    }

    private boolean checkLoop(TransitionsArr tr, State state) {
        boolean loopIsPresent = false;
        for(TransitionArr i : tr.getAll()) {
            if((i.getNext().getState().equals(state.getState()) ) && (i.getCurrent().getState().equals(state.getState()))) {
                loopIsPresent = true;
            }
        }
        return loopIsPresent;
    }

    private TransitionsArr getLoop(TransitionsArr tr, State state) {
        TransitionsArr loops = new TransitionsArr();
        for(TransitionArr i : tr.getAll()) {
            if(i.getCurrent().getState().equals(i.getNext().getState()) && i.getCurrent().getState().equals(state.getState()))
            loops.add(i);
        }
        return loops;
    }

    private TransitionsArr eliminateStateWithLoop(TransitionsArr tr, ArrayList<String> loopLabel, State state){
        //System.out.println("State considered: "+state.getState());
        in.clear();
        out.clear();
        newTr = new TransitionsArr();
        //cancellare anche transition corrispondenti al/ai loop
        for(TransitionArr i : tr.getAll()) {
            if(i.getNext().getState().equals(state.getState())){
                in.add(i);
                //System.out.println("Transition added to in: ");
                //in.get(in.size() - 1).showTransition();
            } else if(i.getCurrent().getState().equals(state.getState())){
                out.add(i);
                //System.out.println("Transition added to out: ");
                //out.get(out.size() - 1).showTransition();
            }
        }
        //per ogni transition presente in "in" e in "out" creare una nuova transition da stato current in "in" a next in "out" e aggiungerle alla lista newTr che verrà aggiunta a transitions
        for(TransitionArr i : in.getAll()){
            for(TransitionArr o : out.getAll()){
                if(i.getInput().get(0).equals("empty")){
                    //ArrayList<String> tempArr = new AArrayList();
                    tempArr.clear();
                    if(stringLenght(loopLabel)+stringLenght(o.getInput()) >= Integer.MAX_VALUE){
                        tempArr.addAll(loopLabel);
                        tempArr.addAll(o.getInput());
                    } else {
                        String str = new String();
                        for(String s: loopLabel){
                            str = str + s;
                        }
                        for(String s: o.getInput()){
                            str = str +s;
                        }
                        tempArr.add(str);
                    }
                    
                    newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    //System.out.println("Current in in: "+i.getCurrent().getState());
                    //System.out.println("Input: "+loopLabel+o.getInput());
                    //System.out.println("Next in out: "+o.getNext().getState());
                } else if(o.getInput().get(0).equals("empty")){
                    //ArrayList<String> tempArr = new AArrayList();
                    tempArr.clear();
                    if(stringLenght(loopLabel)+stringLenght(i.getInput()) >= Integer.MAX_VALUE){
                        tempArr.addAll(i.getInput());
                        tempArr.addAll(loopLabel);
                    } else {
                        String str = new String();
                        for(String s: loopLabel){
                            str = str + s;
                        }
                        for(String s: i.getInput()){
                            str = str + s;
                        }
                        tempArr.add(str);
                    }
                    newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    //System.out.println("Current in in: "+i.getCurrent().getState());
                    //System.out.println("Input: "+i.getInput()+loopLabel);
                    //System.out.println("Next in out: "+o.getNext().getState());
                } else{
                    //method to check if the character is contained in the arrayList of input of the transition
                    //ArrayList<String> tempArr = new AArrayList();
                    System.out.println("Size of tempArr: "+tempArr.size());
                    System.out.println("Size of i.getinput: "+i.getInput().size());
                    System.out.println("Size of loopLabel: "+loopLabel.size());
                    System.out.println("Size of o.getinput: "+o.getInput().size());
                    if(i.checkChar("|") && !o.checkChar("|")){
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(loopLabel)+stringLenght(o.getInput())+2 >= Integer.MAX_VALUE){
                            tempArr.add("(");
                            tempArr.addAll(i.getInput());
                            tempArr.add(")");
                            tempArr.addAll(loopLabel);
                            tempArr.addAll(o.getInput());
                        } else {
                            String str = new String();
                            str = "(";
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            for(String s: loopLabel){
                                str = str + s;
                            }
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            tempArr.add(str);
                        }
                        
                        System.out.println("Size of newTr line 648: "+newTr.size());
                        System.out.println("Size of tempArr line 649: "+tempArr.size());
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    } else if(o.checkChar("|") && !i.checkChar("|")){
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(loopLabel)+stringLenght(o.getInput())+2 >= Integer.MAX_VALUE){
                            tempArr.addAll(i.getInput());
                            tempArr.addAll(loopLabel);
                            tempArr.add("(");
                            tempArr.addAll(o.getInput());
                            tempArr.add(")");
                        } else {
                            String str = new String();
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            for(String s: loopLabel){
                                str = str + s;
                            }
                            str = str + "(";
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            tempArr.add(str);
                        }
                        
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    } else if(i.checkChar("|") && o.checkChar("|")){
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(loopLabel)+stringLenght(o.getInput())+4 >= Integer.MAX_VALUE){
                            tempArr.add("(");
                            tempArr.addAll(i.getInput());
                            tempArr.add(")");
                            tempArr.addAll(loopLabel);
                            tempArr.add("(");
                            tempArr.addAll(o.getInput());
                            tempArr.add(")");
                        } else {
                            String str = new String();
                            str = "(";
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            for(String s: loopLabel){
                                str = str + s;
                            }
                            str = str + "(";
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            tempArr.add(str);
                        }
                        
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    } else {
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(loopLabel)+stringLenght(o.getInput()) >= Integer.MAX_VALUE){
                            tempArr.addAll(i.getInput());
                            tempArr.addAll(loopLabel);
                            tempArr.addAll(o.getInput());
                        } else {
                            String str = new String();
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            for(String s: loopLabel){
                                str = str + s;
                            }
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            tempArr.add(str);
                        }
                        
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    }
                    //System.out.println("Size of i, of added loopLabel and input of o: "+i.getInput().length()+" "+loopLabel.length()+ " "+o.getInput().length());
                    //System.out.println("Max Integer size: "+ Integer.MAX_VALUE);
                    //newTr.add(new Transition(i.getCurrent(), i.getInput()+loopLabel+o.getInput(), o.getNext()));
                    //System.out.println("Current in in: "+i.getCurrent().getState());
                    //System.out.println("Input: "+i.getInput()+loopLabel+o.getInput());
                    //System.out.println("Next in out: "+o.getNext().getState());
                }
            }
        }

        //elimina tutte le transitions entranti e uscenti dallo stato considerato
        for(TransitionArr i : in.getAll()){
            tr.remove(i);
        }
        /*
        for(Transition i : transitions) {
            System.out.println("Transitions after in removal: ");
            i.showTransition();
        }
        */
        for(TransitionArr o : out.getAll()){
            tr.remove(o);
        }
        /*
        for(Transition i : transitions) {
            System.out.println("Transitions after out removal: ");
            i.showTransition();
        }
        
        for(Transition i : newTr){
            System.out.println("New transition instead of loop: ");
            i.showTransition();;
        }*/
        tr.addAll(newTr);
        /*System.out.println("Transitions at the end of eliminateStateWithLoop:");
        for(Transition i : transitions) {
            i.showTransition();
        }*/
        newTr.clear();
        in.clear();
        out.clear();

        return tr;
    }

    private TransitionsArr eliminateState(TransitionsArr tr, State state){
        //System.out.println("State considered: "+state.getState());
        newTr = new TransitionsArr();
        in.clear();
        out.clear();
        //cancellare anche transition corrispondenti al/ai loop
        //System.out.println("State considered in eliminate state: "+state.getState());
        //Transitions newTr = new Transitions();
        //Transitions transitions = new Transitions();
        //transitions.addAll(tr);
        //Transitions in = new Transitions();
        //Transitions out = new Transitions();
        for(TransitionArr i : tr.getAll()) {
            if(i.getNext().getState().equals(state.getState())){
                in.add(i);
                //System.out.println("Transition added to in: ");
                //in.get(in.size() - 1).showTransition();
            } else if(i.getCurrent().getState().equals(state.getState())){
                out.add(i);
                //System.out.println("Transition added to out: ");
                //out.get(out.size() - 1).showTransition();
            }
        }
        //per ogni transition presente in "in" e in "out" creare una nuova transition da stato current in "in" a next in "out" e aggiungerle alla lista tr che verrà aggiunta a transitions
        for(TransitionArr i : in.getAll()){
            for(TransitionArr o : out.getAll()){
                //System.out.println("Current in in: "+i.getCurrent().getState());
                //System.out.println("Next in out: "+o.getNext().getState());
                
                if(i.getInput().get(0).equals("empty")){
                    newTr.add(new TransitionArr(i.getCurrent(), o.getInput(), o.getNext()));
                    
                } else if(o.getInput().get(0).equals("empty")){
                    newTr.add(new TransitionArr(i.getCurrent(), i.getInput(), o.getNext()));
                } else{
                    //se una stringa ha lunghezza > 1 allora mettere parentesi ed è presente un |
                    
                    if(i.checkChar("|") && !o.checkChar("|")){
                        //System.out.println("Entering case of first element with |");
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(o.getInput())+2 >= Integer.MAX_VALUE){
                            tempArr.add("(");
                            tempArr.addAll(i.getInput());
                            tempArr.add(")");
                            tempArr.addAll(o.getInput());
                        } else {
                            String str = new String();
                            str = "(";
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            tempArr.add(str);
                        }
                        
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    } else if(o.checkChar("|") && !i.checkChar("|")){
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(o.getInput())+2 >= Integer.MAX_VALUE){
                            tempArr.addAll(i.getInput());
                            tempArr.add("(");
                            tempArr.addAll(o.getInput());
                            tempArr.add(")");
                        } else {
                            String str = new String();
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            str = str + "(";
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            tempArr.add(str);
                        }
                        
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    } else if(i.checkChar("|") && o.checkChar("|")) {
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(o.getInput())+4 >= Integer.MAX_VALUE){
                            tempArr.add("(");
                            tempArr.addAll(i.getInput());
                            tempArr.add(")(");
                            tempArr.addAll(o.getInput());
                            tempArr.add(")");
                        } else {
                            String str = new String();
                            str = "(";
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            str = str + ")(";
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            str = str + ")";
                            tempArr.add(str);
                        }
                        
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    } else {
                        tempArr.clear();
                        if(stringLenght(i.getInput())+stringLenght(o.getInput()) >= Integer.MAX_VALUE){
                            tempArr.addAll(i.getInput());
                            tempArr.addAll(o.getInput());
                        } else {
                            String str = new String();
                            for(String s: i.getInput()){
                                str = str + s;
                            }
                            for(String s: o.getInput()){
                                str = str + s;
                            }
                            tempArr.add(str);
                        }
                        newTr.add(new TransitionArr(i.getCurrent(), tempArr, o.getNext()));
                    }
                    
                }
                
            }
        }

        //elimina tutte le transitions entranti e uscenti dallo stato considerato
        for(TransitionArr i : in.getAll()){
            tr.remove(i);
        }
        for(TransitionArr i : out.getAll()){
            tr.remove(i);
        }
        /*
        for(Transition i : newTr){
            System.out.println("New transition without state "+state.getState()+": ");
            i.showTransition();
        }*/

        tr.addAll(newTr);
        /*System.out.println("Transitions at the end of eliminateState:");
        for(Transition i : transitions) {
            i.showTransition();
        }*/
        newTr.clear();
        in.clear();
        out.clear();

        return tr;
    }

    private TransitionsArr eliminateParallelTransitions(TransitionsArr tr) {
        //ArrayListTransition> transitions = new AArrayList();
        //transitions.addAll(tr);
        tempTransitions = new TransitionsArr();
        tempTransitions.addAll(tr);

        while(tempTransitions.size() > 0){
            //System.out.println("Line 795");
            //System.out.println("Size of tempTransitions: "+tempTransitions.size());
            //System.out.println("TempTransition: ");
            //tempTransitions.get(0).showTransition();
            for(int i = 0; i < tr.size(); i++) {
                //System.out.println("Line 799");
            //scorriamo l'array, considerando ogni elemento
            //per ogni elemento cerchiamo nuovamente nell'array per cercare altri stati che presentano stesso current e next e "unire" gli input nella prima transizione, cancellando la seconda dalla lista
                if( ( tempTransitions.get(0).getCurrent().getState().equals(tr.get(i).getCurrent().getState())) && ( tempTransitions.get(0).getNext().getState().equals(tr.get(i).getNext().getState())) && ( !tempTransitions.get(0).getInput().equals(tr.get(i).getInput()) )){
                    //System.out.println("Line 802");
                    //System.out.println("First transition to or: ");
                    //transitions.get(i).showTransition();
                    //System.out.println("Second transition to or: ");
                    //tempTransitions.get(0).showTransition();
                    //System.out.println("tempTransition(495): ");
                    //tempTransitions.get(0).showTransition();
                    //System.out.println("tr(497): ");
                    //tr.get(i).showTransition();
                    //tempTransitions.get(0).showTransition();
                    //ArrayList<String> tempArr = new AArrayList();
                    if(tempTransitions.get(0).getInput().get(0).equals("empty")){
                        t = new TransitionArr(tr.get(i).getCurrent(), tr.get(i).getInput(), tr.get(i).getNext());
                    } else if(tr.get(i).getInput().get(0).equals("empty")){
                        t = new TransitionArr(tr.get(i).getCurrent(), tempTransitions.get(0).getInput(), tr.get(i).getNext());
                    } else {
                        if(tr.get(i).checkChar("|")){
                            if(tempTransitions.get(0).checkChar("|")){
                                tempArr.clear();
                                if(stringLenght(tempTransitions.get(0).getInput())+stringLenght(tr.get(i).getInput())+5>=Integer.MAX_VALUE){
                                    tempArr.add("(");
                                    tempArr.addAll(tempTransitions.get(0).getInput());
                                    tempArr.add(")|(");
                                    tempArr.addAll(tr.get(i).getInput());
                                    tempArr.add(")");
                                } else {
                                    String str = new String();
                                    str = "(";
                                    for(String s: tempTransitions.get(0).getInput()){
                                        str = str + s;
                                    }
                                    str = str + ")|(";
                                    for(String s: tr.get(i).getInput()){
                                        str = str + s;
                                    }
                                    str = str + ")";
                                    tempArr.add(str);
                                }
                                
                                System.out.println("Size of tempArr: "+tempArr.size());
                                t = new TransitionArr(tr.get(i).getCurrent(), tempArr, tr.get(i).getNext());
                            } else {
                                tempArr.clear();
                                if(stringLenght(tempTransitions.get(0).getInput())+stringLenght(tr.get(i).getInput())+3>=Integer.MAX_VALUE){
                                    tempArr.addAll(tempTransitions.get(0).getInput());
                                    tempArr.add("|(");
                                    tempArr.addAll(tr.get(i).getInput());
                                    tempArr.add(")");
                                } else {
                                    String str = new String();
                                    for(String s: tempTransitions.get(0).getInput()){
                                        str = str + s;
                                    }
                                    str = str + "|(";
                                    for(String s: tr.get(i).getInput()){
                                        str = str + s;
                                    }
                                    str = str + ")";
                                    tempArr.add(str);
                                }
                                
                                t = new TransitionArr(tr.get(i).getCurrent(), tempArr, tr.get(i).getNext());
                            }
                        } else {
                            if(tempTransitions.get(0).checkChar("|")){
                                tempArr.clear();
                                if(stringLenght(tempTransitions.get(0).getInput())+stringLenght(tr.get(i).getInput())+3>=Integer.MAX_VALUE){
                                    tempArr.add("(");
                                    tempArr.addAll(tempTransitions.get(0).getInput());
                                    tempArr.add(")|");
                                    tempArr.addAll(tr.get(i).getInput());
                                } else {
                                    String str = new String();
                                    str = "(";
                                    for(String s: tempTransitions.get(0).getInput()){
                                        str = str + s;
                                    }
                                    str = str + ")|";
                                    for(String s: tr.get(i).getInput()){
                                        str = str + s;
                                    }
                                    tempArr.add(str);
                                }
                                
                                t = new TransitionArr(tr.get(i).getCurrent(), tempArr, tr.get(i).getNext());
                            } else {
                                //System.out.print("line 527: "+tempTransitions.get(0).getInput()+tr.get(i).getInput());
                                tempArr.clear();
                                System.out.println("Final size of string: "+(stringLenght(tempTransitions.get(0).getInput())+stringLenght(tr.get(i).getInput())+1));
                                System.out.println("Max lenght: "+Integer.MAX_VALUE);
                                if(stringLenght(tempTransitions.get(0).getInput())+stringLenght(tr.get(i).getInput())+1000>=Integer.MAX_VALUE){
                                    tempArr.addAll(tempTransitions.get(0).getInput());
                                    tempArr.add("|");
                                    tempArr.addAll(tr.get(i).getInput());
                                } else {
                                    String str = new String();
                                    
                                    for(String s: tempTransitions.get(0).getInput()){
                                        str = str + s;
                                    }
                                    str = str + "|";
                                    for(String s: tr.get(i).getInput()){
                                        System.out.println("Lenght of string: "+(str.length()+1));
                                        str = str + s;
                                    }
                                    
                                    tempArr.add(str);
                                }
                                
                                t = new TransitionArr(tr.get(i).getCurrent(), tempArr, tr.get(i).getNext());
                            }
                        }
                        /*
                        if( tr.get(i).getInput().length() > 1 && tr.get(i).getInput().contains("|") && tempTransitions.get(0).getInput().length() > 1 && tempTransitions.get(0).getInput().contains("|")){
                            //System.out.println("Transition in temp: ");
                            //tempTransitions.get(0).showTransition();
                            t = new Transition(tr.get(i).getCurrent(), "("+tempTransitions.get(0).getInput()+")|("+tr.get(i).getInput()+")", tr.get(i).getNext());
                        } else if( tempTransitions.get(0).getInput().length() > 1 && tempTransitions.get(0).getInput().contains("|")) {
                            t = new Transition(tr.get(i).getCurrent(), "("+tempTransitions.get(0).getInput()+")|"+tr.get(i).getInput(), tr.get(i).getNext());
                        } else if( tr.get(i).getInput().length() > 1 && tr.get(i).getInput().contains("|")){
                            t = new Transition(tr.get(i).getCurrent(), tempTransitions.get(0).getInput()+"|("+tr.get(i).getInput()+")", tr.get(i).getNext());
                        } else if(tr.get(i).getInput().length() == 1 && tempTransitions.get(0).getInput().length() == 1) {
                            if(tr.get(i).getCurrent().getState()=="test26_5" || tr.get(i).getNext().getState()=="test26_5"){
                                System.out.println("Transition with state test26_5");
                                tr.get(i).showTransition();
                            } 
                            t = new Transition(tr.get(i).getCurrent(), tempTransitions.get(0).getInput()+"|"+tr.get(i).getInput(), tr.get(i).getNext());
                        } else {
                            System.out.println("Not an input of length >= 1, nor empty input");
                            System.out.println("Transitions considered in tr: ");
                            tr.get(i).showTransition();
                            System.out.println("transitions in tempTransitions");
                            tempTransitions.get(0).showTransition();
                            t = new Transition(tr.get(i).getCurrent(), tempTransitions.get(0).getInput()+"|"+tr.get(i).getInput(), tr.get(i).getNext());
                        }*/
                    
                    }
                    //System.out.println("New transition: ");
                    //t.showTransition();
                    temp = new TransitionArr(tr.get(i).getCurrent(), tr.get(i).getInput(), tr.get(i).getNext());
                    //t.showTransition();
                    tr.remove(i);
                    tr.add(i, t);
                    
                    if(tr.isPresent(tempTransitions.get(0))){
                        tr.remove(tempTransitions.get(0));
                    }
                    /*
                    for(int j = 0; j < tr.size(); j ++) {
                        if((tr.get(j).getCurrent().getState().equals(tempTran.get(0).getCurrent().getState())) && (tr.get(j).getInput().equals(tempTran.get(0).getInput())) && (tr.get(j).getNext().getState().equals(tempTran.get(0).getNext().getState()))){
                            //System.out.println("Transition to remove: ");
                            //transitions.get(j).showTransition();
                            tr.remove(j);
                        }
                    }*/

                    //rimuove da tempTransition la transition uguale a quella considerata in transitions (così da evitare di ottenere lo stesso risultato due volte ma ribaltato: b|d e d|b) 
                    /*for(int j = 0; j < tempTransitions.size(); j++) {
                        if((temp.getCurrent().getState().equals(tempTransitions.get(j).getCurrent().getState())) && (temp.getInput().equals(tempTransitions.get(j).getInput())) && (temp.getNext().getState().equals(tempTransitions.get(j).getNext().getState()))){
                            //System.out.println("Transition in tempTransition to remove: ");
                            //tempTransitions.get(j).showTransition();
                            tempTransitions.remove(j);
                        }
                    }*/
                    if(tempTransitions.isPresent(temp)){
                        tempTransitions.remove(temp);
                    }
                }
            
            }
            //System.out.println("Line 912");
            tempTransitions.remove(0);
            /*if(tr.size() < 174){
                System.out.println("Size of tempTran after: "+tempT.size()+" "+ tr.size());
            }*/
            
        }
        /*System.out.println("Transitions at the end of eliminateParallelTransitions: ");
        for(Transition i : transitions){
            i.showTransition();
        }*/
        //System.out.println("Line 925");
        return tr;
    }

    private Integer stringLenght(ArrayList<String> array){
        int sum = 0;
        for(String s: array){
            sum = sum + s.length();
        }
        return sum;
    }

    private AutomatonArr cleanAutomaton(States states, TransitionsArr transitions, States start, States end){
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
                //System.out.println("Line 933");
                for(TransitionArr t: transitions.getAll()){
                    if(t.getNext().getState().equals(states.get(i).getState())){
                        accessible = true;
                    }
                }
                //System.out.println("State "+states.get(i).getState()+" is accessible: "+accessible);
                exit = true;

                
            }
            //delete all the transitions from that state
            if(!accessible && !start.stateIsPresent(states.get(i))){
                //System.out.println("Start state: "+ start.contains(states.get(i)));
                //System.out.println("Entered not accessible if for state "+states.get(i).getState() + accessible);
                
                for(int j = 0; j < transitions.size(); j++){
                    if(transitions.get(j).getCurrent().getState().equals(states.get(i).getState())){
                        //System.out.println("Transition exiting from state not accessible:");
                        //transitions.get(j).showTransition();
                        toDelete.add(j);
                    }
                }
                //System.out.println("Transitions to be eliminated: ");
                //for(Integer d: toDelete){
                //    transitions.get(d).showTransition();
                //}
                for(int in = toDelete.size() -1; in >= 0; in--){
                    pos = toDelete.get(in);
                    transitions.remove(pos);
                }
                toDelete.clear();
                statestoDelete.add(i);
            }
        }
        //for(Integer d: statestoDelete){
            //System.out.println("States to be eliminated: "+ states.get(d).getState());
        //}
        for(int in = statestoDelete.size() -1; in >= 0; in--){
            //System.out.println("States eliminated: "+ states.get(statestoDelete.get(in)).getState());
            pos = statestoDelete.get(in);
            states.remove(pos);
        }
        statestoDelete.clear();
        
        //clean all the states that does not have any outgoing transition, != end state
        for(int i = 0; i < states.size(); i++){
            accessible = false;
            exit = false;
            while(!accessible && !exit){
                //System.out.println("Line 983");
                for(TransitionArr t: transitions.getAll()){
                    if(t.getCurrent().getState().equals(states.get(i).getState())){
                        accessible = true;
                    }
                }
                //System.out.println("State "+states.get(i).getState()+" is post-accessible: "+accessible);
                exit = true;
            }
            //delete all the transitions to that state
            if(!accessible && !end.stateIsPresent(states.get(i))){
                //System.out.println("End state: "+ end.contains(states.get(i)));
                //System.out.println("Entered not post-accessible if for state "+states.get(i).getState() + accessible);
                
                for(int j = 0; j < transitions.size(); j++){
                    if(transitions.get(j).getNext().getState().equals(states.get(i).getState())){
                        //System.out.println("Transition entering in state not post-accessible:");
                        //transitions.get(j).showTransition();
                        toDelete.add(j);
                    }
                }
                for(int in = toDelete.size() -1; in >= 0; in--){
                    //System.out.println("Position in states to delete: "+in+" of transition: ");
                    //transitions.get(toDelete.get(in)).showTransition();
                    pos = toDelete.get(in);
                    transitions.remove(pos);
                }
                toDelete.clear();
                statestoDelete.add(i);
            }
        }

        for(int in = statestoDelete.size() -1; in >= 0; in--){
            //System.out.println("Position in states to delete: "+in+" of element: "+ states.get(in).getState());
            pos = statestoDelete.get(in);
            states.remove(pos);
        }
        statestoDelete = null;
        //System.out.println("Transitions at the end of cleaning: ");
        
        //for(Transition t: transitions){
        //    t.showTransition(); 
        //}
        //System.out.println("States at the end of cleaning: "); 
        //for(State s: states){
        //    System.out.println(s.getState()); 
        //}

        return new AutomatonArr(start, states, end, transitions);
    }

    private void cleanAll(){
        newTr= null;
        newTr = new TransitionsArr();
        trRemove = null;
        trRemove = new TransitionsArr();
        t = null;
        temp = null;
        in = null;
        in = new TransitionsArr();
        out = null;
        out = new TransitionsArr();
        automaton = null;
        automaton = new AutomatonArr();
        cleaned = null;
        cleaned = new AutomatonArr();
    }
}
