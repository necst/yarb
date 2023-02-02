import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;

public class MNRLparser {
    ArrayList<Automaton> automata = new ArrayList<>();

    public Integer parse(String[] file){
        //in MNRL file
        //get the array on "nodes"
        //for each node:
        //"attributes" contains "reportId" (position in automata arrayList) and "symbolSet" (input for the transition exiting from current state)
        //"enable" if it is set to "always" the current state is a start state
        //"id" corresponds to the id of the current state
        //"outputDefs" contains the array "activate" with the states reached from the current state
        //"report" is a boolean value that states if the current state is an ending state
        JSONParser parser = new JSONParser();

        try{
            Object obj = parser.parse(new FileReader(file[0]));
            JSONObject o = (JSONObject) obj;
            JSONArray jsonObject = (JSONArray) o.get("nodes");
            //System.out.println("Size of nodes: "+jsonObject.size());
            for(int i = 0; i < jsonObject.size(); i++){
                JSONObject json = (JSONObject) jsonObject.get(i);

                //if the current state is a start state
                String start = json.get("enable").toString();
                

                //id of the current state
                String id = json.get("id").toString();
                //System.out.println("Id of current state: "+id);
                State curr = new State(id);
                JSONObject attributes = (JSONObject) json.get("attributes");
                //ending position of the automaton
                int pos = Integer.parseInt(attributes.get("reportId").toString());
                
                
                if(pos > automata.size()){
                    //add null positions in the arraylist and add a new automaton in the correponding pos position
                    Automaton a = new Automaton();
                    automata.addAll(Collections.nCopies(pos - automata.size(), null));
                    automata.add(pos, a);
                    System.out.println("reportId: "+pos);
                    System.out.println("Size of automata at 51: "+automata.size());
                } else if(pos == automata.size()){
                    //append the automaton at the end of automata arraylist
                    Automaton a = new Automaton();
                    automata.add(pos, a);
                    System.out.println("reportId: "+pos);
                    System.out.println("Size of automata at 58: "+automata.size());
                } else {
                    if(automata.get(pos) == null){
                        //create a new automaton in that position and add the current state ...
                        Automaton a = new Automaton();
                        automata.remove(pos);
                        automata.add(pos, a);
                    //} else {
                        //add the current state and ... to the automaton contained in automata at pos
                        //automata.get(pos).addState(s);
                    //}
                    }
                    System.out.println("reportId: "+pos);
                    System.out.println("Size of automata at 70: "+automata.size());
                }
            

                Boolean isStart = start.equals("always");
                //System.out.println("Is a start state: "+isStart);

                //add curr state to the automaton at position pos
                if(!automata.get(pos).checkState(curr)){
                    automata.get(pos).addState(curr);
                }
                
                //if curr is a start state then add curr state as start state to the automaton at position pos
                if(isStart){
                    //System.out.println("Enter start always: "+id);
                    if(!automata.get(pos).checkStart(id)){
                        //System.out.println("Enter checkStart");
                        automata.get(pos).addStart(automata.get(pos).getAllStates().getById(curr));
                        //automata.get(pos).getStartStates().showStates();
                    }
                }

                //if the current state is an ending state
                boolean end = (Boolean) json.get("report");
                //System.out.println("It is an ending state? "+end);
                

                //input for the transitions exiting from the current state
                String input = attributes.get("symbolSet").toString();
                //System.out.println("position of automaton: "+pos);
                //System.out.println("input string of automaton: "+input);

                int ind = input.indexOf("[^\\n]");
                if(ind >= 0){
                    if(ind > 0 && ind < input.length()-5){
                        input = input.substring(0, ind-1) + "(.)" + input.substring(ind + 5);
                    } else {
                        input = "(.)";
                    }
                }

                ind = input.indexOf("\\n");
                if(ind >= 0){
                    if(ind > 0 && ind < input.length()-2){
                        input = input.substring(0, ind-1) + "( )" + input.substring(ind + 2);
                    } else {
                        input = "( )";
                    }
                }

                if(input.equals("*")){
                    input = "\\x2A";
                }

                if(input.equals("|")){
                    input = "\\x7c";
                }

                /*ind = input.indexOf("\"");
                if(ind >= 0){
                    if(ind > 0 && ind < input.length()-2){
                        input = input.substring(0, ind-1) + "\\\"" + input.substring(ind + 1);
                    } else {
                        input = "( )";
                    }
                }*/
                JSONObject jObject = new JSONObject();
                input = jObject.escape(input);

                JSONArray outputDefs = (JSONArray) json.get("outputDefs");
                //System.out.println("Size of outputDefs: "+outputDefs.size());
                for(int j = 0; j < outputDefs.size(); j++){
                    JSONObject out = (JSONObject) outputDefs.get(j);
                    JSONArray activate = (JSONArray) out.get("activate");
                    for(int z = 0; z < activate.size(); z++){
                        JSONObject next = (JSONObject) activate.get(z);
                        //id of the next state
                        String nextId = next.get("id").toString();
                        //System.out.println("Next id of "+id+": "+nextId);
                        //add the next state to the correponding automaton if not already present
                        if(!automata.get(pos).checkState(nextId)){
                            automata.get(pos).addState(new State(nextId));
                        }
                        State c = automata.get(pos).getAllStates().getById(curr);
                        State n = automata.get(pos).getAllStates().getById(nextId);
                        if(!automata.get(pos).checkTransition(c, input, n)){
                            automata.get(pos).addTransition(new Transition(c, input, n));
                        }
                        
                    }
                    if(activate.size() == 0 && end){
                        //add a temp final state
                        String endId = "temp_"+id;
                        if(!automata.get(pos).checkState(endId)){
                            automata.get(pos).addState(new State(endId));
                        }
                        if(!automata.get(pos).checkEnd(endId)){
                            automata.get(pos).addEnd(automata.get(pos).getAllStates().getById(endId));
                        }
                        State c = automata.get(pos).getAllStates().getById(curr);
                        State e = automata.get(pos).getAllStates().getById(endId);
                        if(!automata.get(pos).checkTransition(c, input, e)){
                            automata.get(pos).addTransition(new Transition(c, input, e));
                        }
                    }
                }
            if(pos == 10){
                System.out.println("Automaton 10");
                automata.get(pos).showAutomaton();
            }
        }

        

        } catch(Exception e){
            e.printStackTrace();
        }
        for(int i = 0; i < automata.size(); i++){
            if(automata.get(i) != null){
                //System.out.println("Position of automaton: "+i);
                //automata.get(i).showAutomaton();
            }
        }
        GenerateOutput writeTemp = new GenerateOutput(file[5], "automata", "tempAutomaton_"+file[4], "json");
        writeTemp.writeFile("{\"automata\" : [");
        int num = 0;
        for(int i = 0; i < automata.size(); i++){
            if(automata.get(i) != null) {
                num++;
                if(i<automata.size()-1) { writeTemp.writeFile(automata.get(i).automatonStructure(i)+",");}
                else writeTemp.writeFile(automata.get(i).automatonStructure(i));
            } else {
                //System.out.println("Automaton null at position: "+i);
            }
        }
        writeTemp.writeFile("]\n}");
        writeTemp.closeFile();

        //System.out.println("Size of automata: "+num);
        return num;
    }
}
