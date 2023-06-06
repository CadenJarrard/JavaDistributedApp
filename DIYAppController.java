/*
    DIYAppController.java
    DIYAppDataSlicer.java
    DIYAppProtocol.java

    Author: Caden Jarrard
    Date:   11/15/22

    This distributed application uses concurrent threading to find
    the total sum of the numbers in a dataset. To run this program, compile it,
    then type the following in a terminal:
            Usage: java DIYAppController <port number> <file name>
 */

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class DIYAppController {

    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java DIYAppController <port number> <file name>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);
        String fileName = (args[1]);    // File containing dataset

        // Worker group for threads
        ThreadGroup thrGroup = new ThreadGroup("workers");

        // Access the dataset for processing
        File dataset = new File(fileName);
        Scanner datasetSC = new Scanner(dataset);

        // Start the data slicing thread
        DIYAppDataSlicer dataSlicer = new DIYAppDataSlicer(datasetSC);
        dataSlicer.start();

        // Create server socket and listen for incoming connections
        try (ServerSocket serverSocket = new ServerSocket(portNumber))
        {
            System.out.println("Server listening for incoming connections on port# " + portNumber);
            while (true)
            {
                 new DIYAppProtocol(thrGroup, serverSocket.accept(), dataSlicer).start();

            }
        } catch (IOException e)
        {
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    public static void terminateController()
    {
        System.out.println("The total sum is: " + DIYAppProtocol.getAggregatedSum());
        System.exit(0);
    }

}