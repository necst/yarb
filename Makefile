############## Benchmark MAKEFILE #######
## Supported platform: Linux, MacOS
SHELL:=/bin/bash
BENCHMARK_ROOT:=$(shell pwd)

### REGEX FILE LIMTITS ###
RE_FILE_START_LINE=0
RE_FILE_END_LINE=4000


### Alveare Compiler ###
CC=gcc
CCFLAGS=-w
YACC=bison
FLEX=flex
LIBRARIES=ast.c
HEADERS=ast.h
M_COMP_SCRIPT=massive_compilation
ALVEARE_COMPILER_NAME = compiler

### GPU ###
GPU_FOLDER=gpu
GPU_SOURCE_FOLDER=$(GPU_FOLDER)/gpunfa-artifact
GPU_SETUP_SCRIPT=setup
GPU_TEST_SCRIPT=gpu_test
GPU_WORK=infant
GPU_WORK_ALGORITHM=infant
GPU_TEST_FOLDER=gpu
GPU_WORK_TEST_FOLDER=$(GPU_WORK)


UNAME_S:=$(shell uname -s)
PYTHON=python3

### GREP ###
#GREP_TEST_SCRIPT=grep_test
GREP_TEST_SCRIPT=grep_test
GREP_TEST_FOLDER=grep

### RE2 ###
LOCAL_LIB_DIR=/usr/local/lib
RE2_COMPILER=g++
RE2_CFLAGS=-std=c++11 -pthread
RE2_LFLGAGS=-I/usr/local/include -L/usr/local/lib -g0 -lre2 -O3
RE2_DEFINES+=-D RE2_OPTIONS

RE2_TEST_FOLDER=re2
RE2_SRC_TEST=re2_test.cpp
RE2_APP_TEST=re2_test

### Hyperscan ###
HS_COMPILER=g++
HS_CFLAGS?=-I/usr/local/include/hs/ -std=c++11
HS_LFLAGS=-L/usr/local/lib -lhs

HS_TEST_FOLDER=hs
HS_SRC_TEST=hs_test.cpp
HS_APP_TEST=hs_test

### Validation ###
TEST_VALIDATION_SCRIPT=validation

### Test Data & Information ###
REGEX_FILE=regex
ANML_FILE=regex
DATA_FILE=data_1MB

CHUNCK_MULT_FACTOR=1000
CHUNCK_SIZE_VAL=16
CHUNK_NAME=chunk

#0 means the complete file
ifeq ($(CHUNCK_SIZE_VAL), 0)
	CHUNCK_SIZE=$(shell wc -c < ${TESTBENCH_NAME}/${DATA_FILE}.dat)
else
	CHUNCK_SIZE=$(shell echo ${CHUNCK_SIZE_VAL}*${CHUNCK_MULT_FACTOR} | bc)
endif


REPETITIONS=10
HEADER_CSV=header
HEADER_COMP_CSV=header_comp
OUTPUT_CSV=output
OUTPUT_COMP_CSV=output_comp


### ANML network splitter ###
ANML_SPLITTER=anml_splitter
ANML_AUTOMATA_COUNTER=automata_counter



### PCRE to ANML
VASIM=../VASim/vasim
PCRE2MNRL=../hscompile/build/pcre2mnrl
PCRE2ANML_SCRIPT=pcre2anml.py
TEST_AUTOMATA_FOLDER=automata
ANML_AUTOMATA_COUNTER=automata_counter
PCRE2ANML_FOLDER=./pcre2anml

### CSV Format ###
CSV_SEPARATOR=" "


### Test Folder Information ###
TESTBENCH_NAME=PowerEN
TEST_ROOT_FOLDER=$(TESTBENCH_NAME)_massive_test


### Validation ###
TESTED_1=grep
TESTED_2=re2


### Multimatch or Mono-match mode
# 0 monomatch 
# 1 multimatch
MATCH_MODE=0

TEST_INSTR_FOLDER=instr
TEST_DATA_FOLDER=data
TEST_AUTOMATA_FOLDER=automata

DATA_SPLITTER = data_splitter
DATA_OVERLAP=100
FREQ = 150
FGPA_SCRIPT = loader
N_CORE = 1
BITSTREAM = design_1_wrapper
OUTPUT = output

ifeq ($(UNAME_S),Linux)
	CCFLAGS += -lfl
endif
ifeq ($(UNAME_S),Darwin)
	CCFLAGS += -ll
endif	

.PHONY: hs_compile gpu_compile re2_compile get_compiler cpu_test test_validation clean_re2 re2_build grep_test re2_test $(TEST_ROOT_FOLDER) pcre2anml anml_splitter

