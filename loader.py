#!/usr/bin/python
#use
# sudo python3 python_version/loader.py ./tmp/design_1_wrapper.bit 100 ./tmp/instr0.txt ./tmp/dati0.txt

def printDebug(str, debug):
    if debug == True:
        print(str)

def printDebugCSRInstrDebug(core, debug):
    printDebug("********************Printing CSR Instr debug**********************\n\n", debug)
    #printDebug("Leggo i primi 32 bit dei dati: ", platform->readJamRegInd(0))
    printDebug("Leggo la curr reference: "+ str(core.s00_axi.read(0x1<<2)), debug)
    #printDebug("Leggo no more characters: ", platform->readJamRegInd(2) )
    printDebug("Leggo reset: " + str(core.s00_axi.read(0x3<<2)), debug)
    #printDebug("Leggo curr_character: " , platform->readJamRegInd(4) )
    printDebug("Leggo contenuto reg 6, addr start instr: " + str(core.s00_axi.read(0x5<<2)), debug)
    printDebug("Leggo clock counter: " + str(core.s00_axi.read(0x6<<2)), debug)
    printDebug("Leggo reg7, rst|cambia bram addr| abilita il reload?|we opcode | we data|clock counter and src_en enabled: " , core.s00_axi.read(0x7<<2), debug)
    #printDebug("Leggo curr_character_check: " , platform->readJamRegInd(8) )
    printDebug("Leggo control vector: no more chars| found | complete" + str(core.s00_axi.read(0x9<<2)), debug)
    printDebug("Leggo control path state: "+  str(core.s00_axi.read(0x10<<2)), debug)
    printDebug("Leggo debug_curr_opc: "+  str(core.s00_axi.read(0x11<<2)), debug)
    printDebug("Leggo debug_instr_addr_1: "+  str(core.s00_axi.read(0x12<<2)), debug)
    printDebug("Leggo debug_instr_addr_2: "+  str(core.s00_axi.read(0x13<<2)), debug)
    printDebug("Leggo debug_instr_addr_3: "+  str(core.s00_axi.read(0x14<<2)), debug)
    #printDebug("Leggo debug_curr_data (31-0): ",  platform->readJamRegInd(15))
    printDebug("Leggo 31-0 bit of ram out 1: "+  str(core.s00_axi.read(0x16<<2)), debug)
    printDebug("Leggo 38-32 bit of ram out 1: "+  str(core.s00_axi.read(0x17<<2)), debug)
    printDebug("Leggo 31-0 bit of ram out 2: "+  str(core.s00_axi.read(0x18<<2)), debug)
    printDebug("Leggo 38-32 bit of ram out 1: "+  str(core.s00_axi.read(0x19<<2)), debug)
    printDebug("Leggo 31-0 bit of ram out 3:  "+  str(core.s00_axi.read(0x20<<2)), debug)
    printDebug( "Leggo 38-32 bit of ram out 1: "+ str(core.s00_axi.read(0x21<<2)), debug)
    printDebug("\n********************EoF CSR**********************\n", debug)

