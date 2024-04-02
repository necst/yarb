# YARB
YARB: a Methodology to Characterize Regular Expression Matching on Heterogeneous Systems
Benchmark suite is a useful tools to test REs(Regular Expression) in a standard and fair approach.
Currently, the used test benches came from ANMLZoo ANMLZoo Automata Processing Benchmark Suite: https://github.com/jackwadden/ANMLZoo

## Requirements
. Linux-based machines with Ubuntu 16.04 onwards (tested up to 20.04) and Centos 7.

. `bc` for inline multiplications

. `python3` python3 

. CUDA for GPU toolchain Please refer to NVIDIA guide [such as this reference](https://developer.nvidia.com/cuda-downloads?)

. GPU requirements:  Hyperscan, VAsim, MNRL, HScompile (see below)


## Utilization:
The benchmark suite takes as a input a regex file and a data file.
The regex standard currently supported is POSIX ERE, but we plan to expand to PCRE standard.
Input data file could be of a custom size, it will split in chunks with a customizable chunks size **CHUNK_SIZE**.

### Make commands
The RE benchmark suite exploits a Makefile, the following are the most useful commands:

#### CPU 
- make cpu_test  //execute all the supported REs CPU tools (above)

##### GREP
- make grep_test //execute tests with GREP

##### Google RE2
- make re2_build // build the RE2 folder in the benchmark root folde
- make re2_test  //execute tests with Google RE2 library

##### GPU code
Original code from [original repository](https://github.com/bigwater/gpunfa-artifact), its artifacts, and the ASPLOS 2022 paper.
- make gpu_compile
- make gpu_build
- make gpu_test

#####
Execution example:
1. make re2_build
2. make re2_test


#####
Execution example:


## HScompile installation guide

Installation steps:
1. ### Install Hyperscan 

Requirements:

- cmake
```console 
sudo apt install cmake
```
- Boost 1.57.0 or later
```console
sudo apt-get install libboost-all-dev
```
- Ragel state machine compiler
```console
sudo apt-get install ragel
```
- A minimum of g++ 4.8.1 for C++11 support

- Sqlite3

```console
sudo apt install pkg-config
```

if it is still not work:    

```console
sudo apt install sqlite3
sudo apt install libsqlite3-dev
```
Then install Hyperscan:

```console
Hyperscan ( 4.4.1):
git clone https://github.com/01org/hyperscan
cd hyperscan
git checkout v4.4.1
mkdir build
cd build
cmake ..
make
sudo make install
```

**NOTE**: if you have problem with g++ compile, and Hyperscan doesn't seem to find the correct version, remove the check from the CMakeLists.txt

2. ## Install MNRL

Requirements:
- nasm
```console
 sudo apt install nasm
 ```

install MNRL
```console
git clone https://github.com/kevinaangstadt/mnrl
cd mnrl
git checkout v1.0
cd C++
git submodule init
git submodule update   
cd lib/valijson
git checkout v0.3
cd ../..
```

edit the Makefile and change

```console
JSON = ./lib/valijson/thirdparty/nlohmann-json-1.1.0
```

with

```console
JSON = ./lib/valijson/thirdparty/nlohmann-json-3.1.2
```

then

```console
make
```

3. ## Install HScompile
```console
git clone https://github.com/kevinaangstadt/hscompile
cd hscompile
mkdir build
cd build
cmake -DHS_SOURCE_DIR=/path/to/hyperscan -DMNRL_SOURCE_DIR=/path/to/mnrl/C++ ..
make
```

For other informations, please refer to this guide: https://github.com/DavideConficconi/hscompile

## VASim installation guide

### Requirements
It requires g++-5, but it is not more available in recent Linux releases. 
The following steps allows to install g++-5:
```console
sudo vim /etc/apt/sources.list
```
Add the following 2 lines in the file and save it: 

```console
deb [trusted=yes]  http://dk.archive.ubuntu.com/ubuntu/ xenial main
deb [trusted=yes]  http://dk.archive.ubuntu.com/ubuntu/ xenial universe
```
After that:

```console
sudo apt update
sudo apt install g++-5
```


## Set a different compiler version as g++/gcc
```console
mv /usr/bin/gcc /user/bin/gccc
mv /usr/bin/g++ /usr/bin/g+++
ln -s /usr/bin/gcc-5 /usr/bin/gcc
ln -s /usr/bin/g++-5 /usr/bin/g++
```
### Install VASim
From the repository guide: https://github.com/jackwadden/VASim
```console
git clone https://github.com/jackwadden/VASim.git
cd VASim
make
```

## Credits and Contributors <a name="credits"></a> 

Contributors: Carloni Filippo, Conficconi Davide, Moschetto Ilaria, and Santambrogio Marco Domenico

If you find this repository useful, please use the following citation(s):

```
@inproceedings{yarb2023iscas,
  title={Yarb: a methodology to characterize regular expression matching on heterogeneous systems},
  author={Carloni, Filippo and Conficconi, Davide and Moschetto, Ilaria and Santambrogio, Marco D},
  booktitle={2023 IEEE International Symposium on Circuits and Systems (ISCAS)},
  pages={1--5},
  year={2023},
  organization={IEEE}
}

```
