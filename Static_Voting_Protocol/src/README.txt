README FILE: 

> Consists of following files: 
	- config.txt - Contains data like Total Votes , Write Quorum size, Read Quorum size
	- MyVotes.txt - Contains data regarding votes contained by each node
	- Application.java - Application layer of application
	- ThreadContainer.java - Control layer of application
	- Sender.java - Maintains protocol for the application
	- input_generate.py - Generates the input files from input. 
	- Code_Tester.java - Tests the concurrent operations took place in the distributed system.
				It takes a command line argument to test each log file. 
				Example: To test concurrency of operation in log file 1, then give: 
					      java Code_Tester 1

> Steps to run the application: 
	- Run the python script
		python input_generate.py
	- Compile the application
		javac Applicationjava\
		javac Sender.java
		javac ThreadContainer.java
		javac Code_Tester.java
	- Run the application
		java Application
	- After all the nodes stopped generating print statements on console
	- Run the Code_Tester.java to test the code. 
		java Code_Tester.java
