#!/bin/bash
benchmark=("Brill" "PowerEN" "Protomata" "ClamAV" "Dotstar")

platform=("hs")
chunk_size=("1")
data_file=("data_1MB")
#data_file=("data_1MB" "data_10MB")
match_mode=("0" "1")
#match_mode=("0" "1")
start_regex=0
end_regex=4000

#additional setup for GPU test
#can be removed for other platforms test
gpu_work=infant
gpu_work_algorithm=infant
for p in "${platform[@]}"
  do
    make ${p}_compile
  done

for b in "${benchmark[@]}"
do
    benchmark_lowercase=$(echo "$b" | awk '{print tolower($0)}')
    echo ${benchmark_lowercase}
    make get_${benchmark_lowercase}_tb
    make benchmark_re_filter TESTBENCH_NAME=${b} RE_FILE_START_LINE=${start_regex} RE_FILE_END_LINE=${end_regex}
    if [ ${p} -eq "gpu"]; then
    	make pcre2anml TESTBENCH_NAME=${b}
    fi

    for p in "${platform[@]}"
    do
      for cs in "${chunk_size[@]}"
      do
        for df in "${data_file[@]}"
        do
            for mm in "${match_mode[@]}"
            do
	            make ${p}_build TESTBENCH_NAME=${b} MATCH_MODE=${mm} DATA_FILE=${df} CHUNCK_SIZE_VAL=${cs} GPU_WORK=${gpu_work} GPU_WORK_ALGORITHM=${gpu_work_algorithm}

              make ${p}_test TESTBENCH_NAME=${b} MATCH_MODE=${mm} DATA_FILE=${df} CHUNCK_SIZE_VAL=${cs} GPU_WORK=${gpu_work} GPU_WORK_ALGORITHM=${gpu_work_algorithm}
            done
          done
        done
    done
done
