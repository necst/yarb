/*
 * Copyright (c) 2015, Intel Corporation
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *  * Neither the name of Intel Corporation nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * Hyperscan example program 1: simplegrep
 *
 * This is a simple example of Hyperscan's most basic functionality: it will
 * search a given input file for a pattern supplied as a command-line argument.
 * It is intended to demonstrate correct usage of the hs_compile and hs_scan
 * functions of Hyperscan.
 *
 * Patterns are scanned in 'DOTALL' mode, which is equivalent to PCRE's '/s'
 * modifier. This behaviour can be changed by modifying the "flags" argument to
 * hs_compile.
 *
 * Build instructions:
 *
 *     gcc -o simplegrep simplegrep.c $(pkg-config --cflags --libs libhs)
 *
 * Usage:
 *
 *     ./simplegrep <pattern> <input file>
 *
 * Example:
 *
 *     ./simplegrep int simplegrep.c
 *
 */

#include <errno.h>
#include <limits.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <iostream>
#include <chrono>
#include <fstream>
#include <string>
#include <cmath>
#include <vector>
#include <hs.h>


double i_avg(std::vector<double> &vec);
void i_log(std::vector<double > &vec, std::vector<double> &log_vec);
int try_match (const char * data, const char *regex_file_name, 
        const char *output_file_name, int iterations, char * csv_separator, bool multi_match);
static int all_re_match;
/**
 * This is the function that will be called for each match that occurs. @a ctx
 * is to allow you to have some application-specific state that you will get
 * access to for each match. In our simple example we're just going to use it
 * to pass in the pattern that was being searched for so we can print it out.
 */
static int eventHandlerMultiMatch(unsigned int id, unsigned long long from,
                        unsigned long long to, unsigned int flags, void *ctx) {
    //printf("Match for pattern \"%s\" at offset %llu\n", (char *)ctx, to);
    all_re_match++;
    return 0;
}

static char *readInputData(const char *inputFN, unsigned int *length);


int try_match (const char * data, const char *regex_file_name,
                  const char *output_file_name, int iterations, char *csv_separator, bool multi_match){
    std::string n;
    int i, match_count;
    std::ifstream input_re(regex_file_name);
    std::string chunks_file_name(data);
    chunks_file_name.append("/chunk.txt");
    std::ifstream chunks_file(chunks_file_name);
    bool result = false;
    std::string line;
    std::string chunks_line;
    int chunks_number;
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
    
    hs_database_t *database;
    unsigned int length;
    char *inputData;
    hs_compile_error_t *compile_err;
    hs_scratch_t *scratch = NULL;

    //getting number of data chunks
    std::getline(chunks_file, chunks_line);
    chunks_number = atoi(chunks_line.c_str());
    chunks_file.close();
    //iterate over regexes
    int a = 0;
    int b = 0;
    int re_counter = 0;
    while(std::getline(input_re, line)){
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
	int error = 0;
        for(int j = 0; j < chunks_number && error == 0; j++){
            std::string chunk_str = std::to_string(j);
            std::string data_path(data);
            data_path.append("/data");
            data_path.append(chunk_str);
            data_path.append(".dat");

            inputData = readInputData(data_path.c_str(), &length);
            if (!inputData) {
                hs_free_database(database);
                return -1;
            }
	    
            for(int i = 0; i < iterations && error == 0; i++){
                all_re_match = 0;

                start_comp = std::chrono::high_resolution_clock::now();
                /* Compilation */
                if (multi_match)
                {
                    if (hs_compile(line.c_str(), HS_FLAG_DOTALL, HS_MODE_BLOCK, NULL, &database,
                            &compile_err) != HS_SUCCESS) {
                    fprintf(stderr, "ERROR: Unable to compile pattern \"%s\": %s\n",
                            line.c_str(), compile_err->message);
                    hs_free_compile_error(compile_err);
                    error = 1;
		    continue;
                }
                }else{
                    if (hs_compile(line.c_str(), HS_FLAG_DOTALL | HS_FLAG_SINGLEMATCH, HS_MODE_BLOCK, NULL, &database,
                                &compile_err) != HS_SUCCESS) {
                        fprintf(stderr, "ERROR: Unable to compile pattern \"%s\": %s\n",
                                line.c_str(), compile_err->message);
                        hs_free_compile_error(compile_err);
                        error = 1;
			continue;
                }
                }
                /* End of Compilation */
                finish_comp = std::chrono::high_resolution_clock::now();
                if (hs_alloc_scratch(database, &scratch) != HS_SUCCESS) {
                    fprintf(stderr, "ERROR: Unable to allocate scratch space. Exiting.\n");
                    free(inputData);
                    hs_free_database(database);
                    error = 1;
                }

                result = false;

                if(multi_match){
                    start_exe = std::chrono::high_resolution_clock::now();
                    if (hs_scan(database, inputData, length, 0, scratch, eventHandlerMultiMatch,
                                NULL) != HS_SUCCESS) {
                        fprintf(stderr, "ERROR: Unable to scan input buffer. Exiting.\n");
                        hs_free_scratch(scratch);
                        free(inputData);
                        hs_free_database(database);
                        error = 1;
                    }
                    finish_exe = std::chrono::high_resolution_clock::now();
                }else{
                    start_exe = std::chrono::high_resolution_clock::now();
                    if (hs_scan(database, inputData, length, 0, scratch, NULL,
                                NULL) != HS_SUCCESS) {
                        fprintf(stderr, "ERROR: Unable to scan input buffer. Exiting.\n");
                        hs_free_scratch(scratch);
                        free(inputData);
                        hs_free_database(database);
                        error = 1;
                    }
                    finish_exe = std::chrono::high_resolution_clock::now();
                }
                times_comp.push_back(std::chrono::duration_cast<std::chrono::nanoseconds>(finish_comp-start_comp).count());
                times_exe.push_back(std::chrono::duration_cast<std::chrono::nanoseconds>(finish_exe-start_exe).count());
            }
            if(multi_match){
                match_count += all_re_match;
            }else{
                if(result){
                    match_count++;
                }
            }
            if(error == 0){
                avgs_comp.push_back(i_avg(times_comp));
                avgs_exe.push_back(i_avg(times_exe));
            }

            /* Scanning is complete, any matches have been handled, so now we just
            * clean up and exit.
            */
            hs_free_database(database);
            hs_free_scratch(scratch);
            free(inputData);
            scratch = NULL;
        }
	if(error == 1){
        output.precision(10);
	    output << -1 << csv_separator;
        output << 0 << csv_separator;
	    output << 0 << csv_separator;
	    output << 0 << csv_separator;
	    output << 0 << "\n";
	}
	else{
	    i_log(avgs_comp, logs_comp);
        i_log(avgs_exe, logs_exe);
        output.precision(10);
        output << match_count << csv_separator;        
        output << i_avg(avgs_exe) << csv_separator;
        output << exp(i_avg(logs_exe)) << csv_separator;
        output << i_avg(avgs_comp) << csv_separator;
        output << exp(i_avg(logs_comp)) << "\n";
        output.close();
	} 
    }
    input_re.close();
    return 0;
}

