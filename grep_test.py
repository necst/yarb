#!/usr/bin/python
import sys
import subprocess, time
import numpy as np

def escape(str):
    return str.replace("'", "'\"'\"'")

regex_f = open(sys.argv[1], 'r')
chunk_f = open(sys.argv[2] + "/chunk.txt", 'r')
repetitions = int(sys.argv[4])

csv_separator = sys.argv[5]
multi_match = sys.argv[6]

for line in chunk_f:
    chunks_num = int(line[0:len(line)-1])
chunk_f.close()

timeStarted = time.time()
#process = subprocess.Popen("grep -P -q \"^\" " + sys.argv[2] +" -m 1", stdout=subprocess.PIPE, shell=True)
process = subprocess.Popen("", stdout=subprocess.PIPE, shell=True)
output, error = process.communicate()
overhead = time.time() - timeStarted

for line in regex_f:
    output_f = open(sys.argv[3], 'a')
    line = escape(line[0:len(line) - 1])
    if len(line) > 0:
        if line[0] == '/':
            line = line[1:(len(line)-1)]
    print("grep -m 1 -P -q --binary-files=text -- '" + line + "' " + sys.argv[2] + "/data" + ".dat")
    avgs = []
    match_count = 0
    for chunk in range(0, chunks_num):
        times = []
        #print("grep -E -q \"" + line + "\" " + sys.argv[2] + "/data" + str(chunk) + ".txt" +" -m 1")
        for x in range(0, repetitions):
            if multi_match == '0':
                timeStarted = time.time()
                process = subprocess.Popen("grep -m 1 -P -q  --binary-files=text -- '" + line + "' " + sys.argv[2] + "/data" + str(chunk) + ".dat", stdout=subprocess.PIPE, shell=True)
                output, error = process.communicate()
                times.append(time.time() - timeStarted - overhead)
                match = 0
                if process.returncode == 0:
                    match = 1
                else:
                    if process.returncode > 2:
                        print("An error is occurred")
                    else:
                        match = 0
            else:
                timeStarted = time.time()
                process = subprocess.Popen("grep -o -P --binary-files=text \"" + line + "\" " + sys.argv[2] + "/data" + str(chunk) + ".dat " +"| wc -l", stdout=subprocess.PIPE, shell=True)
                output, error = process.communicate()
                times.append(time.time() - timeStarted - overhead)
                match = int(output.decode('utf-8'))
                
        avgs.append(np.average(times))
        if match >= 1:
            match_count = match_count + int(match)
    logs = np.log(avgs)
    #output_f.write(line + csv_separator)
    output_f.write(str(match_count) + csv_separator)
    output_f.write(str(np.average(avgs)) + csv_separator)
    output_f.write(str(np.exp(logs.mean())) + '\n')
    output_f.close()
regex_f.close()