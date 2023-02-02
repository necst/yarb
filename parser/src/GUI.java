import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class GUI {
    private static final String[] suite = {"ANMLZoo", "AutomataZoo", "GPU NFA"};
    private static final String[] ANML = {"Brill", "ClamAV", "Dotstar", "EntityResolution", "Fermi", "Hamming", "Levenshtein", "PowerEn", "Protomata", "RandomForest", "Snort", "SPM", "Synthetic Block Rings", "Synthetic Core Rings"};
    private static final String[] AutomataTypes = {"APPRNG 4-sided", "APPRNG 8-sided", "Brill", "ClamAV", "CRISPR CasOFFinder", "CRISPR CasOT", "EntityResolution", "FileCarving", "Hamming N1000_l18_d3", "Hamming N1000_l2_d5", "Hamming N1000_l31_d10", "Levenshtein N1000_l19_d3", "Levenshtein N1000_l24_d5", "Levenshtein N1000_l37_d10", "Protomata", "RandomForest 20_400_200", "RandomForest 20_400_270", "RandomForest 20_800_200", "SeqMatch w6_p6", "SeqMatch w6_p6 counters", "SeqMatch w6_p10", "SeqMatch w6_p10 counters", "Snort", "YARA", "YARA_wide"};
    private static final String[] Automata = {"APPRNG_n1000_d4", "APPRNG_n1000_d8", "Brill", "ClamAV", "CRISPR_CasOFF", "CRISPR_CasOT", "EntityResolution", "FileCarving", "Hamming_3", "Hamming_5", "Hamming_10", "Levenshtein_3", "Levenshtein_5", "Levenshtein_10", "Protomata", "RandomForest_400_200", "RandomForest_400_270", "RandomForest_800_200", "SeqMatch_6pad", "SeqMatch_6pad_c", "SeqMatch_10pad", "SeqMatch_10pad_c", "Snort", "YARA", "YARA_wide"};
    private static final String[] nameANML = {"brill", "515_nocounter", "backdoor_dotstar", "1000", "fermi_2400", "93_20X3", "24_20x3", "complx_01000_00123", "2340sigs", "rf", "snort", "bible_size4", "BlockRings", "CoreRings"};
    private static final String[] nameAutomata = {"apprng_n1000_d4", "apprng_n1000_d8", "brill", "clamav", "CRISPR_CasOFFinder_2000", "CRISPR_CasOT_2000", "er_10000names", "file_carver", "ham_1000_3", "ham_1000_5", "ham_1000_10", "lev_1000_3", "lev_1000_5", "lev_1000_10", "protomata", "rf_20_400_200", "rf_20_400_270", "rf_20_800_200", "6wide_6pad", "6wide_6pad_counters", "6wide_10pad", "6wide_10pad_counters", "snort", "YARA", "YARA_wide"};
    private static final String[] extAutomata = {"anml", "anml", "mnrl", "anml", "anml", "anml", "anml", "anml", "anml", "anml", "anml", "anml", "anml", "anml", "mnrl", "anml", "anml", "anml", "anml", "anml", "anml", "anml", "mnrl", "mnrl", "mnrl"};
    private static final String[] GPU_NFA = {"Bro217", "Dotstar03", "Dotstar06", "Dotstar09", "ExactMath", "Ranges1", "Ranges05", "TCP"};
    private static final String[] name_GPU = {"bro217", "dotstar03", "dotstar06", "dotstar09", "exactmath", "ranges1", "ranges05", "tcp"};
    private static final String[] minimizations = {"degree", "serial", "bridge", "weight"};
    private static final String[] minimizationType = {"once", "iter"};
    private String[] ret = new String[7];
    public GUI(){

    }
    public String[] start(){
        String fileName = new String();
        int a;
        int n;
        int m;
        int o;
        Scanner scanner = new Scanner(System.in);
        do{
            System.out.println("Which benchmark suite do you want to parse? (1-3)");
            for(int i = 0; i < suite.length; i++){
                System.out.println((i+1)+") "+suite[i]);
            }
            System.out.print("Insert the number (1-3) of the benchmark suite to parse: ");
            a = scanner.nextInt();
            if(a < 1 || a > 3) System.out.println("Insert a valid input");
        } while (a < 1 || a > 3);
        if(a == 1){
            ret[5] = "ANMLZoo";
            //choose the benchmark
            do{
               System.out.println("Choose which benchmark to parse:");
                for(int i = 0; i < ANML.length; i++){
                    System.out.println((i+1)+") "+ANML[i]);
                }
                System.out.print("Insert the number (1-14) of the benchmark to parse:");
                n = scanner.nextInt();
                if(n < 1 || n > 14) System.out.println("Insert a valid input"); 
            } while(n < 1 || n > 14);
            ret[4] = ANML[n-1];
            if(n != 13 && n != 14){
                ret[0] = "../input/ANMLZoo/"+ANML[n-1]+"/"+nameANML[n-1]+".1chip.anml";
                ret[2] = "../output/ANMLZoo/"+ANML[n-1];
            } else {
                ret[0] = "../input/ANMLZoo/Synthetic/"+nameANML[n-1]+".anml";
                ret[2] = "../output/ANMLZoo/Synthetic";
            }

            //choose to minimize or not
            do{
                System.out.print("Do you want to minimize the output? (y/n)");
                a = scanner.next().charAt(0);
                if(a != 'y' && a != 'n') System.out.println("Insert a valid input");
            } while (a != 'y' && a != 'n');
            if(a == 'y'){
                do{
                    System.out.println("Choose which minimization to apply:");
                    System.out.println("1) Max in- and out-degree ordering");
                    System.out.println("2) Serial state ordering");
                    System.out.println("3) Bridge state ordering");
                    System.out.println("4) Weight ordering");
                    System.out.println("5) All the minimizations");
                
                    System.out.print("Insert the number (1-5) of the minimization to apply:");
                    m = scanner.nextInt();
                    if(m < 1 || m > 5) System.out.println("Insert a valid input"); 
                } while(m < 1 || m > 5);
                if(m < 5){
                    ret[3] = minimizations[m - 1];
                } else {
                    ret[3] = "all";
                }
                
                if(m != 5 && m != 3){
                    do{
                        System.out.println("Choose how to apply the minimization:");
                        System.out.println("1) Only once at the beginning");
                        System.out.println("2) At each iteration of the state elimination method");
                    
                        System.out.print("Insert the number (1-2) of the modality to follow:");
                        o = scanner.nextInt();
                        if(o < 1 || o > 2) System.out.println("Insert a valid input"); 
                    } while(o < 1 || o > 2);
                    
                } else {
                    o = 1;
                }

                ret[6] = minimizationType[o-1];

                ANMLparser parser = new ANMLparser();
                if(ret[3]!="all"){
                    ret[1] = nameANML[n-1]+"_"+ret[3]+"_minimization_"+ret[6];

                    execute(parser, ret);

                } else {
                    ret[3] = "false";
                    ret[6] = "";
                    ret[1] = nameANML[n-1];

                    execute(parser, ret);

                    for(String s: minimizations){
                        for(String mi: minimizationType){
                            parser = new ANMLparser();
                            if(s=="bridge" && mi=="iter"){
                            } else {
                                ret[3] = s;
                            ret[6] = mi;
                            if(ret[6]!=""){
                                ret[1] = nameANML[n-1]+"_"+ret[3]+"_minimization_"+ret[6];
                            } else {
                                ret[1] = nameANML[n-1]+"_"+ret[3]+"_minimization";
                            }
                            execute(parser, ret);
                            }
                        }
                    }
                }
                
                /*if(m == 1){
                    //String bench = ANML[n-1];
                    //String fileName = nameANML[n-1]+"_degree_minimization";
                    //String min = "degree";
                    //String minType = 
                    //if(n != 13 && n != 14){
                    //    ret[2] = "../output/ANMLZoo/"+ANML[n-1];
                    //} else {
                    //    ret[2] = "../output/ANMLZoo/Synthetic";
                    //}
                    execute(parser, ret[5], bench, fileName, min);
                    //min degree, ANMLZoo, benchmark n-1
                    //ret[1] = nameANML[n-1]+"_degree_minimization";

                    /*if(n != 13 && n != 14){
                        ret[2] = "../output/ANMLZoo/"+ANML[n-1];
                    } else {
                        ret[2] = "../output/ANMLZoo/Synthetic";
                    }
                    ret[3] = "degree";
                    ret[4] = ANML[n-1];
                    
                } else if(m == 2){
                    ret[1] = nameANML[n-1]+"_serial_minimization";

                    if(n != 13 && n != 14){
                        ret[2] = "../output/ANMLZoo/"+ANML[n-1];
                    } else {
                        ret[2] = "../output/ANMLZoo/Synthetic";
                    }
                    ret[3] = "serial";
                    ret[4] = ANML[n-1];
                } else if(m == 3){
                    ret[1] = nameANML[n-1]+"_bridge_minimization";

                    if(n != 13 && n != 14){
                        ret[2] = "../output/ANMLZoo/"+ANML[n-1];
                    } else {
                        ret[2] = "../output/ANMLZoo/Synthetic";
                    }
                    ret[3] = "bridge";
                    ret[4] = ANML[n-1];
                } else if (m == 4){
                    ret[1] = nameANML[n-1]+"_weight_minimization";

                    if(n != 13 && n != 14){
                        ret[2] = "../output/ANMLZoo/"+ANML[n-1];
                    } else {
                        ret[2] = "../output/ANMLZoo/Synthetic";
                    }
                    ret[3] = "weight";
                    ret[4] = ANML[n-1];

                } else {
                    ret[1] = null;
                    ret[2] = null;
                    ret[3] = "all";
                    ret[4] = ANML[n-1];
                }
                if(o == 1){
                    ret[6] = "once";
                    ret[1] = ret[1]+"_once";
                } else if(o == 2){
                    ret[6] = "iter";
                } else {
                    ret[6] = null;
                }*/
                    
            } else {
                /*fileName = nameANML[n-1];
                if(n != 13 && n != 14){
                    ret[2] = "../output/ANMLZoo/"+ANML[n-1];
                } else {
                    ret[2] = "../output/ANMLZoo/Synthetic";
                }*/
                ret[1] = nameANML[n-1];
                
                ret[3] = "false";
                ret[4] = ANML[n-1];
                ret[6] = "";
                ANMLparser parser = new ANMLparser();
                execute(parser, ret);
            }

            

            /*if(n != 13 && n != 14){
                ret[0] = "../input/ANMLZoo/"+ANML[n-1]+"/"+nameANML[n-1]+".1chip.anml";
            } else {
                ret[0] = "../input/ANMLZoo/Synthetic/"+nameANML[n-1]+".anml";
            }
            ret[5] = "ANMLZoo";
            */

            //System.out.println("Path of "+ANML[n-1]+": "+ret[0]);
            System.out.println("Name of output of "+ANML[n-1]+": "+fileName);

        } else if(a == 2){
            ret[5] = "AutomataZoo";
            //choose the benchmark
            do{
                System.out.println("Choose which benchmark to parse:");
                for(int i = 0; i < Automata.length; i++){
                    System.out.println((i+1)+") "+AutomataTypes[i]);
                }
                System.out.print("Insert the number (1-25) of the benchmark to parse:");
                n = scanner.nextInt();
                if(n < 1 || n > 25) System.out.println("Insert a valid input"); 
            } while(n < 1 || n > 25);

            ret[4] = Automata[n-1];
            ret[0] = "../input/AutomataZoo/"+Automata[n-1]+"/"+nameAutomata[n-1]+"."+extAutomata[n-1];
            ret[2] = "../output/AutomataZoo/"+Automata[n-1];

            //choose to minimize or not
            do{
                System.out.print("Do you want to minimize the output? (y/n)");
                a = scanner.next().charAt(0);
                if(a != 'y' && a != 'n') System.out.println("Insert a valid input");
            } while (a != 'y' && a != 'n');
            if(a == 'y'){
                do{
                    System.out.println("Choose which minimization to apply:");
                    System.out.println("1) Max in- and out-degree ordering");
                    System.out.println("2) Serial state ordering");
                    System.out.println("3) Bridge state ordering");
                    System.out.println("4) Weight ordering");
                    System.out.println("5) All the minimizations");

                    System.out.print("Insert the number (1-5) of the minimization to apply:");
                    m = scanner.nextInt();
                    if(m < 1 || m > 5) System.out.println("Insert a valid input"); 
                } while(m < 1 || m > 5);

                if(m < 5){
                    ret[3] = minimizations[m - 1];
                } else {
                    ret[3] = "all";
                }

                if(m != 5 && m != 3){
                    do{
                        System.out.println("Choose how to apply the minimization:");
                        System.out.println("1) Only once at the beginning");
                        System.out.println("2) At each iteration of the state elimination method");
                    
                        System.out.print("Insert the number (1-2) of the modality to follow:");
                        o = scanner.nextInt();
                        if(o < 1 || o > 2) System.out.println("Insert a valid input"); 
                    } while(o < 1 || o > 2);
                } else {
                    o = 1;
                }

                ret[2] = "../output/ANMLZoo/"+Automata[n-1];
                ret[6] = minimizationType[o - 1];
                
                if(ret[3] != "all"){
                    ret[1] = nameAutomata[n-1]+"_"+ret[3]+"_minimization_"+ret[6];
                    if(extAutomata[n-1].equals("mnrl")){
                        MNRLparser parser = new MNRLparser();
                        execute(parser, ret);
                    } else {
                        ANMLparser parser = new ANMLparser();
                        execute(parser, ret);
                    }
                } else {
                    ret[3] = "false";
                    ret[6] = "";
                    ret[1] = nameAutomata[n-1];
                    if(extAutomata[n-1].equals("mnrl")){
                        MNRLparser parser = new MNRLparser();
                        execute(parser, ret);
                    } else {
                        ANMLparser parser = new ANMLparser();
                        execute(parser, ret);
                    }

                    for(String s : minimizations){
                        for(String mi : minimizationType){
                            if(s== "bridge" && mi == "iter"){
                            } else {
                                ret[3] = s;
                                ret[6] = mi;
                                if(ret[6] != ""){
                                    ret[1] = nameAutomata[n-1]+"_"+ret[3]+"_minimization_"+ret[6];
                                } else {
                                    ret[1] = nameAutomata[n-1]+"_"+ret[3]+"_minimization";
                                }
                                if(extAutomata[n-1].equals("mnrl")){
                                    MNRLparser parser = new MNRLparser();
                                    execute(parser, ret);
                                } else {
                                    ANMLparser parser = new ANMLparser();
                                    execute(parser, ret);
                                }
                            }
                        }
                    }
                }

                /*if(m == 1){
                    ret[1] = nameAutomata[n-1]+"_degree_minimization";
                    ret[2] = "../output/AutomataZoo/"+Automata[n-1];
                    ret[3] = "degree";
                    ret[4] = Automata[n-1];
                } else if(m == 2){
                    ret[1] = nameAutomata[n-1]+"_serial_minimization";
                    ret[2] = "../output/AutomataZoo/"+Automata[n-1];
                    ret[3] = "serial";
                    ret[4] = Automata[n-1];
                } else if(m == 3){
                    ret[1] = nameAutomata[n-1]+"_bridge_minimization";
                    ret[2] = "../output/AutomataZoo/"+Automata[n-1];
                    ret[3] = "bridge";
                    ret[4] = Automata[n-1];
                } else if(m == 4){
                    ret[1] = nameAutomata[n-1]+"_weight_minimization";
                    ret[2] = "../output/AutomataZoo/"+Automata[n-1];
                    ret[3] = "weight";
                    ret[4] = Automata[n-1];
                } else {
                    ret[1] = null;
                    ret[2] = null;
                    ret[3] = "all";
                    ret[4] = Automata[n-1];
                }

                if(o == 1){
                    ret[6] = "once";
                    ret[1] = ret[1]+"_once";
                } else if(o == 2) {
                    ret[6] = "iter";
                } else {
                    ret[6] = null;
                }*/
                     
             } else {
                ret[1] = nameAutomata[n-1];
                ret[2] = "../output/AutomataZoo/"+Automata[n-1];
                ret[3] = "false";
                ret[4] = Automata[n-1];
                ret[6] = "";

                if(extAutomata[n-1].equals("mnrl")){
                    MNRLparser parser = new MNRLparser();
                    execute(parser, ret);
                } else {
                    ANMLparser parser = new ANMLparser();
                    execute(parser, ret);
                }
             }
 
            System.out.println("Path of "+Automata[n-1]+": "+ret[0]);
            System.out.println("Name of output of "+Automata[n-1]+": "+ret[1]);
        } else {
            ret[5] = "GPU_NFA";
            do{
                System.out.println("Choose which benchmark to parse:");
                for(int i = 0; i < GPU_NFA.length; i++){
                    System.out.println((i+1)+") "+GPU_NFA[i]);
                }
                System.out.print("Insert the number (1-8) of the benchmark to parse:");
                n = scanner.nextInt();
                if(n < 1 || n > 8) System.out.println("Insert a valid input"); 
            } while(n < 1 || n > 8);

            ret[4] = GPU_NFA[n-1];
            ret[0] = "../input/GPU_NFA/"+GPU_NFA[n-1]+"/"+name_GPU[n-1]+".anml";
            ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];

            //choose to minimize or not
            do{
                System.out.print("Do you want to minimize the output? (y/n)");
                a = scanner.next().charAt(0);
                if(a != 'y' && a != 'n') System.out.println("Insert a valid input");
            } while (a != 'y' && a != 'n');
            if(a == 'y'){
                do{
                    System.out.println("Choose which minimization to apply:");
                    System.out.println("1) Max in- and out-degree ordering");
                    System.out.println("2) Serial state ordering");
                    System.out.println("3) Bridge state ordering");
                    System.out.println("4) Weight ordering");
                    System.out.println("5) All the minimizations");

                    System.out.print("Insert the number (1-5) of the minimization to apply:");
                    m = scanner.nextInt();
                    if(m < 1 || m > 5) System.out.println("Insert a valid input"); 
                } while(m < 1 || m > 5);

                if(m < 5){
                    ret[3] = minimizations[m-1];
                } else {
                    ret[3] = "all";
                }

                if(m != 5 && m != 3){
                    do{
                        System.out.println("Choose how to apply the minimization:");
                        System.out.println("1) Only once at the beginning");
                        System.out.println("2) At each iteration of the state elimination method");
                    
                        System.out.print("Insert the number (1-2) of the modality to follow:");
                        o = scanner.nextInt();
                        if(o < 1 || o > 2) System.out.println("Insert a valid input"); 
                    } while(o < 1 || o > 2);
                } else {
                    o = 1;
                }

                ret[0] = "../input/GPU_NFA/"+GPU_NFA[n-1]+"/"+name_GPU[n-1]+".anml";
                ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];
                ret[6] = minimizationType[o-1];

                if(ret[3] != "all"){
                    ret[1] = name_GPU[n-1]+"_"+ret[3]+"_minimization_"+ret[6];
                    ANMLparser parser = new ANMLparser();
                    execute(parser, ret);
                } else {
                    ret[3] = "false";
                    ret[6] = "";
                    ret[1] = name_GPU[n-1];

                    ANMLparser parser = new ANMLparser();
                    execute(parser, ret);

                    for(String s : minimizations){
                        for(String mi : minimizationType){
                            if(s== "bridge" && mi == "iter"){
                            } else {
                                ret[3] = s;
                                ret[6] = mi;
                                if(ret[6] != ""){
                                    ret[1] = name_GPU[n-1]+"_"+ret[3]+"_minimization_"+ret[6];
                                } else {
                                    ret[1] = name_GPU[n-1]+"_"+ret[3]+"_minimization";
                                }
                                parser = new ANMLparser();
                                execute(parser, ret);
                            }
                        }
                    }
                }
                
                
                /*if(m == 1){
                    ret[1] = name_GPU[n-1]+"_degree_minimization";
                    ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];
                    ret[3] = "degree";
                    ret[4] = GPU_NFA[n-1];
                } else if(m == 2){
                    ret[1] = name_GPU[n-1]+"_serial_minimization";
                    ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];
                    ret[3] = "serial";
                    ret[4] = GPU_NFA[n-1];
                } else if(m == 3){
                    ret[1] = name_GPU[n-1]+"_bridge_minimization";
                    ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];
                    ret[3] = "bridge";
                    ret[4] = GPU_NFA[n-1];
                } else if(m == 4){
                    ret[1] = name_GPU[n-1]+"_weight_minimization";
                    ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];
                    ret[3] = "weight";
                    ret[4] = GPU_NFA[n-1];
                } else {
                    ret[1] = null;
                    ret[2] = null;
                    ret[3] = "all";
                    ret[4] = GPU_NFA[n-1];
                }

                if(o == 1){
                    ret[6] = "once";
                    ret[1] = ret[1]+"_once";
                } else if(o == 2) {
                    ret[6] = "iter";
                } else {
                    ret[6] = null;
                }*/
                     
             } else {
                ret[1] = name_GPU[n-1];
                ret[2] = "../output/GPU_NFA/"+GPU_NFA[n-1];
                ret[3] = "false";
                ret[4] = GPU_NFA[n-1];
                ret[6] = "";

                ANMLparser parser = new ANMLparser();
                execute(parser, ret);
             }
 
            System.out.println("Path of "+GPU_NFA[n-1]+": "+ret[0]);
            System.out.println("Name of output of "+GPU_NFA[n-1]+": "+ret[1]);
        }
        
        scanner.close();
        return ret;
    }

    private void execute(MNRLparser parser, String[] p){
        try {
            System.out.println("AutomataZoo");
            JSONReadFromFile read = new JSONReadFromFile();
            //all minimizations for a benchmark
            System.out.println("p[5]: "+p[5]+"; p[4]: "+p[4]+"; p[1]: "+p[1] );
            GenerateOutput generator = new GenerateOutput(p[5], p[4], p[1], "regex");
            Translator translator = new Translator();
        
            ArrayList<String> r = new ArrayList<>();

            int num;
            File check = new File("../output/"+p[5]+"/automata/tempAutomaton_"+p[4]+".json");
            if(!check.exists()){
                num = parser.parse(p);
                System.out.println("Check if file exists: "+check.exists());
            } else {
                num = read.sizeAutomata(p[5]+"/automata/tempAutomaton_"+p[4]);
                //num = 200;
                System.out.println("Size of tempAutomaton_"+p[4]+" is: "+num);
                System.out.println("Check if file exists: "+check.exists());
            }
            
            for(int i = 0; i < num; i ++){
                r = translator.translateToRegex(p[5], p[4], i, p[3], p[6], p[5]+"/"+p[4]+"/failed");
                if(r==null){
                    generator.writeFileRow("\nnull");
                } else {
                    if(r.size() > 1){
                        if(i!=0) generator.writeFileRow("\n");
                        for(int j = 0; j < r.size(); j++){
                            generator.writeFileRow(r.get(j));
                        }
                        //generator.writeFileRow("\n");
                    } else if(r.size() == 1){
                    try {
                        if(i!=0) generator.writeFileRow("\n");
                        generator.writeFileRow(r.get(0));
                    } catch (Throwable e) {
                        if(i!=0) generator.writeFileRow("\n");
                        generator.writeFileRow(r.get(0));
                        //generator.writeFileRow("\n");
                    }
                        
                    } else {
                        System.out.println("Size of regex not compatible");
                    }
                }
                
            }
            generator.closeFile();
            System.out.println("Size of automata: "+num);
            System.out.println("Parsed: "+p[1]+" "+p[5]);
        } catch (Exception e) {
            //TODO: handle exception
        }
    }

    private void execute(ANMLparser parser, String[] p){
        try {
            System.out.println("ANMLZoo or GPU_NFA");
            JSONReadFromFile read = new JSONReadFromFile();
            //all minimizations for a benchmark
            System.out.println("p[5]: "+p[5]+"; p[4]: "+p[4]+"; p[1]: "+p[1] );
            GenerateOutput generator = new GenerateOutput(p[5], p[4], p[1], "regex");
            Translator translator = new Translator();
        
            ArrayList<String> r = new ArrayList<>();

            int num;
            File check = new File("../output/"+p[5]+"/automata/tempAutomaton_"+p[4]+".json");
            if(!check.exists()){
                num = parser.parse(p);
                System.out.println("Check if file exists: "+check.exists());
            } else {
                num = read.sizeAutomata(p[5]+"/automata/tempAutomaton_"+p[4]);
                //num = 300;
                System.out.println("Size of tempAutomaton_"+p[4]+" is: "+num);
                System.out.println("Check if file exists: "+check.exists());
            }
            
            for(int i = 0; i < num; i ++){
                translator = new Translator();
                r = translator.translateToRegex(p[5], p[4], i, p[3], p[6], p[5]+"/"+p[4]+"/failed");
                if(r==null){
                    generator.writeFileRow("\nnull");
                } else {
                    if(r.size() > 1){
                        if(i!=0) generator.writeFileRow("\n");
                        for(int j = 0; j < r.size(); j++){
                            generator.writeFileRow(r.get(j));
                        }
                        //generator.writeFileRow("\n");
                    } else if(r.size() == 1){
                    try {
                        if(i!=0) generator.writeFileRow("\n");
                        generator.writeFileRow(r.get(0));
                    } catch (Throwable e) {
                        if(i!=0) generator.writeFileRow("\n");
                        generator.writeFileRow(r.get(0));
                        //generator.writeFileRow("\n");
                    }
                        
                    } else {
                        System.out.println("Size of regex not compatible");
                    }
                }
                
            }
            generator.closeFile();
            System.out.println("Size of automata: "+num);
            System.out.println("Parsed: "+p[1]+" "+p[5]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}