double i_avg(std::vector<double> &vec){
    unsigned long sum;
    double avg;
    avg = 0;
    sum = 0;

    for(unsigned long int i = 0; i < vec.size(); i++){
        sum += vec[i];
    }
    //std::cout<<"sum "<<sum<<"  "<<vec.size();

    avg = sum / vec.size();
    return avg;
} 

void i_log(std::vector<double> &vec, std::vector<double> &log_vec){
    for(unsigned long int i = 0; i < vec.size(); i++){
        log_vec.push_back(log(vec[i]));
    }
 }

int main(int argc, char *argv[]) {
    if (argc < 6 ) {
        fprintf(stderr, "Usage: %s <pattern> <input file> <output file> <repetitions> <csv_seperator>\n", argv[0]);
        return -1;
    }


    char *pattern = argv[1];
    char *inputFN = argv[2];
    char *outputFN = argv[3];
    int iterations=atoi(argv[4]);
    char *csv_separator = argv[5];
    bool multi_match = (argv[6][0] == '0') ? false : true;
    if(try_match(inputFN, pattern, outputFN, iterations, csv_separator, multi_match) != 0){
        std::cout << "ERROR that has been an error in the procedure"<< std::endl;
    }

//     /* First, we attempt to compile the pattern provided on the command line.
//      * We assume 'DOTALL' semantics, meaning that the '.' meta-character will
//      * match newline characters. The compiler will analyse the given pattern and
//      * either return a compiled Hyperscan database, or an error message
//      * explaining why the pattern didn't compile.
//      */

//     hs_database_t *database;
//     /* Next, we read the input data file into a buffer. */
//     unsigned int length;
//     char *inputData = readInputData(inputFN, &length);
//     if (!inputData) {
//         hs_free_database(database);
//         return -1;
//     }

// auto sTime = std::chrono::high_resolution_clock::now();
//     hs_compile_error_t *compile_err;
//     if (multi_match)
//     {
//         if (hs_compile(pattern, HS_FLAG_DOTALL, HS_MODE_BLOCK, NULL, &database,
//                    &compile_err) != HS_SUCCESS) {
//         fprintf(stderr, "ERROR: Unable to compile pattern \"%s\": %s\n",
//                 pattern, compile_err->message);
//         hs_free_compile_error(compile_err);
//         return -1;
//     }
//     }else{
//         if (hs_compile(pattern, HS_FLAG_DOTALL | HS_FLAG_SINGLEMATCH, HS_MODE_BLOCK, NULL, &database,
//                     &compile_err) != HS_SUCCESS) {
//             fprintf(stderr, "ERROR: Unable to compile pattern \"%s\": %s\n",
//                     pattern, compile_err->message);
//             hs_free_compile_error(compile_err);
//             return -1;
//         }
//     }
// auto eeee = std::chrono::high_resolution_clock::now();


//     /* Finally, we issue a call to hs_scan, which will search the input buffer
//      * for the pattern represented in the bytecode. Note that in order to do
//      * this, scratch space needs to be allocated with the hs_alloc_scratch
//      * function. In typical usage, you would reuse this scratch space for many
//      * calls to hs_scan, but as we're only doing one, we'll be allocating it
//      * and deallocating it as soon as our matching is done.
//      *
//      * When matches occur, the specified callback function (eventHandler in
//      * this file) will be called. Note that although it is reminiscent of
//      * asynchronous APIs, Hyperscan operates synchronously: all matches will be
//      * found, and all callbacks issued, *before* hs_scan returns.
//      *
//      * In this example, we provide the input pattern as the context pointer so
//      * that the callback is able to print out the pattern that matched on each
//      * match event.
//      */
//     hs_scratch_t *scratch = NULL;
//     if (hs_alloc_scratch(database, &scratch) != HS_SUCCESS) {
//         fprintf(stderr, "ERROR: Unable to allocate scratch space. Exiting.\n");
//         free(inputData);
//         hs_free_database(database);
//         return -1;
//     }

//     //printf("Scanning %u bytes with Hyperscan\n", length);


// double overall_duration=0.0;
// for(int i=0; i < iterations; i++){

// auto ssTime = std::chrono::high_resolution_clock::now();
//     if (multi_match)
//     {
//         if (hs_scan(database, inputData, length, 0, scratch, eventHandler,
//                     pattern) != HS_SUCCESS) {
//             fprintf(stderr, "ERROR: Unable to scan input buffer. Exiting.\n");
//             hs_free_scratch(scratch);
//             free(inputData);
//             hs_free_database(database);
//             return -1;
//         }
//     }else{
//         if (hs_scan(database, inputData, length, 0, scratch, NULL,
//                     pattern) != HS_SUCCESS) {
//             fprintf(stderr, "ERROR: Unable to scan input buffer. Exiting.\n");
//             hs_free_scratch(scratch);
//             free(inputData);
//             hs_free_database(database);
//             return -1;
//         }
//     }
// auto endTime = std::chrono::high_resolution_clock::now();

//     if(i > 9){
//           overall_duration += std::chrono::duration_cast<std::chrono::nanoseconds>(endTime-ssTime + eeee - sTime).count();
//     }
//   }


// std::cout << pattern << "\t" << (overall_duration/(iterations-10))<<"\n";

//     /* Scanning is complete, any matches have been handled, so now we just
//      * clean up and exit.
//      */
//     hs_free_scratch(scratch);
//     free(inputData);
//     hs_free_database(database);
    return 0;
}



