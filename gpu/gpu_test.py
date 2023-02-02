#!/usr/bin/python
import sys
import subprocess, time
import numpy as np
import os

MILLISECONDS_TO_NANOSECONDS = 1000000
def get_execution_time_ms(str_to_parse):
    return float(str_to_parse.split()[3])

def get_num_match(str_to_parse):
    return int(str_to_parse.split()[7])


work_name = sys.argv[1]
algorithm_name = sys.argv[2]
automata_num = int(sys.argv[3])
chunks_num = int(sys.argv[4])
data_folder = sys.argv[5]
automata_folder = sys.argv[6]
output_file_name = sys.argv[7]

print("automata number " + str(automata_num))
print("data chunks number " + str(chunks_num))

repetitions = int(sys.argv[8])

csv_separator = sys.argv[9]
multi_match = sys.argv[10]
#####

#####

for automaton in range(automata_num):
    output_f = open(output_file_name, 'a')
    avgs = []
    match_count = 0
    for chunk in range(chunks_num):
        times = []
        for x in range(0, repetitions):
            data_to_analyze =  data_folder + "/data" + str(chunk) + ".dat"
            anml_to_execute = automata_folder + "/automaton" + str(automaton) + ".anml"
            if os.path.exists(data_to_analyze) and os.path.exists(anml_to_execute):
                print(work_name + " -i " + data_to_analyze + " -a " + anml_to_execute +" -g " + algorithm_name + " | grep -E \"(count_of_match)|(Elapsed time)\"")
                process = subprocess.Popen(work_name + " -i " + data_to_analyze + " -a " + anml_to_execute + " -g " + algorithm_name + " | grep -E \"(count_of_match)|(Elapsed time)\"", stdout=subprocess.PIPE, shell=True)
                output, error = process.communicate()
                output = output.decode('utf-8')
                match = '0'
                time = 0.0
                if len(output.splitlines()) == 2:
                    time = get_execution_time_ms(output)
                    print(time)
                    match = '0' if get_num_match(output) == 0 else '1'
                else:
                    break
                times.append(time * MILLISECONDS_TO_NANOSECONDS)
            else:
                print("not implemented")
                
        avgs.append(np.average(times))
        if match >= '1':
            match_count = match_count + int(match)
    logs = np.log(avgs)
    #output_f.write(line + csv_separator)
    output_f.write(str(match_count) + csv_separator)
    output_f.write(str(np.average(avgs)) + csv_separator)
    output_f.write(str(np.exp(logs.mean())) + '\n')
    output_f.close()
