// g++ -std=c++11 -pthread  -o re_loop re_loop.cpp -lre2 && ./re_loop
// add -D RE2_OPTIONS -D USE_XEON -D TAKE_COMPILATION
#include <re2/re2.h>
#include <assert.h>
#include <iostream>
#include <fstream>
#include <string>
#include <chrono>
#include <stdlib.h>
#include <cmath>
#include <stdint.h>

static void try_match (RE2::Options& opt, const char * data, const char *regex_file_name, 
        const char *output_file_name, int iterations, char * csv_separator, bool multi_match);
double i_avg(std::vector<double> &vec);
void i_log(std::vector<double > &vec, std::vector<double> &log_vec);
int main (int argc, char * argv[]){

    auto sTime = std::chrono::high_resolution_clock::now();
    RE2::Options opt;
    #ifdef RE2_OPTIONS 
    opt.set_longest_match(false);
    #endif
    if(argc < 6){
        std::cout<<"Wrong parameters utilization: file_re, file_data, file_output, repetitions, csv_separator"<<std::endl;
        return -1;
    }
    bool multi_match = (argv[6][0] == '0') ? false : true;
    try_match(opt, argv[2], argv[1], argv[3], atoi(argv[4]), argv[5], multi_match);

    auto sfinish = std::chrono::high_resolution_clock::now();
    std::cout << std::chrono::duration_cast<std::chrono::nanoseconds>(sfinish-sTime).count()<<"\n";
    return 0;
}

void try_match (RE2::Options& opt, const char * data, const char *regex_file_name,
                  const char *output_file_name, int iterations, char *csv_separator, bool multi_match){
    std::string n;
    long int  i, match_count, all_re_match;
    std::ifstream input_re(regex_file_name);
    std::string chunks_file_name(data);
    chunks_file_name.append("/chunk.txt");
    std::ifstream chunks_file(chunks_file_name);
    bool result = false;
    std::string line;
    std::string chunks_line;
    long int chunks_number;
    std::vector<double> avgs_exe;
    std::vector<double> times_exe;
    std::vector<double> logs_exe;

    std::vector<double> avgs_comp;
    std::vector<double> times_comp;
    std::vector<double> logs_comp;

    auto start_exe = std::chrono::high_resolution_clock::now();
    auto finish_exe = start_exe;
    auto start_comp = std::chrono::high_resolution_clock::now();
    auto finish_comp = start_comp;

    //getting number of data chunks
    std::getline(chunks_file, chunks_line);
    chunks_number = atoi(chunks_line.c_str());
    chunks_file.close();
    //iterate over regexes
    long int a = 0;
    long int b = 0;
    long int re_counter = 0;
    int comp_failed;
    while(std::getline(input_re, line)){
        comp_failed = 0;
        std::cout<<"executing regex: "<<re_counter++<<"\n";
        std::ofstream output;
        output.open(output_file_name, std::ios_base::app);
    
        result = false;

        avgs_exe.clear();
        times_exe.clear();
        logs_exe.clear();

        avgs_comp.clear();
        times_comp.clear();
        logs_comp.clear();
        match_count = 0;
        for(long int j = 0; j < chunks_number; j++){
            std::string chunk_str = std::to_string(j);
            std::string data_path(data);
            data_path.append("/data");
            data_path.append(chunk_str);
            data_path.append(".dat");
            std::ifstream input(data_path,  std::ios::binary);
            std::string content((std::istreambuf_iterator<char>(input)),
                        (std::istreambuf_iterator<char>()));
            input.close();
            for(long int i = 0; i < iterations; i++){
                all_re_match = 0;

                start_comp = std::chrono::high_resolution_clock::now();
                RE2 re(line.c_str(), opt);
                finish_comp = std::chrono::high_resolution_clock::now();

                if(!re.ok()){
                    comp_failed = 1;
                    break;
                }
                result = false;
                if(multi_match){
                    re2::StringPiece text(content);
                    start_exe = std::chrono::high_resolution_clock::now();
                    while(RE2::FindAndConsume(&text, re)){
                        all_re_match++;
                    }
                    finish_exe = std::chrono::high_resolution_clock::now();
                }else{
                    re2::StringPiece text(content);
                    start_exe = std::chrono::high_resolution_clock::now();
                    result = RE2::FindAndConsume(&text, re);
                    finish_exe = std::chrono::high_resolution_clock::now();
                }
                /*if(iterations < 10 || i > 9){
                    //overall_duration += std::chrono::duration_cast<std::chrono::nanoseconds>(finish-start).count();

                }*/
                times_comp.push_back(std::chrono::duration_cast<std::chrono::microseconds>(finish_comp-start_comp).count());
                times_exe.push_back(std::chrono::duration_cast<std::chrono::microseconds>(finish_exe-start_exe).count());
//std::cout<<"iteration"<< i<< "of chunk"<< j<<"     "<<std::chrono::duration_cast<std::chrono::nanoseconds>(finish_exe-start_exe).count()<<std::endl;
	    }
            if(comp_failed)
                break;
            if(multi_match){
                match_count += all_re_match;
            }else{
                if(result){
                    match_count++;
                }
            }
            avgs_comp.push_back(i_avg(times_comp));
            avgs_exe.push_back(i_avg(times_exe));
        }

        output.precision(10);
        if(comp_failed){
            output<< 0 << csv_separator << 0 << csv_separator<< 0 << csv_separator << 0 << csv_separator << 0 << "\n";
        }else{
	    
            i_log(avgs_comp, logs_comp);
            i_log(avgs_exe, logs_exe);
            output << match_count << csv_separator;        
            output << i_avg(avgs_exe) << csv_separator;
            output << exp(i_avg(logs_exe)) << csv_separator;
            output << i_avg(avgs_comp) << csv_separator;
            output << exp(i_avg(logs_comp)) << "\n";
        }
        output.close();
        
    }
    input_re.close();
}

double i_avg(std::vector<double> &vec){
    unsigned long long sum;
    double avg;
    avg = 0;
    sum = 0;

    for(long int i = 0; i < vec.size(); i++){
	sum += vec[i];
    }
    //std::cout<<"sum "<<sum<<"  "<<vec.size();

    avg = sum / vec.size();
    return avg;
} 

void i_log(std::vector<double> &vec, std::vector<double> &log_vec){
    for(long int i = 0; i < vec.size(); i++){
        log_vec.push_back(log(vec[i]));
    }
 }

