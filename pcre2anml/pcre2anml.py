#!/usr/bin/python
import sys
import subprocess, time

pcre_delimiter = "/"
pcre2mnrl = sys.argv[1]
vasim = sys.argv[2]
regex_file = open(sys.argv[3], "r")
automata_counter_file = open(sys.argv[4], "w")
automa_folder_path = sys.argv[5]
tmp_regex_file_name = automa_folder_path + "tmp_regex.txt"
tmp_automaton_file_name = automa_folder_path + "tmp_automaton.mnrl"
automaton_file_prefix = "automaton"



#process = subprocess.Popen(vasim + "", stdout=subprocess.PIPE, shell=True)
#output, error = process.communicate()
automata_number = 0
for line in regex_file:
    line = line.replace('\n', '')
    if(len(line) > 0):
        if(line[0] != '/'):
            tmp_regex_file = open(tmp_regex_file_name, "w")
            regex_to_convert = pcre_delimiter + line + pcre_delimiter
            tmp_regex_file.write(regex_to_convert)
            tmp_regex_file.close()
            print("processing regex number: " + str(automata_number))
            process = subprocess.Popen(pcre2mnrl + " " + tmp_regex_file_name + " " + tmp_automaton_file_name, stdout=subprocess.PIPE, shell=True)
            output, error = process.communicate()
            process = subprocess.Popen(vasim + " " + tmp_automaton_file_name + " -a ; mv automata_0.anml " + automa_folder_path + automaton_file_prefix + str(automata_number) + ".anml", stdout=subprocess.PIPE, shell=True)
            output, error = process.communicate()
            process = subprocess.Popen("rm " + tmp_automaton_file_name + " ; rm " + tmp_regex_file_name , stdout=subprocess.PIPE, shell=True)
            output, error = process.communicate()
            automata_number = automata_number + 1
automata_counter_file.write(str(automata_number))
automata_counter_file.close() 