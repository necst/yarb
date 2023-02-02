import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

//import javax.swing.plaf.nimbus.State;
//import javax.swing.plaf.nimbus.State;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ANMLparser {
    Transitions transitions = new Transitions();
    States startStates = new States();
    States endStates = new States();
    States states = new States();
    ArrayList<String> orEndStates = new ArrayList<>();
    ArrayList <Automaton> automata = new ArrayList<>();
    Automaton automaton;
    //used to save the list of automaton at the end of parsing from ANML to our automaton structure
    

    public ANMLparser(){    }

    public Integer parse(String[] file){

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            //create the folder
            new File(file[2]).mkdirs();
            Document doc = db.parse(new File(file[0]));
            doc.getDocumentElement().normalize();

            //list contains all the state-transition-elements in the file
            NodeList list = doc.getElementsByTagName("state-transition-element");
            NodeList or = doc.getElementsByTagName("or");
            //System.out.println("Number of or: "+or.getLength());

            for(int i = 0; i < or.getLength(); i++){
                Node nor = or.item(i);
                Element orEl = (Element) nor;
                String orState = orEl.getAttribute("id");
                
                if(!file[4].equals("SPM")){
                    if(orState.indexOf("__") >= 0){
                        orState = orState.substring(2, orState.length()-2);
                    }
                }
                
                if(!orEndStates.contains(orState)) orEndStates.add(orState);
            }
            
            // consider the first start state and its transitions
            for(int temp = 0; temp <list.getLength(); temp++){
                Node node = list.item(temp);
                if(node.getNodeType() == Node.ELEMENT_NODE){
                    Element element = (Element) node;
                    String start = element.getAttribute("start");
                    String curr = element.getAttribute("id");
                    if(!file[4].equals("SPM")){
                        if(curr.indexOf("__") >= 0){
                            curr = curr.substring(2, curr.length()-2);
                        }
                    }
                    
                    System.out.println("Current state: "+curr);
                    //input for the transition
                    String input = element.getAttribute("symbol-set");
                    int ind = input.indexOf("x20");
                    if(ind >= 0){
                        input = input.substring(0, ind-1) + " " + input.substring(ind + 3);
                    }

                    ind = input.indexOf("[^\\x0a]");
                    if(ind >= 0){
                        if(ind > 0 && ind < input.length()-7){
                            input = input.substring(0, ind-1) + "(.)" + input.substring(ind + 7);
                        } else {
                            input = "(.)";
                        }
                    }

                    ind = input.indexOf("[");
                    int endIn = input.indexOf("]");

                    if(ind >= 0 && endIn >= 0){
                        if(input.substring(ind+1, endIn).length() == 1 && !input.substring(ind+1, endIn).equals(" ")){
                            input = input.substring(ind+1, endIn);
                        }
                    }

                    if(input.equals("*")){
                        if(file[4].equals("ClamAV")){
                            input = ".";
                        } else {
                            if(file[4].equals("Levenshtein")) input = "(\\x2A)";
                            else input = "\\x2A";
                        }
                    }

                    if(input.equals("|")){
                        if(file[4].equals("Levenshtein")) input = "(\\x7c)";
                        else input = "\\x7c";
                    }

                    if(input.equals("+")){
                        if(file[4].equals("Levenshtein")) input = "(\\x2B)";
                        else input = "\\x2B";
                    }

                    if(input.equals("(")){
                        if(file[4].equals("Levenshtein")) input = "(\\x28)";
                        else input = "\\x28";
                    }

                    if(input.equals(")")){
                        if(file[4].equals("Levenshtein")) input = "(\\x29)";
                        else input = "\\x29";
                    }

                    //next states exiting from the current state
                    NodeList nextStates = element.getElementsByTagName("activate-on-match");
                    String nextId;
                    String endId;
                    NodeList end = element.getElementsByTagName("report-on-match");
                    
                    //no automaton has been created
                    if(automata.size() == 0) {
                        //System.out.println("Automata array is empty.. creating first automaton");
                        //start state is added to both states and startStates if not already present
                        if(!states.labelIsPresent(curr))
                            states.add(new State(curr));
                        if(start != "" && start != "none"){
                            if(!startStates.labelIsPresent(curr))
                                startStates.add(states.getById(curr));
                        }
                        for(int i = 0; i < orEndStates.size(); i++){
                            if(orEndStates.get(i).equals(curr)){
                                if(!endStates.labelIsPresent(curr)) endStates.add(states.getById(curr));
                                orEndStates.remove(i);
                            }
                        }
                        //startStates.showStates();
                        //states.showStates();
                        
                        Element n;
                        //add all of its transitions to the transitions array and all the next states to the states array
                        //if there are any end states, add them to the array of end states
                        for(int i = 0; i <nextStates.getLength(); i++){
                            n = (Element) nextStates.item(i);
                            nextId = n.getAttribute("element");
                            if(!file[4].equals("SPM")){
                                if(nextId.indexOf("__") >= 0){
                                    nextId = nextId.substring(2, nextId.length()-2);
                                }
                            }
                            
                            if(!states.labelIsPresent(nextId)) states.add(new State(nextId));
                            if(!transitions.isPresent(startStates.getById(curr), input, states.getById(nextId))) transitions.add(new Transition(startStates.getById(curr), input, states.getById(nextId)));
                            
                            for(int j = 0; j < orEndStates.size(); j++){
                                if(orEndStates.get(j).equals(nextId)){
                                    if(!endStates.labelIsPresent(orEndStates.get(i))) endStates.add(states.getById(nextId));
                                    orEndStates.remove(i);
                                }
                            }
                            
                        }
                        //add also all the end states in states and endStates if not already present and relative transitions 
                        if(end.getLength() > 0){
                            for(int i = 0; i < end.getLength(); i++){
                                endId = "temp_"+curr+""+i;
                                //end state is added to both states and endStates
                                if(!states.labelIsPresent(endId)){
                                    states.add(new State(endId));
                                }

                                if(!endStates.labelIsPresent(endId)) endStates.add(states.getById(endId));
                                if(!transitions.isPresent(curr, input, endId)){
                                    transitions.add(new Transition(states.getById(curr), input, states.getById(endId)));
                                }
                                    
                            }
                            
                        }
                        
                        automata.add(new Automaton(startStates, states, endStates, transitions));
                        startStates.clear();
                        endStates.clear();
                        states.clear();
                        transitions.clear();
                    } else {
                        Element n;
                        Automaton auto;
                        //WHEN A START STATE OR AN END STATE IS MET:
                        //CHECK IF IN THE TEMP STATES, STARTSTATES, ENDSTATES ARRAYS THERE ARE STATES THAT ARE PRESENT IN ANY OF THE ALREADY CREATED AUTOMATA
                        //IF THEY ARE NOT PRESENT IN ANY OF THEM: CREATE A NEW AUTOMATON WITH THE CONNECTED STATES
                        //if(start != ""){
                            //check only if states in temp are present in any of the already created automata
                        //    checkAtStart();
                        //}
                        if((start != "" && start != "none") || end.getLength() > 0){
                            checkTemp();
                        }

                        //check to try: every time a curr/next state is added to an automaton, check if that state is also contained in another
                        //automaton: if true, add the states/transitions of the smaller automaton to the bigger one (merge)
                            
                        //check in all the created automata if curr state is already present in one of them
                        for(int i = 0; i < automata.size(); i++){
                            //if the current state is present in the automaton i
                            if(automata.get(i).checkState(curr)){
                                auto = automata.get(i);
                                //if it is a start state, add it to the startStates array of the automaton
                                if(start != "" && start != "none"){
                                    if(!auto.checkStart(auto.getAllStates().getById(curr))){
                                        auto.addStart(auto.getAllStates().getById(curr));
                                    }
                                    
                                } 

                                for(int in = 0; in < orEndStates.size(); in++){
                                    if(orEndStates.get(in).equals(curr)){
                                        if(!auto.checkEnd(curr)) auto.addEnd(auto.getAllStates().getById(curr));
                                        orEndStates.remove(in);
                                    }
                                }
                                //add all the transitions and next states to automaton i(if not already present in the automaton)
                                for(int j = 0; j <nextStates.getLength(); j++){
                                    n = (Element) nextStates.item(j);
                                    nextId = n.getAttribute("element");
                                    if(!file[4].equals("SPM")){
                                        if(nextId.indexOf("__") >= 0){
                                            nextId = nextId.substring(2, nextId.length()-2);
                                        }
                                    }
                                    
                                    if(!auto.checkState(nextId)) auto.addState(new State(nextId));
                                    if(!auto.checkTransition(auto.getAllStates().getById(curr), input, auto.getAllStates().getById(nextId))){
                                        auto.addTransition(new Transition(auto.getAllStates().getById(curr), input, auto.getAllStates().getById(nextId)));
                                    }
                                    for(int in = 0; in < orEndStates.size(); in++){
                                        if(orEndStates.get(in).equals(nextId)){
                                            if(!auto.checkEnd(nextId)) auto.addEnd(auto.getAllStates().getById(nextId));
                                            orEndStates.remove(in);
                                        }
                                    }

                                }
                                if(end.getLength() > 0){
                                    for(int k = 0; k < end.getLength(); k++){
                                        endId = "temp_"+curr+""+k;
                                        //end state is added to both states and endStates if not already present
                                        if(!auto.checkState(endId)){
                                            auto.addState(new State(endId));
                                        }
                                        if(!auto.checkEnd(endId)){
                                            auto.addEnd(auto.getAllStates().getById(endId));
                                        }
                                        if(!auto.checkTransition(curr, input, endId)){
                                            auto.addTransition(new Transition(auto.getAllStates().getById(curr), input, auto.getAllStates().getById(endId)));
                                        } 
                                    }
                                }
                                merge(i);
                            //if the nextStates are present in one of the automata
                            } else {
                                auto = automata.get(i);
                                int j = 0;
                                boolean present = false;
                                while(!present && j < nextStates.getLength()){
                                    n = (Element) nextStates.item(j);
                                    nextId = n.getAttribute("element");
                                    if(!file[4].equals("SPM")){
                                        if(nextId.indexOf("__") >= 0){
                                            nextId = nextId.substring(2, nextId.length()-2);
                                        }
                                    }
                                    
                                    if(auto.checkState(nextId)){
                                        present = true;
                                    }
                                    j++;
                                }
                                //if one of the next states is present in one of the automata
                                if(present){
                                    //curr is added to the corresponding automaton
                                    auto.addState(new State(curr));
                                    //if it is a start state is added to startStates array of the automaton
                                    if(start != "" && start != "none"){
                                        if(!auto.checkStart(auto.getAllStates().getById(curr))){
                                            auto.addStart(auto.getAllStates().getById(curr));
                                        }
                                    }
                                    for(int in = 0; in < orEndStates.size(); in++){        
                                        if(orEndStates.get(in).equals(curr)){
                                            if(!auto.checkEnd(curr)) auto.addEnd(auto.getAllStates().getById(curr));
                                            orEndStates.remove(in);
                                        }
                                    }
                                    //add next states (if not already present) and the relative transitions
                                    for(int k = 0; k < nextStates.getLength(); k++){
                                        n = (Element) nextStates.item(k);
                                        nextId = n.getAttribute("element");
                                        if(!file[4].equals("SPM")){
                                            if(nextId.indexOf("__") >= 0){
                                                nextId = nextId.substring(2, nextId.length()-2);
                                            }
                                        }
                                        
                                        if(!auto.checkState(nextId)) {
                                            auto.addState(new State(nextId));
                                        }
                                        if(!auto.checkTransition(auto.getAllStates().getById(curr), input, auto.getAllStates().getById(nextId))){
                                            auto.addTransition(new Transition(auto.getAllStates().getById(curr), input, auto.getAllStates().getById(nextId)));
                                        }
                                        //check if any of the next states is also an ending state
                                        for(int in = 0; in < orEndStates.size(); in++){        
                                            if(orEndStates.get(in).equals(nextId)){
                                                if(!auto.checkEnd(nextId)) auto.addEnd(auto.getAllStates().getById(nextId));
                                                orEndStates.remove(in);
                                            }
                                        }

                                    }

                                    if(end.getLength() > 0){
                                        for(int k = 0; k < end.getLength(); k++){
                                            endId = "temp_"+curr+""+k;

                                            //end state is added to both states and endStates if not already present
                                            if(!auto.checkState(endId)){
                                                auto.addState(new State(endId));
                                            }
                                            if(!auto.checkEnd(endId)){
                                                auto.addEnd(auto.getAllStates().getById(endId));
                                            }
                                            if(!auto.checkTransition(curr, input, endId)){
                                                auto.addTransition(new Transition(auto.getAllStates().getById(curr), input, auto.getAllStates().getById(endId)));
                                            } 
                                        }
                                    }
                                } else {
                                    //neither the current and the next states are present in the considered automaton --> add them to temp arrays
                                    if(!states.labelIsPresent(curr)) states.add(new State(curr));

                                    if(start != "" && start != "none"){
                                        if(!startStates.labelIsPresent(curr)) startStates.add(states.getById(curr));
                                    }
                                    for(int in = 0; in < orEndStates.size(); in++){        
                                        if(orEndStates.get(in).equals(curr)){
                                            if(!endStates.labelIsPresent(curr)) endStates.add(states.getById(curr));
                                            orEndStates.remove(in);
                                        }
                                    }
                                    
                                    for(int l = 0; l < nextStates.getLength(); l++){
                                        n = (Element) nextStates.item(l);
                                        nextId = n.getAttribute("element");
                                        if(!file[4].equals("SPM")){
                                            if(nextId.indexOf("__") >= 0){
                                                nextId = nextId.substring(2, nextId.length()-2);
                                            }
                                        }
                                        
                                        if(!states.labelIsPresent(nextId)) states.add(new State(nextId));

                                        if(!transitions.isPresent(curr, input, nextId)) transitions.add(new Transition(states.getById(curr), input, states.getById(nextId)));

                                        for(int in = 0; in < orEndStates.size(); in++){        
                                            if(orEndStates.get(in).equals(nextId)){
                                                if(!endStates.labelIsPresent(nextId)) endStates.add(states.getById(nextId));
                                                orEndStates.remove(in);
                                            }
                                        }
                                    }

                                    if(end.getLength() > 0){
                                        for(int k = 0; k < end.getLength(); k++){
                                            endId = "temp_"+curr+""+k;
                                            //end state is added to both temp states and temp endStates if not already present
                                            if(!states.labelIsPresent(endId)){
                                                states.add(new State(endId));
                                            }
                                            if(!endStates.labelIsPresent(endId)){
                                                endStates.add(states.getById(endId));
                                            }
                                            if(!transitions.isPresent(curr, input, endId)){
                                                transitions.add(new Transition(states.getById(curr), input, states.getById(endId)));
                                            }
                                            
                                        }
                                    }
                                }
                                merge(i);
                            }
                        }
                    }
                }
            }
            if(transitions.size() > 0 || startStates.size() > 0 || endStates.size() > 0 || states.size() > 0){
                checkTemp();
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        //check on all the generated automaton: there are common states/transitions with states contained in another automaton?

        GenerateOutput writeTemp = new GenerateOutput(file[5], "automata", "tempAutomaton_"+file[4], "json");
        writeTemp.writeFile("{\"automata\" : [");
        for(int i = 0; i < automata.size(); i++){
            if(i<automata.size()-1) writeTemp.writeFile(automata.get(i).automatonStructure(i)+",");
            else writeTemp.writeFile(automata.get(i).automatonStructure(i));
        }
        writeTemp.writeFile("]\n}");
        writeTemp.closeFile();
        int num = automata.size();
        automata.clear();
        transitions = null;
        startStates = null;
        endStates = null;
        orEndStates = null;
        automaton = null;
        automata = null;
        return num;
    }

    private void checkTemp(){
        //check if any of the states in states array is present in any of the already created automata
        //if they are not present, create a new automaton with the connected states, start, end, transitions

        for(int j = 0; j < automata.size(); j++){
            
            for(State s : states.getAll()){
                //if the state s is present in any automaton in automata array
                if(automata.get(j).checkState(s.getState())){
                    //if the state is also a start state, it is added to the start states array of the automaton
                    if(startStates.labelIsPresent(s.getState())){
                        if(!automata.get(j).checkStart(s.getState())) automata.get(j).addStart(s);
                        //if the state s is added to the start states array of an automaton, it has to be deleted from the temp start states array
                        startStates.removeById(s.getState());
                    }
                    //if the state is also an end states, it is added to the end states array of the automaton
                    if(endStates.labelIsPresent(s.getState())){
                        if(!automata.get(j).checkEnd(s.getState())) automata.get(j).addEnd(s);
                        //if the state s is added to the end states array of an automaton, it has to be deleted from the temp end states array
                        endStates.removeById(s.getState());
                    }

                    for(int in = 0; in < orEndStates.size(); in++){        
                        if(orEndStates.get(in).equals(s.getState())){
                            if(!automata.get(j).checkEnd(s)) automata.get(j).addEnd(automata.get(j).getAllStates().getById(s));
                            orEndStates.remove(in);
                        }
                    }

                    //check for transitions exiting from state s considered, that are not already present in the automaton
                    if(transitions.isCurr(s.getState())){
                        for(int k = 0; k < transitions.getCurr(s.getState()).size(); k++){
                            //if the automata does not contain the transition from transitions exiting from state s, add the transition to the transitions of the automaton
                            if(!automata.get(j).checkTransition(transitions.getCurr(s).get(k))){
                                automata.get(j).addTransition(transitions.getCurr(s).get(k));

                                //if the automaton does not contain the next state of the transition just added, then add it to the automaton
                                if(!automata.get(j).checkState(transitions.getCurr(s).get(k).getNext().getState())){
                                    automata.get(j).addState(states.getById(transitions.getCurr(s).get(k).getNext().getState()));
                                }
                                //if the next state is also a start state, it has to be added to the start states array of the automaton
                                if(startStates.stateIsPresent(states.getById(transitions.getCurr(s).get(k).getNext()))){
                                    if(!automata.get(j).checkStart(transitions.getCurr(s).get(k).getNext().getState())) automata.get(j).addStart(states.getById(transitions.getCurr(s).get(k).getNext()));
                                }

                                for(int in = 0; in < orEndStates.size(); in++){        
                                    if(orEndStates.get(in).equals(transitions.getCurr(s).get(k).getNext().getState())){
                                        if(!automata.get(j).checkEnd(transitions.getCurr(s).get(k).getNext().getState())) automata.get(j).addEnd(automata.get(j).getAllStates().getById(transitions.getCurr(s).get(k).getNext().getState()));
                                        orEndStates.remove(in);
                                    }
                                }
                            }
                        }
                    }
                    
                    //check for transitions entering in state s considered, that are not already present in the automaton
                    if(transitions.isNext(s.getState())){
                        for(int k = 0; k < transitions.getNext(s.getState()).size(); k++){
                            //if the automata does not contain the transition present in transitions entering in state s, add the transition to the transitions of the automaton
                            if(!automata.get(j).checkTransition(transitions.getNext(s).get(k))){
                                automata.get(j).addTransition(transitions.getNext(s.getState()).get(k));
                                //System.out.println("Line 465");
                                //automata.get(j).showAutomaton();
                                //if the automaton does not contain the current state of the transition just added, then add it to the automaton
                                if(!automata.get(j).checkState(transitions.getNext(s.getState()).get(k).getCurrent())){
                                    automata.get(j).addState(states.getById(transitions.getNext(s.getState()).get(k).getCurrent().getState()));
                                }
                                if(startStates.stateIsPresent(states.getById(transitions.getNext(s.getState()).get(k).getCurrent()))){
                                    automata.get(j).addStart(states.getById(transitions.getNext(s).get(k).getCurrent()));
                                }

                                for(int in = 0; in < orEndStates.size(); in++){        
                                    if(orEndStates.get(in).equals(transitions.getNext(s).get(k).getCurrent().getState())){
                                        if(!automata.get(j).checkEnd(transitions.getNext(s).get(k).getCurrent().getState())) automata.get(j).addEnd(automata.get(j).getAllStates().getById(transitions.getNext(s).get(k).getCurrent().getState()));
                                        orEndStates.remove(in);
                                    }
                                }
                            }
                        }
                    }
                    //remove from temp transitions all the transitions added above to an automaton
                    for(Transition t: automata.get(j).getTransitions().getAll()){
                        if(transitions.isPresent(t)) transitions.remove(t);
                    }
                }
            }
            //if the state s has been added to the automaton state array, then it has to be deleted from the temp state array
            for(State s: automata.get(j).getAllStates().getAll()){
                if(states.labelIsPresent(s.getState())) states.removeById(s.getState());
                if(startStates.labelIsPresent(s.getState())) startStates.removeById(s.getState());
            }
        }

        //if there are states in the temp start states array, then create a new automaton with the first start state in temp start states array as start state, then check for transitions and states connected to it, etc...
        if(startStates.size() > 0){
            //create a new automaton, add first start state to startstates and states of the automaton, then check for relative transitions and for end states
            automaton = new Automaton();
            //System.out.println("Start states and states at 439 (startstate: "+startStates.get(0).getState()+"): ");
            //startStates.showStates();
            //states.showStates();
            automaton.addStart(startStates.get(0));
            automaton.addState(startStates.get(0));
            /*if(startStates.get(0).getState().equals("1073") || startStates.get(0).getState().equals("1074")){
                                System.out.println("Start states and states at 450: ");
                                startStates.showStates();
                                states.showStates();
                            }*/
            //if in temp transitions some transitions with curr state equals to start state are present, then add the transitions to the array of transitions of the new automaton
            if(transitions.isCurr(startStates.get(0))){
                for(int k = 0; k < transitions.getCurr(startStates.get(0).getState()).size(); k++){
                    //if the automata does not contain the transition from transitions exiting from considered state, add the transition to the transitions of the automaton
                    if(!automaton.checkTransition(transitions.getCurr(startStates.get(0)).get(k))){
                        automaton.addTransition(transitions.getCurr(startStates.get(0)).get(k));
                        //if the automaton does not contain the next state of the transition just added, then add it to the automaton
                        if(!automaton.checkState(transitions.getCurr(startStates.get(0)).get(k).getNext())){
                            if(states.labelIsPresent(transitions.getCurr(startStates.get(0)).get(k).getNext().getState())){
                                automaton.addState(states.getById(transitions.getCurr(startStates.get(0)).get(k).getNext().getState()));
                            } else {
                                automaton.addState(startStates.get(0));
                            }
                            
                        }
                    }
                }
            }
            //if in temp transitions some transitions with nest state equals to start state are present, then add the transitions to the array of transitions of the new automaton
            if(transitions.isNext(startStates.get(0))){
                for(int k = 0; k < transitions.getNext(startStates.get(0).getState()).size(); k++){
                    //if the automata does not contain the transition present in transitions entering in state s, add the transition to the transitions of the automaton
                    if(!automaton.checkTransition(transitions.getNext(startStates.get(0)).get(k))){
                        automaton.addTransition(transitions.getNext(startStates.get(0)).get(k));

                        //if the automaton does not contain the current state of the transition just added, then add it to the automaton
                        if(!automaton.checkState(transitions.getNext(startStates.get(0)).get(k).getCurrent())){
                            automaton.addState(states.getById(transitions.getNext(startStates.get(0)).get(k).getCurrent()));
                        }
                    }
                }
            }

            if(endStates.size() > 0){
                for(State s: endStates.getAll()){
                    if(automaton.checkState(s.getState())){
                        automaton.addEnd(s);
                    } 
                }
            }

            for(int in = 0; in < orEndStates.size(); in++){
                if(automaton.checkState(orEndStates.get(in))){
                    if(!automaton.checkEnd(orEndStates.get(in))) automaton.addEnd(automaton.getAllStates().getById(orEndStates.get(in)));
                    orEndStates.remove(in);
                }
            }
            //remove from temp arrays all the states added to the new automaton
            for(State s : automaton.getAllStates().getAll()){
                if(startStates.labelIsPresent(s.getState())) startStates.removeById(s.getState());
                if(endStates.labelIsPresent(s.getState())) endStates.removeById(s.getState());
                if(states.labelIsPresent(s.getState())) states.removeById(s.getState());
                
            }
            //remove all the added transitions from the temp transitions array
            for(Transition t : automaton.getTransitions().getAll()){
                /*if(t.getCurrent().getState().equals("509") && t.getNext().getState().equals("510")){
                    System.out.println("Transitions before removing transition (485)");
                    transitions.showTransitions();
                    System.out.println("States before removing transition (487)");
                } */
                if(transitions.isPresent(t)) transitions.remove(t);
            }
            automata.add(automaton);
            automaton = null;
        }
        //if any of the curr or next states of any transition is present in states of any of the automaton in automata
        if(transitions.size() > 0){
            for(Transition t : transitions.getAll()){
                for(Automaton a: automata){
                    //add the transitions relative to the states present in the automaton
                    if(a.checkState(t.getCurrent().getState())){
                        if(!a.checkTransition(t)) a.addTransition(t);

                        if(!a.checkState(t.getNext().getState())) a.addState(t.getNext());

                        //if the state is also start or end, add to the start or end states array of the automaton
                        if(startStates.labelIsPresent(t.getCurrent().getState())){
                            if(!a.checkStart(t.getCurrent().getState())) a.addStart(t.getCurrent());
                            startStates.removeById(t.getCurrent().getState());
                        } 
                        if(startStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkStart(t.getNext().getState())) a.addStart(t.getNext());
                            startStates.removeById(t.getNext().getState());
                        }
                        
                        if(endStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkEnd(t.getNext().getState())) a.addEnd(t.getNext());
                            endStates.removeById(t.getNext().getState());
                        }
                    }
                    if(a.checkState(t.getNext().getState())){
                        if(!a.checkTransition(t)) a.addTransition(t);

                        if(!a.checkState(t.getCurrent().getState())) a.addState(t.getCurrent());

                        //if the state is also start or end, add to the start or end states array of the automaton
                        if(startStates.labelIsPresent(t.getCurrent().getState())){
                            if(!a.checkStart(t.getCurrent().getState())) a.addStart(t.getCurrent());
                            startStates.removeById(t.getCurrent().getState());
                        } 
                        if(startStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkStart(t.getNext().getState())) a.addStart(t.getNext());
                            startStates.removeById(t.getNext().getState());
                        }
                        
                        if(endStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkEnd(t.getNext().getState())) a.addEnd(t.getNext());
                            endStates.removeById(t.getNext().getState());
                        }
                    }
                    for(int in = 0; in < orEndStates.size(); in++){        
                        if(a.checkState(orEndStates.get(in))){
                            if(!a.checkEnd(orEndStates.get(in))) a.addEnd(a.getAllStates().getById(orEndStates.get(in)));
                            orEndStates.remove(in);
                        }
                    }

                }
            }

            //remove the transitions from temp transitions added to the automaton
            for(Automaton a: automata){
                for(Transition t: a.getTransitions().getAll()){
                    if(transitions.isPresent(t)) transitions.remove(t);
                }
            }

        }
    }

    public void merge(int i){
        //get the current automaton, compare its states to the states of the nearest automata (5 other automata), if another automaton has at least one equal state
        //merge them in a unique automaton
        int j = 0;
        int max = automata.size();
        int from = 0;
        int to = 0;
        if(i - 5 >= 0) j = i-5;
        if(i + 5 <= automata.size()) max = i+5;
        while(j < max){
            if(j != i){
                for(State s: automata.get(i).getAllStates().getAll()){
                    //if any of the states of the current automaton is contained in another automaton
                    //or if any of the states of the current automaton is present in at least one of the transitions of the current automaton (curr or next states)
                    //System.out.println("i: "+i+"; j: "+j+"; size of automata: "+automata.size());
                    if(automata.get(j).checkState(s) || automata.get(j).getTransitions().isCurr(s) || automata.get(j).getTransitions().isNext(s)){
                        if(automata.get(i).getTransitions().size()>automata.get(j).getTransitions().size()){
                            from = j;
                            to = i;
                            break;
                        } else {
                            from = i;
                            to = j;
                            break;
                        }
                    }
                    
                }
            }
            j++; 
        }

        if(from != to){
            //merge the states and transitions of "from" automaton into the "to" automaton
            for(State s : automata.get(from).getAllStates().getAll()){
                if(!automata.get(to).checkState(s)){
                    automata.get(to).addState(s);
                    if(automata.get(from).checkStart(s)) automata.get(to).addStart(s);
                    else if(automata.get(from).checkEnd(s)) automata.get(to).addEnd(s);
                }
            }
            for(Transition t : automata.get(from).getTransitions().getAll()){
                if(!automata.get(to).checkTransition(t)){
                    automata.get(to).addTransition(t);
                    if(!automata.get(to).checkState(t.getCurrent())){
                        automata.get(to).addState(t.getCurrent());
                        if(automata.get(from).checkStart(t.getCurrent())) automata.get(to).addStart(t.getCurrent());
                    else if(automata.get(from).checkEnd(t.getCurrent())) automata.get(to).addEnd(t.getCurrent());
                    } 
                    else if(!automata.get(to).checkState(t.getNext())){
                        automata.get(to).addState(t.getNext());
                        if(automata.get(from).checkStart(t.getNext())) automata.get(to).addStart(t.getNext());
                    else if(automata.get(from).checkEnd(t.getNext())) automata.get(to).addEnd(t.getNext());
                    } 
                }
            }
            automata.remove(from);
        }
    }
    /*
    private void checkAtStart(){
        //check if any of the states in states array is present in any of the already created automata
        //if they are not present, create a new automaton with the connected states, start, end, transitions

        for(int j = 0; j < automata.size(); j++){
            
            for(State s : states.getAll()){
                //if the state s is present in any automaton in automata array
                if(automata.get(j).checkState(s.getState())){
                    //if the state is also a start state, it is added to the start states array of the automaton
                    if(startStates.labelIsPresent(s.getState())){
                        if(!automata.get(j).checkStart(s.getState())) automata.get(j).addStart(s);
                        //if the state s is added to the start states array of an automaton, it has to be deleted from the temp start states array
                        startStates.removeById(s.getState());
                    }
                    //if the state is also an end states, it is added to the end states array of the automaton
                    if(endStates.labelIsPresent(s.getState())){
                        if(!automata.get(j).checkEnd(s.getState())) automata.get(j).addEnd(s);
                        //if the state s is added to the end states array of an automaton, it has to be deleted from the temp end states array
                        endStates.removeById(s.getState());
                    }

                    for(int in = 0; in < orEndStates.size(); in++){        
                        if(orEndStates.get(in).equals(s.getState())){
                            if(!automata.get(j).checkEnd(s)) automata.get(j).addEnd(automata.get(j).getAllStates().getById(s));
                            orEndStates.remove(in);
                        }
                    }

                    //check for transitions exiting from state s considered, that are not already present in the automaton
                    if(transitions.isCurr(s.getState())){
                        for(int k = 0; k < transitions.getCurr(s.getState()).size(); k++){
                            //if the automata does not contain the transition from transitions exiting from state s, add the transition to the transitions of the automaton
                            if(!automata.get(j).checkTransition(transitions.getCurr(s).get(k))){
                                automata.get(j).addTransition(transitions.getCurr(s).get(k));

                                //if the automaton does not contain the next state of the transition just added, then add it to the automaton
                                if(!automata.get(j).checkState(transitions.getCurr(s).get(k).getNext().getState())){
                                    automata.get(j).addState(states.getById(transitions.getCurr(s).get(k).getNext().getState()));
                                }
                                //if the next state is also a start state, it has to be added to the start states array of the automaton
                                if(startStates.stateIsPresent(states.getById(transitions.getCurr(s).get(k).getNext()))){
                                    if(!automata.get(j).checkStart(transitions.getCurr(s).get(k).getNext().getState())) automata.get(j).addStart(states.getById(transitions.getCurr(s).get(k).getNext()));
                                }

                                for(int in = 0; in < orEndStates.size(); in++){        
                                    if(orEndStates.get(in).equals(transitions.getCurr(s).get(k).getNext().getState())){
                                        if(!automata.get(j).checkEnd(transitions.getCurr(s).get(k).getNext().getState())) automata.get(j).addEnd(automata.get(j).getAllStates().getById(transitions.getCurr(s).get(k).getNext().getState()));
                                        orEndStates.remove(in);
                                    }
                                }
                            }
                        }
                    }
                    
                    //check for transitions entering in state s considered, that are not already present in the automaton
                    if(transitions.isNext(s.getState())){
                        for(int k = 0; k < transitions.getNext(s.getState()).size(); k++){
                            //if the automata does not contain the transition present in transitions entering in state s, add the transition to the transitions of the automaton
                            if(!automata.get(j).checkTransition(transitions.getNext(s).get(k))){
                                automata.get(j).addTransition(transitions.getNext(s.getState()).get(k));
                                //System.out.println("Line 465");
                                //automata.get(j).showAutomaton();
                                //if the automaton does not contain the current state of the transition just added, then add it to the automaton
                                if(!automata.get(j).checkState(transitions.getNext(s.getState()).get(k).getCurrent())){
                                    automata.get(j).addState(states.getById(transitions.getNext(s.getState()).get(k).getCurrent().getState()));
                                }
                                if(startStates.stateIsPresent(states.getById(transitions.getNext(s.getState()).get(k).getCurrent()))){
                                    automata.get(j).addStart(states.getById(transitions.getNext(s).get(k).getCurrent()));
                                }

                                for(int in = 0; in < orEndStates.size(); in++){        
                                    if(orEndStates.get(in).equals(transitions.getNext(s).get(k).getCurrent().getState())){
                                        if(!automata.get(j).checkEnd(transitions.getNext(s).get(k).getCurrent().getState())) automata.get(j).addEnd(automata.get(j).getAllStates().getById(transitions.getNext(s).get(k).getCurrent().getState()));
                                        orEndStates.remove(in);
                                    }
                                }
                            }
                        }
                    }
                    //remove from temp transitions all the transitions added above to an automaton
                    for(Transition t: automata.get(j).getTransitions().getAll()){
                        if(transitions.isPresent(t)) transitions.remove(t);
                    }
                }
            }
            //if the state s has been added to the automaton state array, then it has to be deleted from the temp state array
            for(State s: automata.get(j).getAllStates().getAll()){
                if(states.labelIsPresent(s.getState())) states.removeById(s.getState());
                if(startStates.labelIsPresent(s.getState())) startStates.removeById(s.getState());
            }
        }

        //if any of the curr or next states of any transition is present in states of any of the automaton in automata
        if(transitions.size() > 0){
            for(Transition t : transitions.getAll()){
                for(Automaton a: automata){
                    //add the transitions relative to the states present in the automaton
                    if(a.checkState(t.getCurrent().getState())){
                        if(!a.checkTransition(t)) a.addTransition(t);

                        if(!a.checkState(t.getNext().getState())) a.addState(t.getNext());

                        //if the state is also start or end, add to the start or end states array of the automaton
                        if(startStates.labelIsPresent(t.getCurrent().getState())){
                            if(!a.checkStart(t.getCurrent().getState())) a.addStart(t.getCurrent());
                            startStates.removeById(t.getCurrent().getState());
                        } 
                        if(startStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkStart(t.getNext().getState())) a.addStart(t.getNext());
                            startStates.removeById(t.getNext().getState());
                        }
                        
                        if(endStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkEnd(t.getNext().getState())) a.addEnd(t.getNext());
                            endStates.removeById(t.getNext().getState());
                        }
                    }
                    if(a.checkState(t.getNext().getState())){
                        if(!a.checkTransition(t)) a.addTransition(t);

                        if(!a.checkState(t.getCurrent().getState())) a.addState(t.getCurrent());

                        //if the state is also start or end, add to the start or end states array of the automaton
                        if(startStates.labelIsPresent(t.getCurrent().getState())){
                            if(!a.checkStart(t.getCurrent().getState())) a.addStart(t.getCurrent());
                            startStates.removeById(t.getCurrent().getState());
                        } 
                        if(startStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkStart(t.getNext().getState())) a.addStart(t.getNext());
                            startStates.removeById(t.getNext().getState());
                        }
                        
                        if(endStates.labelIsPresent(t.getNext().getState())){
                            if(!a.checkEnd(t.getNext().getState())) a.addEnd(t.getNext());
                            endStates.removeById(t.getNext().getState());
                        }
                    }
                    for(int in = 0; in < orEndStates.size(); in++){        
                        if(a.checkState(orEndStates.get(in))){
                            if(!a.checkEnd(orEndStates.get(in))) a.addEnd(a.getAllStates().getById(orEndStates.get(in)));
                            orEndStates.remove(in);
                        }
                    }

                }
            }

            //remove the transitions from temp transitions added to the automaton
            for(Automaton a: automata){
                for(Transition t: a.getTransitions().getAll()){
                    if(transitions.isPresent(t)) transitions.remove(t);
                }
            }

        }
    }
    */
}