def printDebugCSRThreadSafe(core, debug):
    printDebug("********************printDebuging CSR**********************\n\n", debug)
    printDebug("Leggo registro 0: "+ str(core.s00_axi.read(0x0<<2)), debug)
    printDebug("Leggo registro 1: "+ str(core.s00_axi.read(0x1<<2)), debug)
    printDebug("Leggo registro 2: "+ str(core.s00_axi.read(0x2<<2)), debug)
    printDebug("Leggo registro 3: " + str(core.s00_axi.read(0x3<<2)), debug)
    printDebug("Leggo registro 4: " + str(core.s00_axi.read(0x4<<2)), debug)
    printDebug("Leggo registro 5: " + str(core.s00_axi.read(0x5<<2)), debug)
    printDebug("Leggo registro 6: " + str(core.s00_axi.read(0x6<<2)), debug)
    printDebug("Leggo registro 7: " + str(core.s00_axi.read(0x7<<2)), debug)
    printDebug("Leggo registro 8: " + str(core.s00_axi.read(0x8<<2)), debug)
    printDebug("Leggo registro 9: " + str(core.s00_axi.read(0x9<<2)), debug)
    printDebug("Leggo registro 10: "+ str(core.s00_axi.read(0xa<<2)), debug)
    printDebug("Leggo registro 11: "+ str(core.s00_axi.read(0xb<<2)), debug)
    printDebug("Leggo registro 12: "+ str(core.s00_axi.read(0xc<<2)), debug)
    printDebug("Leggo registro 13: "+ str(core.s00_axi.read(0xd<<2)), debug)
    printDebug("Leggo registro 14: "+ str(core.s00_axi.read(0xe<<2)), debug)
    printDebug("Leggo registro 15: "+ str(core.s00_axi.read(0xf<<2)), debug)
    printDebug("Leggo registro 16: "+ str(hex(core.s00_axi.read(0x10<<2))), debug)
    printDebug("Leggo registro 17: "+ str(hex(core.s00_axi.read(0x11<<2))), debug)
    printDebug("Leggo registro 18: "+ str(core.s00_axi.read(0x12<<2)), debug)
    printDebug("Leggo registro 19: "+ str(core.s00_axi.read(0x13<<2)), debug)
    printDebug("Leggo registro 20: "+ str(core.s00_axi.read(0x14<<2)), debug)
    printDebug( "Leggo registro 21: "+ str(core.s00_axi.read(0x15<<2)), debug)
    printDebug("\n********************EoF CSR**********************\n", debug)

INSTR_LINE_LENGTH = 32
MHZ = 1000000
OVERLAPPING = 100
import numpy as np
import sys
import time
import os
from pynq.ps import Clocks
from pynq import Overlay
debug = False
# actually used /home/xilinx/alveare/tmp/design_1_wrapper.bit"
overlay = Overlay(sys.argv[1])
overlay.download()
#set the frequency with the second argument
printDebug("Old frequency" + str(Clocks.fclk0_mhz), debug)
Clocks.fclk0_mhz = float(sys.argv[2])
printDebug("New frequency" + str(Clocks.fclk0_mhz), debug)
number_of_cores = int(sys.argv[7])
core = []
for i in range(number_of_cores):
    core.append(getattr(overlay, "tirex_core_ip_v1_0_" + str(i)))
chunk_f = open(sys.argv[4] + "/chunk.txt", 'r')
for line in chunk_f:
    chunks_num = int(line[0:len(line)-1])
n_execution = 0
print("core num " + str(number_of_cores))
print("chunk num " + str(chunks_num))
chunk_f.close()
count_regex = 0
re_f = open(sys.argv[5], 'r')
for line in re_f:
    if len(line) > 0:
        count_regex = count_regex + 1
