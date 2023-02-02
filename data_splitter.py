#!/usr/bin/python
import sys

data_f = open(sys.argv[1], 'rb')
chunk_size = sys.argv[2]
data_dir = sys.argv[3]
data_overlap = 100 #sys.argv[4]
chunk = 0
data_chunk = data_f.read(int(chunk_size))
data_chunk_length = len(data_chunk)
print("cane e gatto "+ chunk_size)
while data_chunk_length > 0:
        chunk_f = open(data_dir + "/data" + str(chunk) + ".dat", 'wb')
        chunk_f.write(data_chunk)
        chunk_f.close()
        back_jump = data_f.tell() - int(data_overlap)
        if back_jump < 0:
                back_jump = 0
        data_f.seek(back_jump)
        chunk = chunk + 1
        data_chunk = data_f.read(int(chunk_size))
        data_chunk_length = len(data_chunk) - int(data_overlap)
        
data_f.close()

chunk_f = open(data_dir + "/chunk.txt", 'w')
chunk_f.write(str(chunk) + "\n")
chunk_f.close()