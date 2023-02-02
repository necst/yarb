import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RegexLengthComparison {

    private static final String[] ANML = {"Brill", "ClamAV", "Dotstar", "EntityResolution", "Fermi", "Hamming", "Levenshtein", "PowerEn", "Protomata", "RandomForest", "Snort", "SPM", "Synthetic Block Rings", "Synthetic Core Rings"};
    private static final String[] nameANML = {"brill", "515_nocounter", "backdoor_dotstar", "1000", "fermi_2400", "93_20X3", "24_20x3", "complx_01000_00123", "2340sigs", "rf", "snort", "bible_size4", "BlockRings", "CoreRings"};
    private static final String[] Automata = {"APPRNG", "APPRNG", "Brill", "ClamAV", "CRISPR", "CRISPR", "EntityResolution", "FileCarving", "Hamming", "Hamming", "Hamming", "Levenshtein", "Levenshtein", "Levenshtein", "Protomata", "RandomForest", "RandomForest", "RandomForest", "SeqMatch", "SeqMatch", "SeqMatch", "SeqMatch", "Snort", "YARA", "YARA"};
    private static final String[] nameAutomata = {"apprng_n1000_d4", "apprng_n1000_d8", "brill", "clamav", "CRISPR_CasOFFinder_2000", "CRISPR_CasOT_2000", "er_10000names", "file_carver", "ham_1000_3", "ham_1000_5", "ham_1000_10", "lev_1000_3", "lev_1000_5", "lev_1000_10", "protomata", "rf_20_400_200", "rf_20_400_270", "rf_20_800_200", "6wide_6pad", "6wide_6pad_counters", "6wide_10pad", "6wide_10pad_counters", "snort", "YARA", "YARA_wide"};
    private static final Integer[] mnrl_Auto = {2, 14, 22, 23};
    private static final String[] GPU_NFA = {"Bro217", "Dotstar03", "Dotstar06", "Dotstar09", "ExactMath", "Ranges1", "Ranges05", "TCP"};
    private static final String[] name_GPU = {"bro217", "dotstar03", "dotstar06", "dotstar09", "exactmath", "ranges1", "ranges05", "tcp"};
    private static final String[] type = {"", "_degree_minimization_once", "_degree_minimization", "_serial_minimization_once", "_serial_minimization", "_bridge_minimization_once", /*"_bridge_minimization",*/ "_weight_minimization_once", "_weight_minimization"};
    private static final String[] mex = {"without minimization", "with degree minimization only once", "with degree minimization iterated", "with serial minimization only once", "with serial minimization iterated", "with bridge minimization only once", /*"with bridge minimization iterated",*/ "with weight minimization only once", "with weight minimization iterated"};
    private static final String[] suite = {"ANMLZoo", "AutomataZoo", "GPU_NFA"};
    //get each regex from the .regex file
    //get its lenght and sum them all
    //compute the mean value

    public static void main(String args[]) {  
        //int mean =  0;
        double sum = 0;
        double count = 0;
        double min = 0;
        int n;
        int a;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("Which benchmark suite do you want to parse? (1-3)");
            for(int i = 0; i < suite.length; i++){
                System.out.println((i+1)+") "+suite[i]);
            }
            System.out.println("4) Both benchmark suites and write results on a file");
            System.out.print("Insert the number (1-4) of the benchmark suite: ");
            a = scanner.nextInt();
            if(a < 1 || a > 4) System.out.println("Insert a valid input");
        } while (a < 1 || a > 4);
        if(a==1){
            do{
                System.out.println("Choose which benchmark of ANMLZoo to compute the mean length for:");
                 for(int i = 0; i < ANML.length; i++){
                     System.out.println((i+1)+") "+ANML[i]);
                 }
                 System.out.println("15) All the benchmarks");
                 System.out.print("Insert the number (1-15) of the benchmark:");
                 n = scanner.nextInt();
                 if(n < 1 || n > 15) System.out.println("Insert a valid input"); 
             } while(n < 1 || n > 15);
            try{
                if(n == 15){
                    GenerateOutput generator = new GenerateOutput("ANMLZoo/mean_regex_length", "txt");
                    ArrayList<Double> without_min = new ArrayList<>();
                    ArrayList<Double> degree_min = new ArrayList<>();
                    ArrayList<Double> degree_min_once = new ArrayList<>();
                    ArrayList<Double> serial_min = new ArrayList<>();
                    ArrayList<Double> serial_min_once = new ArrayList<>();
                    //ArrayList<Double> bridge_min = new ArrayList<>();
                    ArrayList<Double> bridge_min_once = new ArrayList<>();
                    ArrayList<Double> weight_min = new ArrayList<>();
                    ArrayList<Double> weight_min_once = new ArrayList<>();
                    try {
                        for(int i = 0; i < ANML.length-2; i++){
                            ArrayList<String> failed = new ArrayList<>();
                            File check = new File("../output/ANMLZoo/"+ANML[i]+"/failed.json");
                            if(check.exists()){
                                JSONReadFromFile read = new JSONReadFromFile();
                                failed.addAll(read.getAutomataNum("ANMLZoo/"+ANML[i]+"/failed"));
                            }
                            for(int j = 0; j <type.length; j++){
                                File file = new File("../output/ANMLZoo/"+ANML[i]+"/"+nameANML[i]+type[j]+".regex");    //creates a new file instance  
                                FileReader fr = new FileReader(file);   //reads the file  
                                BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                                //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                                String line;
                                int pos = 0;
                                while((line=br.readLine())!=null) {  
                                    if(!failed.contains(pos+"")){
                                        //sb.append(line);      //appends line to string buffer 
                                        //System.out.println("Size of regex: "+line.length());
                                        sum = sum + line.length();
                                        //sb.append("\n");     //line feed 
                                        count++;
                                        if(min == 0 || min > line.length()){
                                            min = line.length();
                                        }
                                    }
                                    pos++;
                                }
                                if(j==0){
                                    without_min.add(mean(sum, count));
                                } else if(j==1){
                                    degree_min_once.add(mean(sum, count));
                                } else if(j==2){
                                    degree_min.add(mean(sum, count));
                                } else if(j==3){
                                    serial_min_once.add(mean(sum, count));
                                } else if(j==4){
                                    serial_min.add(mean(sum, count));
                                } else if(j==5){
                                    bridge_min_once.add(mean(sum, count));
                                } else if(j==6){
                                    weight_min_once.add(mean(sum, count));
                                } else {
                                    weight_min.add(mean(sum, count));
                                }
                                fr.close();
                                sum = 0;
                                count = 0;
                                System.out.println("Benchmark considered: "+nameANML[i]+" and type: "+type[j]);
                            }


                        }
                        generator.writeFileRow("without_min=[");
                        for(int i = 0; i < without_min.size(); i ++){
                            if(i < without_min.size()-1){
                                generator.writeFileRow(without_min.get(i)+", ");
                            } else {
                                generator.writeFile(without_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("degree_min_once=[");
                        for(int i = 0; i < degree_min_once.size(); i ++){
                            if(i < degree_min_once.size()-1){
                                generator.writeFileRow(degree_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(degree_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("degree_min=[");
                        for(int i = 0; i < degree_min.size(); i ++){
                            if(i < degree_min.size()-1){
                                generator.writeFileRow(degree_min.get(i)+", ");
                            } else {
                                generator.writeFile(degree_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("serial_min_once=[");
                        for(int i = 0; i < serial_min_once.size(); i ++){
                            if(i < serial_min_once.size()-1){
                                generator.writeFileRow(serial_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(serial_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("serial_min=[");
                        for(int i = 0; i < serial_min.size(); i ++){
                            if(i < serial_min.size()-1){
                                generator.writeFileRow(serial_min.get(i)+", ");
                            } else {
                                generator.writeFile(serial_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("bridge_min_once=[");
                        for(int i = 0; i < bridge_min_once.size(); i ++){
                            if(i < bridge_min_once.size()-1){
                                generator.writeFileRow(bridge_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(bridge_min_once.get(i)+"],");
                            }
                        }/*
                        generator.writeFileRow("bridge_min=[");
                        for(int i = 0; i < bridge_min.size(); i ++){
                            if(i < bridge_min.size()-1){
                                generator.writeFileRow(bridge_min.get(i)+", ");
                            } else {
                                generator.writeFile(bridge_min.get(i)+"],");
                            }
                        }*/
                        generator.writeFileRow("weight_min_once=[");
                        for(int i = 0; i < weight_min_once.size(); i ++){
                            if(i < weight_min_once.size()-1){
                                generator.writeFileRow(weight_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(weight_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("weight_min=[");
                        for(int i = 0; i < weight_min.size(); i ++){
                            if(i < weight_min.size()-1){
                                generator.writeFileRow(weight_min.get(i)+", ");
                            } else {
                                generator.writeFile(weight_min.get(i)+"],");
                            }
                        }
                        
                    } catch (Exception e) {
                        System.out.println("Benchmark not present");
                    }

                    generator.closeFile();

                    //open file
                    //compute all the values and add them into arrays
                    //write the results in arrays (without min, degree min, bridge min, weight min)
                    //close file
                } else {
                    for(int i = 0; i < type.length; i++){
                        ArrayList<String> failed = new ArrayList<>();
                        File check = new File("../output/ANMLZoo/"+ANML[n-1]+"/failed.json");
                        System.out.println("Check if "+ANML[n-1]+" exists: "+check.exists());
                        if(check.exists()){
                            JSONReadFromFile read = new JSONReadFromFile();
                            failed.addAll(read.getAutomataNum("ANMLZoo/"+ANML[n-1]+"/failed"));
                        }
                        //System.out.println("Path: ../output/ANMLZoo/"+ANML[n-1]+"/"+nameANML[n-1]+type[i]+".regex");
                        File file = new File("../output/ANMLZoo/"+ANML[n-1]+"/"+nameANML[n-1]+type[i]+".regex");    //creates a new file instance  
                        FileReader fr = new FileReader(file);   //reads the file  
                        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                        //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                        String line;
                        int pos = 0;
                        int tot = 0;
                        System.out.print("States to not consider: ");
                        for(String s : failed){
                            System.out.print(s+", ");
                        }
                        String s = pos+"";
                        while((line=br.readLine())!=null) {
                            s = pos+"";
                            if(!failed.contains(s)){
                                //System.out.println("Line considered: "+s);
                                //sb.append(line);      //appends line to string buffer 
                                //System.out.println("Size of regex: "+line.length());
                                sum = sum + line.length();
                                //sb.append("\n");     //line feed 
                                count++;
                                if(min == 0 || min > line.length()){
                                    min = line.length();
                                }
                            } else {
                                System.out.println("Line NOT considered: "+pos);
                            }
                            pos++;
                            tot++;
                        }  
                        fr.close();
                        //closes the stream and release the resources
                        System.out.println("Total count with failed: "+tot);
                        System.out.println("Count: "+count);
                        System.out.println("Sum: "+sum);
                        System.out.println("Mean length for "+ANML[n-1]+" "+mex[i]+": "+mean(sum, count));
                        //System.out.println("Sum: "+sum);
                        //System.out.println("Count: "+count); 
                        //System.out.println("Mean length: "+mean(sum, count));
                        sum = 0;
                        count = 0;
                    }
                }
                
                
                //System.out.println("Contents of File: ");  
                //System.out.println(sb.toString());   //returns a string that textually represents the object  
            }  
            catch(IOException e) { 
                System.out.println("Benchmark not present");
                //e.printStackTrace();  
            }
        } else if (a==2) {
            do{
                System.out.println("Choose which benchmark of AutomataZoo to compute the mean length for:");
                 for(int i = 0; i < Automata.length; i++){
                     System.out.println((i+1)+") "+Automata[i]);
                 }
                 System.out.println("26) All the benchmarks");
                 System.out.print("Insert the number (1-26) of the benchmark:");
                 n = scanner.nextInt();
                 if(n < 1 || n > 26) System.out.println("Insert a valid input"); 
             } while(n < 1 || n > 26);
            try{
                if(n == 26){
                    GenerateOutput generator = new GenerateOutput("AutomataZoo/mean_regex_length", "txt");
                    ArrayList<Double> without_min = new ArrayList<>();
                    ArrayList<Double> degree_min = new ArrayList<>();
                    ArrayList<Double> degree_min_once = new ArrayList<>();
                    ArrayList<Double> serial_min = new ArrayList<>();
                    ArrayList<Double> serial_min_once = new ArrayList<>();
                    //ArrayList<Double> bridge_min = new ArrayList<>();
                    ArrayList<Double> bridge_min_once = new ArrayList<>();
                    ArrayList<Double> weight_min = new ArrayList<>();
                    ArrayList<Double> weight_min_once = new ArrayList<>();
                    try {
                        for(int i = 0; i < mnrl_Auto.length; i++){
                            ArrayList<String> failed = new ArrayList<>();
                            File check = new File("../output/AutomataZoo/"+Automata[mnrl_Auto[i]]+"/failed.json");
                            if(check.exists()){
                                JSONReadFromFile read = new JSONReadFromFile();
                                failed.addAll(read.getAutomataNum("AutomataZoo/"+Automata[mnrl_Auto[i]]+"/failed"));
                            }
                            for(int j = 0; j <type.length; j++){
                                File file = new File("../output/AutomataZoo/"+Automata[mnrl_Auto[i]]+"/"+nameAutomata[mnrl_Auto[i]]+type[j]+".regex");    //creates a new file instance  
                                FileReader fr = new FileReader(file);   //reads the file  
                                BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                                //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                                String line;
                                int pos = 0;
                                while((line=br.readLine())!=null) {  
                                    if(!failed.contains(pos+"")){
                                        //sb.append(line);      //appends line to string buffer 
                                        //System.out.println("Size of regex: "+line.length());
                                        sum = sum + line.length();
                                        //sb.append("\n");     //line feed 
                                        count++;
                                        if(min == 0 || min > line.length()){
                                            min = line.length();
                                        }
                                    }
                                    pos++;
                                }
                                if(j==0){
                                    without_min.add(mean(sum, count));
                                } else if(j==1){
                                    degree_min_once.add(mean(sum, count));
                                } else if(j==2){
                                    degree_min.add(mean(sum, count));
                                } else if(j==3){
                                    serial_min_once.add(mean(sum, count));
                                } else if(j==4){
                                    serial_min.add(mean(sum, count));
                                } else if(j==5){
                                    bridge_min_once.add(mean(sum, count));
                                } else if(j==6){
                                    weight_min_once.add(mean(sum, count));
                                } else {
                                    weight_min.add(mean(sum, count));
                                }
                                fr.close();
                                sum = 0;
                                count = 0;
                                System.out.println("Benchmark considered: "+nameAutomata[mnrl_Auto[i]]+" and type: "+type[j]);
                            }

                        }
                        generator.writeFileRow("without_min=[");
                        for(int i = 0; i < without_min.size(); i ++){
                            if(i < without_min.size()-1){
                                generator.writeFileRow(without_min.get(i)+", ");
                            } else {
                                generator.writeFile(without_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("degree_min_once=[");
                        for(int i = 0; i < degree_min_once.size(); i ++){
                            if(i < degree_min_once.size()-1){
                                generator.writeFileRow(degree_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(degree_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("degree_min=[");
                        for(int i = 0; i < degree_min.size(); i ++){
                            if(i < degree_min.size()-1){
                                generator.writeFileRow(degree_min.get(i)+", ");
                            } else {
                                generator.writeFile(degree_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("serial_min_once=[");
                        for(int i = 0; i < serial_min_once.size(); i ++){
                            if(i < serial_min_once.size()-1){
                                generator.writeFileRow(serial_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(serial_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("serial_min=[");
                        for(int i = 0; i < serial_min.size(); i ++){
                            if(i < serial_min.size()-1){
                                generator.writeFileRow(serial_min.get(i)+", ");
                            } else {
                                generator.writeFile(serial_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("bridge_min_once=[");
                        for(int i = 0; i < bridge_min_once.size(); i ++){
                            if(i < bridge_min_once.size()-1){
                                generator.writeFileRow(bridge_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(bridge_min_once.get(i)+"],");
                            }
                        }/*
                        generator.writeFileRow("bridge_min=[");
                        for(int i = 0; i < bridge_min.size(); i ++){
                            if(i < bridge_min.size()-1){
                                generator.writeFileRow(bridge_min.get(i)+", ");
                            } else {
                                generator.writeFile(bridge_min.get(i)+"],");
                            }
                        }*/
                        generator.writeFileRow("weight_min_once=[");
                        for(int i = 0; i < weight_min_once.size(); i ++){
                            if(i < weight_min_once.size()-1){
                                generator.writeFileRow(weight_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(weight_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("weight_min=[");
                        for(int i = 0; i < weight_min.size(); i ++){
                            if(i < weight_min.size()-1){
                                generator.writeFileRow(weight_min.get(i)+", ");
                            } else {
                                generator.writeFile(weight_min.get(i)+"],");
                            }
                        }
                        
                    } catch (Exception e) {
                        System.out.println("Benchmark not present");
                    }

                    generator.closeFile();

                    //open file
                    //compute all the values and add them into arrays
                    //write the results in arrays (without min, degree min, bridge min, weight min)
                    //close file
                } else {
                ArrayList<String> failed = new ArrayList<>();
                File check = new File("../output/AutomataZoo/"+Automata[n-1]+"/failed.json");
                if(check.exists()){
                    JSONReadFromFile read = new JSONReadFromFile();
                    failed.addAll(read.getAutomataNum("AutomataZoo/"+Automata[n-1]+"/failed"));
                }
                for(int i = 0; i < type.length; i++){
                    //System.out.println("Path: ../output/ANMLZoo/"+ANML[n-1]+"/"+nameANML[n-1]+type[i]+".regex");
                    File file = new File("../output/AutomataZoo/"+Automata[n-1]+"/"+nameAutomata[n-1]+type[i]+".regex");    //creates a new file instance  
                    FileReader fr = new FileReader(file);   //reads the file  
                    BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                    //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                    String line;
                    int pos = 0;
                    System.out.print("States to not consider: ");
                    for(String s : failed){
                        System.out.print(s+", ");
                    }
                    String s = pos+"";
                    while((line=br.readLine())!=null) {
                        if(!failed.contains(s)){
                            sum = sum + line.length();
                            //sb.append("\n");     //line feed 
                            count++;
                            if(min == 0 || min > line.length()){
                                min = line.length();
                            }
                        }
                        pos++;
                        //sb.append(line);      //appends line to string buffer 
                        //System.out.println("Size of regex: "+line.length());
                    }  
                    fr.close();
                      //closes the stream and release the resources
                    System.out.println("Mean length for "+Automata[n-1]+" "+mex[i]+": "+mean(sum, count));
                    //System.out.println("Sum: "+sum);
                    //System.out.println("Count: "+count); 
                    //System.out.println("Mean length: "+mean(sum, count));
                    sum = 0;
                    count = 0;
                }
                }
                //System.out.println("Contents of File: ");  
                //System.out.println(sb.toString());   //returns a string that textually represents the object 
            
            } catch(IOException e) { 
                System.out.println("Benchmark not present");
                //e.printStackTrace();  
            }
        } else {
            do{
                System.out.println("Choose which benchmark of GPU NFA to compute the mean length for:");
                 for(int i = 0; i < GPU_NFA.length; i++){
                     System.out.println((i+1)+") "+GPU_NFA[i]);
                 }
                 System.out.println("9) All the benchmarks");
                 System.out.print("Insert the number (1-9) of the benchmark:");
                 n = scanner.nextInt();
                 if(n < 1 || n > 9) System.out.println("Insert a valid input"); 
             } while(n < 1 || n > 9);
            try{
                if(n == 9){
                    GenerateOutput generator = new GenerateOutput("GPU_NFA/mean_regex_length", "txt");
                    ArrayList<Double> without_min = new ArrayList<>();
                    ArrayList<Double> degree_min = new ArrayList<>();
                    ArrayList<Double> degree_min_once = new ArrayList<>();
                    ArrayList<Double> serial_min = new ArrayList<>();
                    ArrayList<Double> serial_min_once = new ArrayList<>();
                    //ArrayList<Double> bridge_min = new ArrayList<>();
                    ArrayList<Double> bridge_min_once = new ArrayList<>();
                    ArrayList<Double> weight_min = new ArrayList<>();
                    ArrayList<Double> weight_min_once = new ArrayList<>();
                    try {
                        for(int i = 0; i < GPU_NFA.length; i++){
                            ArrayList<String> failed = new ArrayList<>();
                            File check = new File("../output/GPU_NFA/"+GPU_NFA[i]+"/failed.json");
                            if(check.exists()){
                                JSONReadFromFile read = new JSONReadFromFile();
                                failed.addAll(read.getAutomataNum("GPU_NFA/"+GPU_NFA[i]+"/failed"));
                            }
                            for(int j = 0; j <type.length; j++){
                                File file = new File("../output/GPU_NFA/"+GPU_NFA[i]+"/"+name_GPU[i]+type[j]+".regex");    //creates a new file instance  
                                FileReader fr = new FileReader(file);   //reads the file  
                                BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                                //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                                String line;
                                int pos = 0;
                                while((line=br.readLine())!=null) {  
                                    if(!failed.contains(pos+"")){
                                        //sb.append(line);      //appends line to string buffer 
                                        //System.out.println("Size of regex: "+line.length());
                                        sum = sum + line.length();
                                        //sb.append("\n");     //line feed 
                                        count++;
                                        if(min == 0 || min > line.length()){
                                            min = line.length();
                                        }
                                    }
                                    pos++;
                                }
                                if(j==0){
                                    without_min.add(mean(sum, count));
                                } else if(j==1){
                                    degree_min_once.add(mean(sum, count));
                                } else if(j==2){
                                    degree_min.add(mean(sum, count));
                                } else if(j==3){
                                    serial_min_once.add(mean(sum, count));
                                } else if(j==4){
                                    serial_min.add(mean(sum, count));
                                } else if(j==5){
                                    bridge_min_once.add(mean(sum, count));
                                } else if(j==6){
                                    weight_min_once.add(mean(sum, count));
                                } else {
                                    weight_min.add(mean(sum, count));
                                }
                                fr.close();
                                sum = 0;
                                count = 0;
                                System.out.println("Benchmark considered: "+name_GPU[i]+" and type: "+type[j]);
                            }


                        }
                        generator.writeFileRow("without_min=[");
                        for(int i = 0; i < without_min.size(); i ++){
                            if(i < without_min.size()-1){
                                generator.writeFileRow(without_min.get(i)+", ");
                            } else {
                                generator.writeFile(without_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("degree_min_once=[");
                        for(int i = 0; i < degree_min_once.size(); i ++){
                            if(i < degree_min_once.size()-1){
                                generator.writeFileRow(degree_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(degree_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("degree_min=[");
                        for(int i = 0; i < degree_min.size(); i ++){
                            if(i < degree_min.size()-1){
                                generator.writeFileRow(degree_min.get(i)+", ");
                            } else {
                                generator.writeFile(degree_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("serial_min_once=[");
                        for(int i = 0; i < serial_min_once.size(); i ++){
                            if(i < serial_min_once.size()-1){
                                generator.writeFileRow(serial_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(serial_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("serial_min=[");
                        for(int i = 0; i < serial_min.size(); i ++){
                            if(i < serial_min.size()-1){
                                generator.writeFileRow(serial_min.get(i)+", ");
                            } else {
                                generator.writeFile(serial_min.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("bridge_min_once=[");
                        for(int i = 0; i < bridge_min_once.size(); i ++){
                            if(i < bridge_min_once.size()-1){
                                generator.writeFileRow(bridge_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(bridge_min_once.get(i)+"],");
                            }
                        }/*
                        generator.writeFileRow("bridge_min=[");
                        for(int i = 0; i < bridge_min.size(); i ++){
                            if(i < bridge_min.size()-1){
                                generator.writeFileRow(bridge_min.get(i)+", ");
                            } else {
                                generator.writeFile(bridge_min.get(i)+"],");
                            }
                        }*/
                        generator.writeFileRow("weight_min_once=[");
                        for(int i = 0; i < weight_min_once.size(); i ++){
                            if(i < weight_min_once.size()-1){
                                generator.writeFileRow(weight_min_once.get(i)+", ");
                            } else {
                                generator.writeFile(weight_min_once.get(i)+"],");
                            }
                        }
                        generator.writeFileRow("weight_min=[");
                        for(int i = 0; i < weight_min.size(); i ++){
                            if(i < weight_min.size()-1){
                                generator.writeFileRow(weight_min.get(i)+", ");
                            } else {
                                generator.writeFile(weight_min.get(i)+"],");
                            }
                        }
                        
                    } catch (Exception e) {
                        System.out.println("Benchmark not present");
                    }

                    generator.closeFile();

                    //open file
                    //compute all the values and add them into arrays
                    //write the results in arrays (without min, degree min, bridge min, weight min)
                    //close file
                } else {
                    ArrayList<String> failed = new ArrayList<>();
                    File check = new File("../output/GPU_NFA/"+GPU_NFA[n-1]+"/failed.json");
                    if(check.exists()){
                        JSONReadFromFile read = new JSONReadFromFile();
                        failed.addAll(read.getAutomataNum("GPU_NFA/"+GPU_NFA[n-1]+"/failed"));
                    }
                    for(int i = 0; i < type.length; i++){
                        //System.out.println("Path: ../output/ANMLZoo/"+ANML[n-1]+"/"+nameANML[n-1]+type[i]+".regex");
                        File file = new File("../output/GPU_NFA/"+GPU_NFA[n-1]+"/"+name_GPU[n-1]+type[i]+".regex");    //creates a new file instance  
                        FileReader fr = new FileReader(file);   //reads the file  
                        BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                        //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                        String line;
                        int pos = 0;
                        System.out.print("States to not consider: ");
                        for(String s : failed){
                            System.out.print(s+", ");
                        }
                        String s = pos+"";
                        while((line=br.readLine())!=null) {
                            if(!failed.contains(s)){
                                sum = sum + line.length();
                                //sb.append("\n");     //line feed 
                                count++;
                                if(min == 0 || min > line.length()){
                                    min = line.length();
                                }
                            }
                            pos++;
                            //sb.append(line);      //appends line to string buffer 
                            //System.out.println("Size of regex: "+line.length());
                        }  
                        fr.close();
                        //closes the stream and release the resources
                        System.out.println("Mean length for "+GPU_NFA[n-1]+" "+mex[i]+": "+mean(sum, count));
                        //System.out.println("Sum: "+sum);
                        //System.out.println("Count: "+count); 
                        //System.out.println("Mean length: "+mean(sum, count));
                        sum = 0;
                        count = 0;
                    }
                }
                
                
                //System.out.println("Contents of File: ");  
                //System.out.println(sb.toString());   //returns a string that textually represents the object  
            }  
            catch(IOException e) { 
                System.out.println("Benchmark not present");
                //e.printStackTrace();  
            }
        } 
        
        scanner.close();
         
    }  

    public static Double mean(double sum, double count){
        if(sum == 0 || count == 0) return 0.0;
        else return sum/count;
    }

}