re_f.close()
print("re num" + str(count_regex))
init_data_n = 0
while n_execution < count_regex:
    output_f = open(sys.argv[6] + "/output.txt", 'a')
    time_f = open(sys.argv[6] + "/time.txt", 'a')
    print("regex n:" + str(n_execution))
    #read instructions from file
    instructions = []
    f = open(sys.argv[3]+"/instr"+str(n_execution)+".txt", 'r')
    for line in f:
        line2 = f.readline()
        instr_tuple = (int(line[0:INSTR_LINE_LENGTH], 2), int(line2[0:INSTR_LINE_LENGTH], 2))
        instructions.append(instr_tuple)
        printDebug(instr_tuple, debug)
    printDebug("Number of instructions" + str(len(instructions)), debug)
    avgs = []
    match_count = 0
    for chunk in range(0, chunks_num):
        match = 0
        #read data from file
        char_counter = 0
        f = open(sys.argv[4] + "/data" + str(chunk) + ".txt", 'r')
        for line in f:
            for character in line:
                int_content = ord(character)
                hex_content = int(int_content)
                printDebug(hex_content, debug)
                char_counter = char_counter + 1
        f.close()
        # Size of data in input that has to be divided in the various cores
        printDebug("Data characters: "+ str(char_counter), debug)

        #
        #        The following constants define:
        #            - The starting address of the instructions
        #            - The starting character of the data in the BRAM
        #            - The ending character in the BRAM
        #
        instr_start_addr = 0
        data_start_addr  = 0
        tirex_cores_base_addr = []
        tirex_cores_base_addr = [0 for i in range(number_of_cores)] 

        #
        #        Control Vector that holds all the control bits needed by the core
        #
        #        VALUE       MEANING
        #        0x0         Core disabled
        #        0x1         Search of the core but core disabled
        #        0x2         Write the Instruction reference data
        #        0x4         Write the Instruction opcode
        #        0x8         Enable the core
        #        0x9         Enable the core and the search
        #        0x10        Enable BRAM debug mode
        #        0x20        Reset the system
        #

        control = 0

        # Data in AXI register 7 that holds the results of the computation
        result = []
        result = [0 for i in range(number_of_cores)]

        # Data in AXI register 8 that holds the character index of the matching string
        char_num = []
        char_num = [0 for i in range(number_of_cores)]

        # These variable are filled when the computation terminates and are needed
        # to display the results
        found = []
        found = [0 for i in range(number_of_cores)]

        timing_counter = []
        timing_counter = [0 for i in range(number_of_cores)]

        #
        #        This array holds the instructions to be loaded into the
        #        instruction memory. The instruction are organized as follows:
        #            - first dimension   : reference data
        #           - second dimension  : opcode
        #
        #        OPCODE      MEANING
        #        0x7         JMP
        #        0x8         OR
        #        0x9         OR )*
        #        0xa         OR )+
        #        0xb         OR )|
        #        0xc         OR )
        #        0x10        AND
        #        0x11        AND )*
        #        0x12        AND )+
        #        0x13        AND )|
        #        0x14        AND )
        #        0x20        (
        #
        #       The array must be filled with the instruction provided by the compiler in
        #        order to test the regular expression

        # Write enable for Manual Insertion of data in the BRAM
        data_we = 0

        # Address of the data that has to be written
        data_address = 0

        # Instruction address needed to load instruction in the Instruction
        # Memory of the core
        instr_address = 0
        # Resetting the system
        printDebug("Core reset", debug)
        control = 0x20
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x7<<2, control)
        # printDebug("Initializing Instruction RAM of the core", debug)
        # control = 0x0
        # core.s00_axi.write(0x7<<2, control)

        #Fill the instruction RAM of the core
        i = 0
        while i < len(instructions):
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x0<<2, instr_address)
            printDebug("Debug after start address\n", debug)
            #printDebugCSRInstrDebug(core[0])
            printDebugCSRThreadSafe(core[0], debug)
            control = 0x2
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x7<<2, control)
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x1<<2,instructions[i][0])
            printDebug(hex(instructions[i][0]), debug)
            printDebug("Debug after first part of instr\n", debug)
            #printDebugCSRInstrDebug(core[0])
            printDebugCSRThreadSafe(core[0], debug)
            control = 0x4
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x7<<2, control)
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x1<<2,instructions[i][1])
            printDebug(hex(instructions[i][1]), debug)
            printDebug("Debug after second part of instr\n", debug)
            #printDebugCSRInstrDebug(core[0])
            printDebugCSRThreadSafe(core[0], debug)
            instr_address = instr_address + 1
            i = i + 1
        control = 0x0
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x7<<2, control)
        init_data_n = 0
        if char_counter > 0:
            i = 0
            init_data_n = int(char_counter / number_of_cores) + OVERLAPPING
            printDebug("Initializing Data RAM of the core...", debug)
            data_we = 1
            data_to_init = init_data_n + 150
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x2<<2, data_we)
            while i < data_to_init:
                for core_n in range(number_of_cores):
                    core[core_n].s00_axi.write(0x8<<2,data_address)
                for core_n in range(number_of_cores):
                    core[core_n].s00_axi.write(0x9<<2,0)
                data_address = data_address + 1
                i = i + 1
            data_we = 0
            for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x2<<2,data_we)
            printDebugCSRThreadSafe(core[0], debug)
            batch_size = int(char_counter / number_of_cores / 4 + OVERLAPPING)
            printDebug("Batch size for each core ---> "+ str(batch_size), debug)

            f = open(sys.argv[4] + "/data" + str(chunk) + ".txt", 'r')
            core_data = 0
            data_counter = 0
            while core_data < number_of_cores:
                data_address = 0
                data_we = 1
                core[core_data].s00_axi.write(0x2<<2,data_we)
                j = 0
                while j < batch_size:
                    data = f.read(4)
                    if data == '':
                        break
                    x = 0
                    out_data = 0
                    while x < len(data):
                        out_data = ord(data[x]) + (out_data << (8))
                        x = x + 1
                    core[core_data].s00_axi.write(0x8<<2,data_address)
                    core[core_data].s00_axi.write(0x9<<2,out_data)
                    printDebug("Data loaded at address"+ str(data_address)+ ", of core"+ str(core_data)+ ", of --->"+ hex(out_data), debug)
                    data_address = data_address + 1
                    j = j + 1
                    data_counter = data_counter + 4
                f.seek(data_counter - OVERLAPPING)
                core_data = core_data + 1
            f.close()
        printDebugCSRThreadSafe(core[0], debug)
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x9<<2,0)
        control = 0x0
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x7<<2,control)

        # Set up of the core for the search
        # Polling in order to check the completion of the core computation

        allCompleted = 0
        printDebugCSRThreadSafe(core[0], debug)
        arrayCompleted = []
        arrayCompleted = [False for i in range(number_of_cores)]

        repetitions = 0
        data_end_addr = init_data_n + 128 if init_data_n != 0 else 16000
        printDebug("Data end address"+ str(data_end_addr), debug)
        printDebug("Setting up the core for the search...", debug)
        control = 0x8
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x7<<2,control)
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x4<<2,data_start_addr)
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x5<<2,data_end_addr)
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x6<<2,instr_start_addr)


        printDebug("Core ready for the search...", debug)
        printDebugCSRThreadSafe(core[0], debug)
        # #Enable the search
        control = 0x9
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x7<<2,control)
            
        printDebugCSRThreadSafe(core[0], debug)
        while allCompleted < number_of_cores and repetitions < 10:
            printDebugCSRThreadSafe(core[0], debug)
            i = 0
            while i < number_of_cores:
                    if not arrayCompleted[i]:
                        result[i] = core[i].s00_axi.read(0x9<<2)
                        printDebug("Clock_cycles core "+ str(i) + str(core[i].s00_axi.read(0x6<<2))+ " Check current character: "+ str(core[i].s00_axi.read(0x8<<2)), debug)
                        if result[i] & (1 << 0) != 0:
                            allCompleted = allCompleted + 1
                            arrayCompleted[i] = True
                    i = i + 1
            repetitions = repetitions + 1
        printDebug("number of cores that have completed -----> " + str(allCompleted), debug)

        # # Counter measurement retrevial
        i = 0
        while i < number_of_cores:
            timing_counter[i] = core[i].s00_axi.read(0x6<<2)
            i = i + 1

        # # Disabling the cores
        control = 0x0
        for core_n in range(number_of_cores):
            core[core_n].s00_axi.write(0x7<<2,control)

        i = 0
        while i < number_of_cores:
            #print(str(timing_counter[i]))
            printDebug("Core "+ str(i) + "has completed the search in: "+ str(timing_counter[i])+ " clock cycles", debug)
            if result[i] & (1 << 1) != 0:
                found[i] = 1
                match = 1
            else:
                found[i] = 0
            printDebug("Core "+ str(i) + "  has completed the search ----->\n\t complete = "+ str(result[i] & (1 << 0))+ "\n\n\t found = "+ str(found[i] & (1 << 1))+ "\n\n", debug)
            i = i + 1

        # Last match retrevial
        i = 0
        while i < number_of_cores:
            if found[i] == 1:
                char_num[i] = core[i].s00_axi.read(0x8<<2)
                printDebug("Last match character index of core "+ str(i) + " ------> "+ str(char_num[i])+ "\n\n", debug)
            i = i + 1
        if match == 1:
            match_count = match_count + 1
        printDebugCSRThreadSafe(core[0], debug)
        # Resetting the core
        control = 0x20
        for core_n in range(number_of_cores):
                core[core_n].s00_axi.write(0x7<<2,control)
        #     deinitPlatform(platform)
        avgs.append(np.amax(timing_counter)/(MHZ * int(sys.argv[2])))
    logs = np.log(avgs)
    time_f.write('{:.20f}'.format(np.average(avgs)) + ' ')
    time_f.write('{:.20f}'.format(np.exp(logs.mean())) + '\n')
    output_f.write(str(match_count) + '\n')
    n_execution = n_execution + 1
    print(match_count)
    time_f.close()
    output_f.close()
    print("data for each chunk: " + str(init_data_n))
print("end")
