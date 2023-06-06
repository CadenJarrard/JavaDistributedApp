This package contains a Distributed Application controller/worker program.
The TCP controller is concurrent, and one or more TCP workers can be used in
processing the total sum of the given dataset.

After unzipping the package into a folder (directory), 
you should see these files:

	DIYAppController.java     readme.txt
	DIYAppDataSlicer.java   
	DIYAppProtocol.java
	DIYAppWorker.java


Code Design:

	DIYAppController.java:	The controller of the distributed application,
					creates a server socket and waits for 
					incoming connections for workers. Creates
					a new thread for each worker connection. 
					Outputs the total sum of the dataset
					once processing is complete.

	DIYAppDataSlicer.java:	With its own thread, the DIYAppDataSlicer class scans
					through the dataset, producing data slices of 
					up to fifty numbers. The data slices are then
					stored in a Blocking Queue to be retrieved by
					the DIYAppProtocol class.

	DIYAppProtocol.java:	The DIYAppProtocol handles communication of data slices
					and data slice sums between the controller and 
					workers. Once processing of the dataset is complete,
					workers send a termination message after receiving an
					empty dataslice, and finally, the controller itself
					is terminated.

	DIYAppWorker.java:	DIYAppWorker is responsible for the calculation of the
					data slice sums, and communicating them back to
					the controller to contribute to the total sum of
					the dataset. If the worker(s) receives an empty
					dataslice, then processing of the dataset is complete,
					and the worker(s) terminate.


Application Message Protocol:

	Messages: 	The flow of messages between the controller and
			the workers is as follows: 

			1. The Worker sends an initial idling message to the
				controller to begin the process...
			2. Controller responds by sending a dataslice
				to the Worker...
			3. The Worker calculates the sum of dataslice and sends
				it back to the Controller...
			4. Controller adds sum of dataslice received from worker
				to the total sum of the dataset...
			5. At this point, if the dataset has been completely
				processed, the worker(s) will send a termination
				message to the controller, else the process repeats 
				beginning at step 1.

	Messages'	All messages sent between the Controller and workers,
	Format: 		including the idling message, dataslice,
				dataslice sum, and termination messages,
				are sent in a text format.

	Worker		When the worker receives an empty string from the
	Termination:		controller in place of the requested dataslice,
				the worker will terminate.
	
	Controller	When the end of dataset has been reached, and
	Termination:		all workers have already terminated,
				the controller is sent a termination
				signal.



Running the Program: 

1. Compile the code

Open a terminal window, change working folder (directory) to 
where the code is.

You can compile the package manually by typing:

	javac DIYAppController.java DIYAppWorker.java

What the compilation command do is to create two programs:

	DIYAppController # TCP thread-based concurrent controller
	DIYAppWorker	 # TCP worker that connects to the controller

2. Run the code

Ideally you run the program on one controller machine and run the workers
on separate machines. However you can run both the controller and
the workers on the same machine as well (thanks to multitasking).

2.1 On two different machines

On one machine, open a terminal window and change working 
folder (directory) to where the code is.

Run the thread-based controller with the following command:

	java DIYAppController <port#> <filename>

This starts the controller on local machine at the specified port #.

On the other worker machines, open a terminal window and change working 
folder (directory) to where the code is.

Run the workers with the following command:

	java DIYAppWorker <controller's ip address> <port#>

This starts the worker to call the remote controller at the specified port #.

After the dataset has been processed by one or more workers, the total sum of
the dataset will be printed to the controller's standard output.

2.2 On the same machine

Open two terminal windows and change working folders
to where the code is.

Run the thread-based controller such as:

	java DIYAppController <port#> <filename>

This starts the controller on local machine at the specified port #.

Run the worker in the other terminal window such as:

	java DIYAppWorker localhost <port#>

This starts the worker to call the controller running on local
machine at the specified port #. The "localhost" is a special name 
that refers to the local machine.

After the dataset has been processed by one or more workers, the total sum of
the dataset will be printed to the controller's standard output.