all: get_compiler cpu_test  test_validation

get_compiler: $(ALVEARE_COMPILER_NAME)

$(YACC): $(ALVEARE_COMPILER_NAME).y
	$(YACC) -d $(ALVEARE_COMPILER_NAME).y -v		

$(FLEX): $(ALVEARE_COMPILER_NAME).lex
	$(FLEX) -o $(ALVEARE_COMPILER_NAME).yy.c $(ALVEARE_COMPILER_NAME).lex 

$(ALVEARE_COMPILER_NAME): $(FLEX) $(YACC) $(HEADERS) $(LIBRARIES)
	$(info ******** OBTAINING COMPILER ********)
	$(CC) -o $@ $(ALVEARE_COMPILER_NAME).yy.c $(ALVEARE_COMPILER_NAME).tab.c $(LIBRARIES) $(CCFLAGS)

$(TEST_ROOT_FOLDER): $(TESTBENCH_NAME)/$(DATA_FILE).dat $(TESTBENCH_NAME)/$(REGEX_FILE).txt $(HEADER_CSV).csv
	$(info ******** CREATING TEST FOLDER ********)
	mkdir -p $(TEST_ROOT_FOLDER)
	rm -f -r $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)
	mkdir -p $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)
	mkdir -p $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(TEST_AUTOMATA_FOLDER)
	cp $(HEADER_CSV).csv  $(TEST_ROOT_FOLDER)
	cp $(HEADER_COMP_CSV).csv  $(TEST_ROOT_FOLDER)
	mkdir -p $(TEST_ROOT_FOLDER)/$(TEST_INSTR_FOLDER)
	mkdir -p $(TEST_ROOT_FOLDER)/$(FPGA_TEST_FOLDER)
	echo ${CHUNCK_SIZE}
	$(PYTHON) $(DATA_SPLITTER).py $(TESTBENCH_NAME)/$(DATA_FILE).dat $(CHUNCK_SIZE) $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(DATA_OVERLAP)
	cp $(TESTBENCH_NAME)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)
	cp -fr $(TESTBENCH_NAME)/automata $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) || true

anml_splitter: $(TEST_ROOT_FOLDER)
	$(info ******** ANML NETWORK SPLITTING ********)
	$(PYTHON) $(ANML_SPLITTER).py $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(ANML_FILE).anml $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(TEST_AUTOMATA_FOLDER)/ $(ANML_AUTOMATA_COUNTER).txt

cpu_test: $(TEST_ROOT_FOLDER) grep_test re2_test

grep_build: $(TEST_ROOT_FOLDER)
	mkdir -p $(TEST_ROOT_FOLDER)/$(GREP_TEST_FOLDER)