/**
 * Fill a data buffer from the given filename, returning it and filling @a
 * length with its length. Returns NULL on failure.
 */
static char *readInputData(const char *inputFN, unsigned int *length) {
    FILE *f = fopen(inputFN, "rb");
    if (!f) {
        fprintf(stderr, "ERROR: unable to open file \"%s\": %s\n", inputFN,
                strerror(errno));
        return NULL;
    }

    /* We use fseek/ftell to get our data length, in order to keep this example
     * code as portable as possible. */
    if (fseek(f, 0, SEEK_END) != 0) {
        fprintf(stderr, "ERROR: unable to seek file \"%s\": %s\n", inputFN,
                strerror(errno));
        fclose(f);
        return NULL;
    }
    long dataLen = ftell(f);
    if (dataLen < 0) {
        fprintf(stderr, "ERROR: ftell() failed: %s\n", strerror(errno));
        fclose(f);
        return NULL;
    }
    if (fseek(f, 0, SEEK_SET) != 0) {
        fprintf(stderr, "ERROR: unable to seek file \"%s\": %s\n", inputFN,
                strerror(errno));
        fclose(f);
        return NULL;
    }

    /* Hyperscan's hs_scan function accepts length as an unsigned int, so we
     * limit the size of our buffer appropriately. */
    if ((unsigned long)dataLen > UINT_MAX) {
        dataLen = UINT_MAX;
        printf("WARNING: clipping data to %ld bytes\n", dataLen);
    } else if (dataLen == 0) {
        fprintf(stderr, "ERROR: input file \"%s\" is empty\n", inputFN);
        fclose(f);
        return NULL;
    }

    char *inputData = (char*)malloc(dataLen);
    if (!inputData) {
        fprintf(stderr, "ERROR: unable to malloc %ld bytes\n", dataLen);
        fclose(f);
        return NULL;
    }

    char *p = inputData;
    size_t bytesLeft = dataLen;
    while (bytesLeft) {
        size_t bytesRead = fread(p, 1, bytesLeft, f);
        bytesLeft -= bytesRead;
        p += bytesRead;
        if (ferror(f) != 0) {
            fprintf(stderr, "ERROR: fread() failed\n");
            free(inputData);
            fclose(f);
            return NULL;
        }
    }

    fclose(f);

    *length = (unsigned int)dataLen;
    return inputData;
}
