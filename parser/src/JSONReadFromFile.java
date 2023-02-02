import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
//import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
 
import java.io.FileReader;
import java.util.ArrayList;

 
public class JSONReadFromFile {
	
	public JSONReadFromFile() { }

	//@SuppressWarnings("unchecked")
	public Automaton getAutomaton(String file, int n) {
		System.out.println("Reading automaton "+n);
		Automaton a = new Automaton();
		JSONParser parser = new JSONParser();
		
		try {
			
			Object obj = parser.parse(new FileReader("../output/"+file+".json"));
			JSONObject o = (JSONObject) obj;
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
			JSONArray jsonObject = (JSONArray) o.get("automata");
			JSONObject json = (JSONObject) jsonObject.get(n);
 
			// A JSON array. JSONObject supports java.util.List interface.
			//String automata = json.get("automaton").toString();
			//System.out.println("Number of the automaton: "+automata.toString());
			
			JSONArray states = (JSONArray) json.get("states");
			for(int j = 0; j < states.size(); j++){
				String state = states.get(j).toString();
				//String id = state.get("id").toString();
				//System.out.println("Id of state: "+state);
				a.addState(new State(state));
				//System.out.println("Added state: "+state);
				state = null;
			}
			
            JSONArray start = (JSONArray) json.get("start");
			for(int j = 0; j < start.size(); j++){
				String state = start.get(j).toString();
				//String id = state.get("id").toString();
				//System.out.println("Id of start: "+state);
				a.addStart(a.getAllStates().getById(state));
				state = null;
			}

			JSONArray end = (JSONArray) json.get("end");
			for(int j = 0; j < end.size(); j++){
				String state = end.get(j).toString();
				//String id = state.get("id").toString();
				//System.out.println("Id of end: "+state);
				a.addEnd(a.getAllStates().getById(state));
				state = null;
			}
			JSONArray transitions = (JSONArray) json.get("transitions");
			for(int j = 0; j < transitions.size(); j++){
				JSONObject tr = (JSONObject) transitions.get(j);
				String curr = tr.get("current").toString();
				String input = tr.get("input").toString();
				String next = tr.get("next").toString();
				//String id = state.get("id").toString();
				//System.out.println("Transition: curr: "+curr+" input: "+input+" next: "+next);
				/*if(!a.checkState(curr) || !a.checkState(next)){
					GenerateOutput output = new GenerateOutput("error"+c, "error", "txt");
					output.writeFile("curr: "+curr);
					output.writeFile("next: "+next);
					output.writeFile("states in automaton: ");
					for(State s: a.getAllStates().getAll()){
						output.writeFile(s.getState());
					}
					output.writeFile("transition: curr "+curr+" input: "+input+ "next: "+next);
					c++;
					output.closeFile();
				}
				if(!a.checkState(curr) || !a.checkState(next)){
					GenerateOutput err = new GenerateOutput("error_json"+num, "error", "txt");
					if(!a.checkState(curr)){
						System.out.println("Curr not present. Curr: "+curr);
						err.writeFile("Automaton "+num+": error with curr "+curr);
					} 
					if(!a.checkState(next)){
						System.out.println("Next not present. Next: "+next);
						err.writeFile("Automaton "+num+": error with next "+next);
					}
					err.writeFile("States present: ");
					for(State s: a.getAllStates().getAll()){
						err.writeFile(s.getState()+", ");
					}
					err.closeFile();
				}*/
				
				//System.out.println("Input: "+input);
				a.addTransition(new Transition(a.getAllStates().getById(curr), input, a.getAllStates().getById(next)));

				//ArrayList<String> inp = new ArrayList<>();
				//inp.add(input);
				//a.addTransition(new Transition(a.getAllStates().getById(curr), inp, a.getAllStates().getById(next)));
				
				curr = null;
				input = null;
				next = null;
			}
 
			// An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
			// Iterators differ from enumerations in two ways:
			// 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
			// 2. Method names have been improved.
			//Iterator<JSONObject> iterator = transitions.iterator();
            //Iterator<JSONObject> it = end.iterator();

            //System.out.println(iterator.hasNext());
			/*
            Transition t = new Transition(new State("1"), "a", new State("1"));
            t.showTransition();
			while (iterator.hasNext()) {
                //int c = (JSONObject) iterator.get("current");
                transitionList.add(iterator.next());
				System.out.println(iterator.next());
			}

            while (it.hasNext()) {
                System.out.println(it.next());
			}*/
		} catch (Exception e) {
			e.printStackTrace();
			//write to error file the number of the automaton that had an error
		}
		//a.showAutomaton();
		return a;
	}

