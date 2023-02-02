import sys

from numpy import mat


def file_line_counter(file_name):
    file = open(file_name, 'r')
    count_line = 0
    for line in file: 
        if len(line) > 0:
            count_line = count_line+ 1
    file.close()
    return count_line

count_regex = file_line_counter(sys.argv[1])
count_tested_1 = file_line_counter(sys.argv[2]) - 1 # -1 is for the header line
count_tested_2 = file_line_counter(sys.argv[3]) - 1 # -1 is for the header line

csv_separator = sys.argv[4]
correct_execution = 0

#check if the number of regex and the number of results match
if count_regex != count_tested_1 or count_regex != count_tested_2:
    print("Different output lengths: " + str(count_regex) + " " + str(count_tested_1) + " " + str(count_tested_2))
    exit()

tested1_output_f = open(sys.argv[2], 'r')
tested2_output_f = open(sys.argv[3], 'r')

#discard the first line from the output file
tested1_output_f.readline()
tested2_output_f.readline()

for line in tested1_output_f:
    line2 = tested2_output_f.readline()
    match_tested_1 = line.split(csv_separator)[0]
    match_tested_2 = line.split(csv_separator)[0]
    if match_tested_1 == match_tested_2:
        correct_execution = correct_execution + 1
print("Accuracy: ", (correct_execution/count_regex)*100)
tested1_output_f.close()
tested2_output_f.close()