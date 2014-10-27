import random
import math

maxMsg = raw_input('Enter Total Operations: ')
print "Max Messages: {}".format(maxMsg)

maxNodes = raw_input('Enter Max Nodes: ')
print "Max Nodes: {}".format(maxNodes)

readPrcnt = raw_input('Enter Percentage of read Operations: ')
print "Read Percentage is {}".format(readPrcnt)

TotFiles= raw_input('Enter Total Number of Files in File System: ')
print "Total Files is {}".format(TotFiles)

TotRead=math.ceil(float(maxMsg)*float(readPrcnt)/100)
TotWrite= int(maxMsg)-int(TotRead)

print "Total Reads are: %d" %TotRead
print "Total writes are:%d"%TotWrite

print "-----------GENERATING FILE SYSTEM-------------------"

for i in range(1,int(maxNodes)+1):
	temp=i
	for z in range(1,int(TotFiles)+1):
		if i<10:
			fp = open("net0"+str(temp)+"_"+str(z)+".txt", "w")
		else:
			fp = open("net"+str(temp)+"_"+str(z)+".txt", "w")
		fp.close

fp_1 = open("Log_File.txt","w+")
fp_1.close

file_list=range(1,int(TotFiles)+1)

for z in range(1,int(TotFiles)+1):
	fp_1=open("Log_File_"+str(z)+".txt", "w")
	fp_1.close

print " -------- COMPLETED FILE SYSTEM GENERATION -------- "
print file_list
input_List=['read','write']

print "-----------GENERATING INPUT FILE-------------------"
TotRead_temp= int(TotRead)
TotWrite_temp= int(TotWrite)

ID=1

for k in range(1,int(maxNodes)+1):
	temp=k
        if k<10:
                fp = open("net0"+str(temp)+"_input.txt", "w")
        else:
                fp = open("net"+str(temp)+"_input.txt", "w")
	for j in range(1,int(maxMsg)+1):
		random_item = random.choice(input_List)
		random_file = random.choice(file_list)
		if random_item is 'read':
			TotRead_temp-=1
			if TotRead_temp>=0:
				fp.write(str(ID)+':read'+':'+str(random_file)+'\n')
			else: 
				fp.write(str(ID)+':write'+':'+str(random_file)+'\n')
		else:
			TotWrite_temp-=1
			if TotWrite_temp>=0:
				fp.write(str(ID)+':write'+':'+str(random_file)+'\n')
			else:
				fp.write(str(ID)+':read'+':'+str(random_file)+'\n')
		ID+=1
	j=1
	TotRead_temp= TotRead
	TotWrite_temp= TotWrite
	fp.close

ID=1;
print " -------- COMPLETED INPUT FILE GENERATION -------- "