	public Integer sizeAutomata(String file) {
		//System.out.println("Reading automaton "+n);
		int num = -1;
		JSONParser parser = new JSONParser();
		
		try {
			
			Object obj = parser.parse(new FileReader("../output/"+file+".json"));
			JSONObject o = (JSONObject) obj;
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
			JSONArray jsonObject = (JSONArray) o.get("automata");
			num = jsonObject.size();
			
		} catch (Exception e) {
			e.printStackTrace();
			//write to error file the number of the automaton that had an error
		}
		//a.showAutomaton();
		return num;
	}

	public ArrayList<String> getAutomataNum(String name){
		ArrayList<String> fail = new ArrayList<>();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("../output/"+name+".json"));
			JSONObject o = (JSONObject) obj;
			JSONArray automat = (JSONArray) o.get("automata");
			//System.out.println("Size of automata in file failed.json: "+automat.size());
			for(int i = 0; i < automat.size(); i++){
				//System.out.println("Adding automaton :"+automat.get(i).toString());
				fail.add(automat.get(i).toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fail;
	}

	public Automaton getStates(String file, int n){
		Automaton a = new Automaton();
		JSONParser parser = new JSONParser();
		try {
			Object obj = parser.parse(new FileReader("parser/output/"+file+".json"));
			JSONObject o = (JSONObject) obj;
 
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
			JSONArray jsonObject = (JSONArray) o.get("automata");
			JSONObject json = (JSONObject) jsonObject.get(n);
 
			// A JSON array. JSONObject supports java.util.List interface.
			//String automata = json.get("automaton").toString();
			//System.out.println("Number of the automaton: "+automata.toString());
			
			JSONArray states = (JSONArray) json.get("states");
			for(int j = 0; j < states.size(); j++){
				String state = states.get(j).toString();
				//String id = state.get("id").toString();
				//System.out.println("Id of state: "+state);
				a.addState(new State(state));
				state = null;
			}
			
            JSONArray start = (JSONArray) json.get("start");
			for(int j = 0; j < start.size(); j++){
				String state = start.get(j).toString();
				//String id = state.get("id").toString();
				//System.out.println("Id of start: "+state);
				a.addStart(a.getAllStates().getById(state));
				state = null;
			}

			JSONArray end = (JSONArray) json.get("end");
			for(int j = 0; j < end.size(); j++){
				String state = end.get(j).toString();
				//String id = state.get("id").toString();
				//System.out.println("Id of end: "+state);
				a.addEnd(a.getAllStates().getById(state));
				state = null;
			}
 
			// An iterator over a collection. Iterator takes the place of Enumeration in the Java Collections Framework.
			// Iterators differ from enumerations in two ways:
			// 1. Iterators allow the caller to remove elements from the underlying collection during the iteration with well-defined semantics.
			// 2. Method names have been improved.
			//Iterator<JSONObject> iterator = transitions.iterator();
            //Iterator<JSONObject> it = end.iterator();

            //System.out.println(iterator.hasNext());
			/*
            Transition t = new Transition(new State("1"), "a", new State("1"));
            t.showTransition();
			while (iterator.hasNext()) {
                //int c = (JSONObject) iterator.get("current");
                transitionList.add(iterator.next());
				System.out.println(iterator.next());
			}

            while (it.hasNext()) {
                System.out.println(it.next());
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return a;
	}

    public static Transitions getIngoingTransitions(State s) {
		Transitions tr = new Transitions();
		//return only the transitions of the automaton n that has state s in them
        return tr;
    }

	public Transitions getOutgoingTransitions(State s) {
		Transitions tr = new Transitions();
		//return only the transitions of the automaton n that has state s in them
        return tr;
	}

}  


