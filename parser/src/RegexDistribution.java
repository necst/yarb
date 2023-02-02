import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class RegexDistribution {
    private static final String[] ANML = {"Brill", "ClamAV", "Dotstar", "EntityResolution", "Fermi", "Hamming", "Levenshtein", "PowerEn", "Protomata", "RandomForest", "Snort", "SPM", "Synthetic Block Rings", "Synthetic Core Rings"};
    private static final String[] nameANML = {"brill", "515_nocounter", "backdoor_dotstar", "1000", "fermi_2400", "93_20X3", "24_20x3", "complx_01000_00123", "2340sigs", "rf", "snort", "bible_size4", "BlockRings", "CoreRings"};
    private static final String[] Automata = {"APPRNG", "APPRNG", "Brill", "ClamAV", "CRISPR", "CRISPR", "EntityResolution", "FileCarving", "Hamming", "Hamming", "Hamming", "Levenshtein", "Levenshtein", "Levenshtein", "Protomata", "RandomForest", "RandomForest", "RandomForest", "SeqMatch", "SeqMatch", "SeqMatch", "SeqMatch", "Snort", "YARA", "YARA"};
    private static final String[] nameAutomata = {"apprng_n1000_d4", "apprng_n1000_d8", "brill", "clamav", "CRISPR_CasOFFinder_2000", "CRISPR_CasOT_2000", "er_10000names", "file_carver", "ham_1000_3", "ham_1000_5", "ham_1000_10", "lev_1000_3", "lev_1000_5", "lev_1000_10", "protomata", "rf_20_400_200", "rf_20_400_270", "rf_20_800_200", "6wide_6pad", "6wide_6pad_counters", "6wide_10pad", "6wide_10pad_counters", "snort", "YARA", "YARA_wide"};
    private static final Integer[] mnrl_Auto = {2, 14, 22, 23};
    private static final String[] GPU_NFA = {"Bro217", "Dotstar03", "Dotstar06", "Dotstar09", "ExactMath", "Ranges1", "Ranges05", "TCP"};
    private static final String[] name_GPU = {"bro217", "dotstar03", "dotstar06", "dotstar09", "exactmath", "ranges1", "ranges05", "tcp"};
    private static final String[] type = {"", "_degree_minimization_once", "_degree_minimization", "_serial_minimization_once", "_serial_minimization", "_bridge_minimization_once", "_weight_minimization_once", "_weight_minimization"};
    private static final String[] mex = {"without minimization", "with degree minimization only once", "with degree minimization iterated", "with serial minimization only once", "with serial minimization iterated", "with bridge minimization only once", "with weight minimization only once", "with weight minimization iterated"};
    private static final String[] suite = {"ANMLZoo", "AutomataZoo", "GPU_NFA"};

    private static ArrayList<Integer> lengths = new ArrayList<>();
    public static void main(String args[]) { 
        int n;
        int a;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("Which benchmark suite do you want to parse? (1-3)");
            for(int i = 0; i < suite.length; i++){
                System.out.println((i+1)+") "+suite[i]);
            }
            System.out.println("4) All the benchmark suites");
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
                    GenerateOutput generator = new GenerateOutput("ANMLZoo/regex_lenghts", "txt");
                    for(int i = 0; i < ANML.length-2; i++){
                        distribution(generator, i, "ANMLZoo");
                    }
                    generator.closeFile();
                } else {
                    GenerateOutput generator = new GenerateOutput("ANMLZoo/"+ANML[n-1]+"/"+ANML[n-1]+"_regex_lenghts", "txt");
                    distribution(generator, n - 1, "ANMLZoo");
                    generator.closeFile();
                }  
            }  
            catch(Exception e) { 
                System.out.println("Benchmark not present");
                //e.printStackTrace();  
            }
        } else if (a==2) {
            do{
                System.out.println("Choose which benchmark of AutomataZoo to compute the mean length for:");
                 for(int i = 0; i < Automata.length; i++){
                     System.out.println((i+1)+") "+Automata[i]);
                 }
                 System.out.println("26) All MNRL benchmarks (Brill, Protomata, Snort, Yara)");
                 System.out.print("Insert the number (1-26) of the benchmark:");
                 n = scanner.nextInt();
                 if(n < 1 || n > 26) System.out.println("Insert a valid input"); 
             } while(n < 1 || n > 26);
            try{
                if(n == 26){
                    GenerateOutput generator = new GenerateOutput("AutomataZoo/regex_lenghts_mnrl", "txt");
                    for(int i = 0; i < mnrl_Auto.length; i++){
                        distribution(generator, mnrl_Auto[i], "AutomataZoo");
                    }
                    generator.closeFile();
                } else {
                    GenerateOutput generator = new GenerateOutput("AutomataZoo/"+Automata[n-1]+"/"+Automata[n-1]+"_regex_lenghts", "txt");
                    distribution(generator, n-1, "AutomataZoo");
                    generator.closeFile();
                }
            }  
            catch(Exception e) { 
                System.out.println("Benchmark not present");
                //e.printStackTrace();  
            }
        } else if(a ==3){
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
                    GenerateOutput generator = new GenerateOutput("GPU_NFA/regex_lenghts", "txt");
                    for(int i = 0; i < GPU_NFA.length; i++){
                        distribution(generator, i, "GPU");
                    }
                    generator.closeFile();
                } else {
                    GenerateOutput generator = new GenerateOutput("GPU_NFA/"+GPU_NFA[n-1]+"/"+GPU_NFA[n-1]+"_regex_lenghts", "txt");
                    distribution(generator, n - 1, "GPU");
                    generator.closeFile();
                } 
            }  
            catch(Exception e) { 
                System.out.println("Benchmark not present");
            }
        } else {
            try{
                GenerateOutput generator = new GenerateOutput("ANMLZoo/regex_lenghts", "txt");
                for(int i = 0; i < ANML.length-2; i++){
                    distribution(generator, i, "ANMLZoo");
                }
                generator.closeFile();
                
                generator = new GenerateOutput("AutomataZoo/regex_lenghts_mnrl", "txt");
                for(int i = 0; i < mnrl_Auto.length; i++){
                    distribution(generator, mnrl_Auto[i], "AutomataZoo");
                }
                generator.closeFile();

                generator = new GenerateOutput("GPU_NFA/regex_lenghts_gpunfa", "txt");
                for(int i = 0; i < GPU_NFA.length; i++){
                    distribution(generator, i, "GPU");
                }
                generator.closeFile();
                 
            }  
            catch(Exception e) { 
                System.out.println("Benchmark not present");
                //e.printStackTrace();  
            }
        }
        scanner.close();
         
    }

    private static void distribution(GenerateOutput generator, Integer b, String bench){
        try{
            ArrayList<String> failed = new ArrayList<>();
            String path;
            String name;
            if(bench.equals("ANMLZoo")){
                path = "ANMLZoo/"+ANML[b];
                name = nameANML[b];
            } else if(bench.equals("AutomataZoo")) {
                path = "AutomataZoo/"+Automata[b];
                name = nameAutomata[b];
            } else {
                path = "GPU_NFA/"+GPU_NFA[b];
                name = name_GPU[b];
            }
            File check = new File("../output/"+path+"/failed.json");
            if(check.exists()){
                JSONReadFromFile read = new JSONReadFromFile();
                failed.addAll(read.getAutomataNum(path+"/failed"));
            }
            for(int j = 0; j <type.length; j++){
                int count = 0;
                lengths.clear();
                File file = new File("../output/"+path+"/"+name+type[j]+".regex");    //creates a new file instance  
                FileReader fr = new FileReader(file);   //reads the file  
                BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream  
                //StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters  
                String line;
                int pos = 0;
                String s = pos+"";
                while((line=br.readLine())!=null) {
                    s = pos+"";
                    if(!failed.contains(s)){
                        //sb.append(line);      //appends line to string buffer 
                        //System.out.println("Size of regex: "+line.length());
                        lengths.add(line.length());
                        count++;
                        /*if(j==0){
                            without_min.add(line.length());
                        } else if(j==1){
                            degree_min_once.add(line.length());
                        } else if(j==2){
                            degree_min.add(line.length());
                        } else if(j==3){
                            bridge_min_once.add(line.length());
                        } else if(j==4){
                            bridge_min.add(line.length());
                        } else if(j==5){
                            weight_min_once.add(line.length());
                        } else {
                            weight_min.add(line.length());
                        }*/
                    }
                    pos++;
                }
                //add type of minimization in name below
                System.out.println("Number of considered automata: "+count);
                generator.writeFileRow(name+type[j]+"=[");
                for(int k = 0; k < lengths.size(); k++){
                    if(k < lengths.size()-1){
                        generator.writeFileRow(lengths.get(k)+", ");
                    } else {
                        generator.writeFile(lengths.get(k)+"],");
                    }
                }
                fr.close();

                System.out.println("Benchmark considered: "+name+" and type: "+type[j]);
            }
                            /*generator.writeFileRow(nameANML[i]+"_without_min=[");
                            for(int k = 0; k < without_min.size(); k++){
                                if(k < without_min.size()-1){
                                    generator.writeFileRow(without_min.get(k)+", ");
                                } else {
                                    generator.writeFile(without_min.get(k)+"],");
                                }
                            }
                            generator.writeFileRow(nameANML[i]+"_degree_min_once=[");
                            for(int k = 0; i < degree_min_once.size(); k++){
                                if(k < degree_min_once.size()-1){
                                    generator.writeFileRow(degree_min_once.get(k)+", ");
                                } else {
                                    generator.writeFile(degree_min_once.get(k)+"],");
                                }
                            }
                            generator.writeFileRow(nameANML[i]+"_degree_min=[");
                            for(int k = 0; k < degree_min.size(); k++){
                                if(k < degree_min.size()-1){
                                    generator.writeFileRow(degree_min.get(k)+", ");
                                } else {
                                    generator.writeFile(degree_min.get(k)+"],");
                                }
                            }
                            generator.writeFileRow(nameANML[i]+"_bridge_min_once=[");
                            for(int k = 0; k < bridge_min_once.size(); k++){
                                if(k < bridge_min_once.size()-1){
                                    generator.writeFileRow(bridge_min_once.get(k)+", ");
                                } else {
                                    generator.writeFile(bridge_min_once.get(k)+"],");
                                }
                            }
                            generator.writeFileRow(nameANML[i]+"_bridge_min=[");
                            for(int k = 0; k < bridge_min.size(); k++){
                                if(k < bridge_min.size()-1){
                                    generator.writeFileRow(bridge_min.get(k)+", ");
                                } else {
                                    generator.writeFile(bridge_min.get(k)+"],");
                                }
                            }
                            generator.writeFileRow(nameANML[i]+"_weight_min_once=[");
                            for(int k = 0; k < weight_min_once.size(); k++){
                                if(k < weight_min_once.size()-1){
                                    generator.writeFileRow(weight_min_once.get(k)+", ");
                                } else {
                                    generator.writeFile(weight_min_once.get(k)+"],");
                                }
                            }
                            generator.writeFileRow(nameANML[i]+"_weight_min=[");
                            for(int k = 0; k < weight_min.size(); k++){
                                if(k < weight_min.size()-1){
                                    generator.writeFileRow(weight_min.get(k)+", ");
                                } else {
                                    generator.writeFile(weight_min.get(k)+"],");
                                }
                            }*/
        } catch (Exception e) {
            System.out.println("Benchmark not present");
        }
    }
}
