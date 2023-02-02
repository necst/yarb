public class App {
    public static void main(String[] args) throws Exception {

        GUI m = new GUI();
        m.start();
        
       /* if(p != null){
            if(p[5].equals("ANMLZoo") || p[5].equals("GPU_NFA")){
                ANMLparser parser = new ANMLparser();
                try {
                    
                    
                } catch (Throwable e) {
                    e.printStackTrace();
                    
                    System.out.println("Start of parsing with arrays");
                    JSONReadFromFileArr read = new JSONReadFromFileArr();
                    GenerateOutput generator = new GenerateOutput(p[5], p[4], p[1], "regex");
                    TranslatorArray translator = new TranslatorArray();
                
                    ArrayList<String> r = new ArrayList<>();

                    //read.getAutomaton("tempAutomaton", 4).showAutomaton();
                    //for(int i = 0; i < 7; i++){
                    //    r = translator.translateToRegex(read.getAutomaton("tempAutomaton", i));
                    //System.out.println(r);
                    //}
                    int num;
                    File check = new File("../output/"+p[5]+"/automata/tempAutomaton_"+p[4]+".json");
                    if(!check.exists()){
                        num = parser.parse(p);
                        System.out.println("Check if file exists: "+check.exists());
                    } else {
                        num = read.sizeAutomata(p[5]+"/automata/tempAutomaton_"+p[4]);
                        System.out.println("Size of tempAutomaton_"+p[4]+" is: "+num);
                        System.out.println("Check if file exists: "+check.exists());
                    }
                    
                    //int num = 2585; //Snort
                    //int num = 2340; //Protomata
                    for(int i = 0; i < num; i ++){
                        //translator = new Translator();
                        
                        //if(p[3].equals("true")) order = true;
                        //else order = false;
                        r = translator.translateToRegex(read.getAutomaton(p[5]+"/automata/tempAutomaton_"+p[4], i), i, p[3], p[6]);
                        
                        //translator = null;
                        //System.out.println("Size of regex: "+r.size());
                        if(r.size() > 1){
                            for(int j = 0; j < r.size(); j++){
                                //System.out.println("Writing regex: "+j);
                                generator.writeFileRow(r.get(j));
                            }
                            generator.writeFileRow("\n");
                        } else if(r.size() == 1){
                            generator.writeFile(r.get(0));
                        } else {
                            System.out.println("Size of regex not compatible");
                        }
                        
                        
                        //r = null;
                    }
                    generator.closeFile();
                    System.out.println("Size of automata: "+num);
                    System.out.println("Parsed with arrays: "+p[1]+" "+p[5]);
                
                }
                
            } else if(p[5].equals("AutomataZoo")){

                
                try {
                    JSONReadFromFile read = new JSONReadFromFile();
                    GenerateOutput generator = new GenerateOutput(p[5], p[4], p[1], "regex");
                    Translator translator = new Translator();
                    ArrayList<String> r = new ArrayList<>();

                    int num;
                    File check = new File("../output/"+p[5]+"/automata/tempAutomaton_"+p[4]+".json");

                    if(Arrays.stream(mnrl).anyMatch(p[4]::equals)){
                        System.out.println("Matches");
                        MNRLparser parser = new MNRLparser();
                        if(!check.exists()){
                            num = parser.parse(p);
                            System.out.println("Check if file exists: "+check.exists());
                        } else {
                            num = read.sizeAutomata(p[5]+"/automata/tempAutomaton_"+p[4]);
                            System.out.println("Size of tempAutomaton_"+p[4]+" is: "+num);
                            System.out.println("Check if file exists: "+check.exists());
                        }
                    } else {
                        System.out.println("Does not match");
                        ANMLparser parser = new ANMLparser();
                        if(!check.exists()){
                            num = parser.parse(p);
                            System.out.println("Check if file exists: "+check.exists());
                        } else {
                            num = read.sizeAutomata(p[5]+"/automata/tempAutomaton_"+p[4]);
                            System.out.println("Size of tempAutomaton_"+p[4]+" is: "+num);
                            System.out.println("Check if file exists: "+check.exists());
                        }
                    }
                    
                    for(int i = 0; i < num; i ++){
                        r = translator.translateToRegex(p[5], p[4], i, p[3], p[6], p[5]+"/"+p[4]+"/failed");
                        if(r==null){
                            generator.writeFile("null");
                        } else {
                            if(r.size() > 1){
                                for(int j = 0; j < r.size(); j++){
                                    generator.writeFileRow(r.get(j));
                                }
                                generator.writeFileRow("\n");
                            } else if(r.size() == 1){
                                generator.writeFile(r.get(0));
                            } else {
                                System.out.println("Size of regex not compatible");
                            }
                        }
                        //translator = null;
                        //r = null;
                    }
                    generator.closeFile();
                    System.out.println("Size of automata: "+num);

                    System.out.println("Parsed: "+p[1]+" "+p[5]);
                    //Translator translator = new Translator();
                    //System.out.println("Final regex: "+translator.translateToRegex(new Automaton()));
                } catch (Exception e) {
                    e.printStackTrace();
                    /*
                    System.out.println("Start of parsing with arrays");
                    JSONReadFromFileArr read = new JSONReadFromFileArr();
                    GenerateOutput generator = new GenerateOutput(p[5], p[4], p[1], "regex");
                    TranslatorArray translator = new TranslatorArray();
                    
                    ArrayList<String> r = new ArrayList<>();

                    //read.getAutomaton("tempAutomaton", 4).showAutomaton();
                    //for(int i = 0; i < 7; i++){
                    //    r = translator.translateToRegex(read.getAutomaton("tempAutomaton", i));
                    //System.out.println(r);
                    //}
                    int num;
                    File check = new File("../output/"+p[5]+"/automata/tempAutomaton_"+p[4]+".json");
                    if(!check.exists()){
                        num = parser.parse(p);
                        System.out.println("Check if file exists: "+check.exists());
                    } else {
                        num = read.sizeAutomata(p[5]+"/automata/tempAutomaton_"+p[4]);
                        System.out.println("Size of tempAutomaton_"+p[4]+" is: "+num);
                        System.out.println("Check if file exists: "+check.exists());
                    }
                    
                    
                    //int num = 2585; //Snort
                    //int num = 2340; //Protomata
                    for(int i = 0; i < num; i ++){
                        //translator = new Translator();
                        
                        //if(p[3].equals("true")) order = true;
                        //else order = false;
                        r = translator.translateToRegex(read.getAutomaton(p[5]+"/automata/tempAutomaton_"+p[4], i), i, p[3], p[6]);
                        
                        if(r.size() > 1){
                            for(int j = 0; j < r.size(); j++){
                                generator.writeFileRow(r.get(j));
                            }
                            generator.writeFileRow("\n");
                        } else if(r.size() == 1){
                            generator.writeFile(r.get(0));
                        } else {
                            System.out.println("Size of regex not compatible");
                        }
                        //translator = null;
                        //r = null;
                    }
                    generator.closeFile();
                    System.out.println("Size of automata: "+num);

                    System.out.println("Parsed with arrays: "+p[1]+" "+p[5]);
                    //Translator translator = new Translator();
                    //System.out.println("Final regex: "+translator.translateToRegex(new Automaton()));
                    
                }
                
            } else {
                System.out.println("Not supported yet");
            }
            
        }*/
    }
}