grep_test: grep_build $(TEST_ROOT_FOLDER)/$(HEADER_CSV).csv
	$(info ******** GREP TEST ********)
	cp $(TEST_ROOT_FOLDER)/$(HEADER_CSV).csv $(TEST_ROOT_FOLDER)/$(GREP_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv
	$(PYTHON) $(GREP_TEST_SCRIPT).py $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(TEST_ROOT_FOLDER)/$(GREP_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv $(REPETITIONS) $(CSV_SEPARATOR) $(MATCH_MODE)

test_validation: $(TEST_ROOT_FOLDER)/$(TESTED_1) $(TEST_ROOT_FOLDER)/$(TESTED_2)
	$(PYTHON) $(TEST_VALIDATION_SCRIPT).py $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TESTED_1)/$(OUTPUT_CSV)_$(MATCH_MODE).csv $(TEST_ROOT_FOLDER)/$(TESTED_2)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv $(CSV_SEPARATOR)

clean_all: clean_compiler clean_test clean_re2

### PCRE2ANML ###
pcre2anml: $(PCRE2ANML_FOLDER)/$(PCRE2ANML_SCRIPT) $(PCRE2MNRL) $(VASIM) $(TESTBENCH_NAME)/$(REGEX_FILE).txt
	mkdir -p $(TESTBENCH_NAME)/$(TEST_AUTOMATA_FOLDER)
	touch $(TESTBENCH_NAME)/$(TEST_AUTOMATA_FOLDER)/$(ANML_AUTOMATA_COUNTER).txt
	$(PYTHON) $(PCRE2ANML_FOLDER)/$(PCRE2ANML_SCRIPT) $(PCRE2MNRL) $(VASIM) $(TESTBENCH_NAME)/$(REGEX_FILE).txt $(TESTBENCH_NAME)/$(TEST_AUTOMATA_FOLDER)/$(ANML_AUTOMATA_COUNTER).txt $(TESTBENCH_NAME)/$(TEST_AUTOMATA_FOLDER)/

### GPU ###
gpu_compile:
	@cd $(GPU_SOURCE_FOLDER)/;\
	source $(GPU_SETUP_SCRIPT).sh

gpu_build: $(TEST_ROOT_FOLDER)
	$(info ******** BUILD GPU ********)
	mkdir -p $(TEST_ROOT_FOLDER)/$(GPU_TEST_FOLDER)
	mkdir -p $(TEST_ROOT_FOLDER)/$(GPU_TEST_FOLDER)/$(GPU_WORK_TEST_FOLDER)
	cp $(TEST_ROOT_FOLDER)/$(HEADER_CSV).csv $(TEST_ROOT_FOLDER)/$(GPU_TEST_FOLDER)/$(GPU_WORK_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_$(MATCH_MODE).csv


gpu_test:
	$(info ******** TEST GPU ********)
	@AUTOMATA_COUNTER=$$(cat $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(TEST_AUTOMATA_FOLDER)/$(ANML_AUTOMATA_COUNTER).txt);\
	DATA_CHUNK_COUNTER=$$(cat $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(CHUNK_NAME).txt);\
	PATH=$(BENCHMARK_ROOT)/$(GPU_SOURCE_FOLDER)/gpunfa_code/build/bin:$(PATH);\
	$(PYTHON) $(GPU_FOLDER)/$(GPU_TEST_SCRIPT).py $(GPU_WORK) $(GPU_WORK_ALGORITHM) $${AUTOMATA_COUNTER} $${DATA_CHUNK_COUNTER} $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(TEST_AUTOMATA_FOLDER) $(TEST_ROOT_FOLDER)/$(GPU_TEST_FOLDER)/$(GPU_WORK_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_$(MATCH_MODE).csv $(REPETITIONS) $(CSV_SEPARATOR) $(MATCH_MODE)

### RE2 ###
re2_compile:
	$(RE2_COMPILER) $(RE2_CFLAGS) $(RE2_DEFINES) -o $(RE2_APP_TEST) $(RE2_SRC_TEST) $(RE2_LFLGAGS)

re2_build: $(RE2_SRC_TEST) $(TEST_ROOT_FOLDER)
	$(info ******** BUILD RE2 ********)
	mkdir -p $(TEST_ROOT_FOLDER)/$(RE2_TEST_FOLDER)


re2_test: re2_build
	$(info ******** RE2 TEST ********)
	cp $(TEST_ROOT_FOLDER)/$(HEADER_COMP_CSV).csv $(TEST_ROOT_FOLDER)/$(RE2_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv
ifeq ($(UNAME_S),Darwin)
	DYLD_LIBRARY_PATH="$(LOCAL_LIB_DIR):$(LD_LIBRARY_PATH)" ./$(RE2_APP_TEST) $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(TEST_ROOT_FOLDER)/$(RE2_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv $(REPETITIONS) $(CSV_SEPARATOR) $(MATCH_MODE)
else
	LD_LIBRARY_PATH="$(LOCAL_LIB_DIR):$(LD_LIBRARY_PATH)" ./$(RE2_APP_TEST) $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(TEST_ROOT_FOLDER)/$(RE2_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv $(REPETITIONS) $(CSV_SEPARATOR) $(MATCH_MODE)
endif

### Hyperscan ###
hs_compile:
	$(HS_COMPILER) $(HS_CFLAGS) $(HS_DEFINES) -o $(HS_APP_TEST) $(HS_SRC_TEST) $(HS_LFLAGS)


hs_build: $(HS_SRC_TEST) $(TEST_ROOT_FOLDER)
	$(info ******** BUILD HS ********)
	mkdir -p $(TEST_ROOT_FOLDER)/$(HS_TEST_FOLDER)


hs_test: hs_build
	$(info ******** HS TEST ********)
	cp $(TEST_ROOT_FOLDER)/$(HEADER_COMP_CSV).csv $(TEST_ROOT_FOLDER)/$(HS_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv
ifeq ($(UNAME_S),Darwin)
	DYLD_LIBRARY_PATH="$(LOCAL_LIB_DIR):$(LD_LIBRARY_PATH)" ./$(HS_APP_TEST) $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(TEST_ROOT_FOLDER)/$(HS_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv $(REPETITIONS) $(CSV_SEPARATOR) $(MATCH_MODE)
else
	LD_LIBRARY_PATH="$(LOCAL_LIB_DIR):$(LD_LIBRARY_PATH)" ./$(HS_APP_TEST) $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER)/$(REGEX_FILE).txt $(TEST_ROOT_FOLDER)/$(TEST_DATA_FOLDER) $(TEST_ROOT_FOLDER)/$(HS_TEST_FOLDER)/$(OUTPUT_COMP_CSV)_$(DATA_FILE)_${CHUNCK_SIZE}_$(MATCH_MODE).csv $(REPETITIONS) $(CSV_SEPARATOR) $(MATCH_MODE)
endif

#Simple Clean
clean_hs:
	rm -f $(HS_APP_TEST)

clean_re2:
	rm -f $(RE2_APP_TEST)


	
clean_test: 
	$(info ******** CLEANING TEST FOLDER ********)
	rm -r -f $(TEST_ROOT_FOLDER)

benchmark_re_filter:
	sed -E '/(^$$)|(\\([0-9]+))|(\\i)|(\\ *$$)/d' ./$(TESTBENCH_NAME)/regex.txt > ./$(TESTBENCH_NAME)/regex1.txt
	sed -E 's/(^ *\/)//g' ./$(TESTBENCH_NAME)/regex1.txt > ./$(TESTBENCH_NAME)/regex.txt
	sed -E 's/([^\\])\/.*$$/\1/g' ./$(TESTBENCH_NAME)/regex.txt > ./$(TESTBENCH_NAME)/regex1.txt
	tail -n +$(RE_FILE_START_LINE) ./$(TESTBENCH_NAME)/regex1.txt | head -n +$(RE_FILE_END_LINE) > ./$(TESTBENCH_NAME)/regex.txt

###     Getting Testebench from ANMLZoo repository   ###
get_brill_tb:
	$(info ******** CREATING Brill TEST FOLDER ********)
	mkdir -p Brill
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Brill/anml/brill.1chip.anml -o ./Brill/regex.anml
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Brill/regex/brill.1chip.regex -o ./Brill/regex.txt
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Brill/inputs/brill_1MB.input -o ./Brill/data_1MB.dat
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Brill/inputs/brill_10MB.input -o ./Brill/data_10MB.dat

get_poweren_tb:
	$(info ******** CREATING PowerEN TEST FOLDER ********)
	mkdir -p PowerEN
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/PowerEN/anml/complx_01000_00123.1chip.anml -o ./PowerEN/regex.anml
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/PowerEN/regex/complx_01000_00123.1chip.regex -o ./PowerEN/regex.txt
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/PowerEN/inputs/poweren_1MB.input -o ./PowerEN/data_1MB.dat
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/PowerEN/inputs/poweren_10MB.input -o ./PowerEN/data_10MB.dat

get_clamav_tb:
	$(info ******** CREATING ClamAV TEST FOLDER ********)
	mkdir -p ClamAV
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/ClamAV/regex/515_nocounter.1chip.anml -o ./ClamAV/regex.txt
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/ClamAV/anml/515_nocounter.1chip.anml -o ./ClamAV/regex.anml
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/ClamAV/inputs/vasim_1MB.input -o ./ClamAV/data_1MB.dat
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/ClamAV/inputs/vasim_10MB.input -o ./ClamAV/data_10MB.dat

get_protomata_tb:
	$(info ******** CREATING Protomata TEST FOLDER ********)
	mkdir -p Protomata
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Protomata/anml/2340sigs.1chip.anml -o ./Protomata/regex.anml
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Protomata/regex/2340sigs.1chip.regex -o ./Protomata/regex.txt
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Protomata/inputs/uniprot_fasta_1MB.input -o ./Protomata/data_1MB.dat
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Protomata/inputs/uniprot_fasta_10MB.input -o ./Protomata/data_10MB.dat

get_dotstar_tb:
	$(info ******** CREATING Dotstar TEST FOLDER ********)
	mkdir -p Dotstar
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Dotstar/anml/backdoor_dotstar.1chip.anml -o ./Dotstar/regex.anml
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Dotstar/regex/backdoor_dotstar.1chip.regex -o ./Dotstar/regex.txt
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Dotstar/inputs/backdoor_1MB.input -o ./Dotstar/data_1MB.dat
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Dotstar/inputs/backdoor_10MB.input -o ./Dotstar/data_10MB.dat

get_snort_tb:
	$(info ******** CREATING Dotstar TEST FOLDER ********)
	mkdir -p Snort
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Snort/anml/snort.1chip.anml -o ./Snort/regex.anml
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Snort/regex/snort.1chip.regex -o ./Snort/regex.txt
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/Snort/inputs/snort_1MB.input -o ./Snort/data_1MB.dat
	curl https://raw.githubusercontent.com/jackwadden/ANMLZoo/master/SNort/inputs/snort_10MB.input -o ./Snort/data_10MB.dat


