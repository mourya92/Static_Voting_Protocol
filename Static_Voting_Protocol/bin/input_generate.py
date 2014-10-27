import random
import math

maxMsg = raw_input('Enter Total Operations: ')
print "Max Messages: {}".format(maxMsg)

maxNodes = raw_input('Enter Max Nodes: ')
print "Max Nodes: {}".format(maxNodes)

readPrcnt = raw_input('Enter Percentage of read Operations: ')
print "Read Percentage is {}".format(readPrcnt)

'''writePrcnt = raw_input('Enter Percentage of write Operations: ')
print "write Percentage is {}".format(writePrcnt)
'''
TotRead=math.ceil(float(maxMsg)*float(readPrcnt)/100)
TotWrite= int(maxMsg)-int(TotRead)

print "Total Reads are: %d" %TotRead
print "Total writes are:%d"%TotWrite

print "-----------GENERATING FILE SYSTEM-------------------"

for i in range(1,int(maxNodes)+1):
	temp=i
	if i<10:
		fp = open("net0"+str(temp)+".txt", "w")
	else:
		fp = open("net"+str(temp)+".txt", "w")
	fp.write('DEFAULT DATA')
	fp.close

print " -------- COMPLETED FILE SYSTEM GENERATION -------- "

input_List=['read','write']

print "-----------GENERATING INPUT FILE-------------------"
TotRead_temp= int(TotRead)
TotWrite_temp= int(TotWrite)

for k in range(1,int(maxNodes)+1):
	temp=k
        if k<10:
                fp = open("net0"+str(temp)+"_input.txt", "w")
        else:
                fp = open("net"+str(temp)+"_input.txt", "w")
	for j in range(1,int(maxMsg)+1):
		random_item = random.choice(input_List)
		if random_item is 'read':
			TotRead_temp-=1
			if TotRead_temp>0:
				fp.write('read\n')
			else: 
				fp.write('write\n')
		else:
			TotWrite_temp-=1
			if TotWrite_temp>0:
				fp.write('write\n')
			else:
				fp.write('read\n')
	j=1
	TotRead_temp= TotRead
	TotWrite_temp= TotWrite
	fp.close

print " -------- COMPLETED INPUT FILE GENERATION -------- "
