/*
    DIYAppWorker.java

    Author: Caden Jarrard
    Date:   11/15/22

    This program is a TCP client that connects to a remote controller to calculate
    the sum of the numbers in a data slice from a larger dataset. To run this program,
    compile it, then type the following in a terminal:
            Usage: java DIYAppWorker <controller ip address> <port#>
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class DIYAppWorker {

    private static final int REQUEST_DATA_SLICE = 0;
    private static final int SEND_PARTIAL_SUM = 1;

    public static void main(String[] args) throws IOException, InterruptedException {

        int workerState = REQUEST_DATA_SLICE;
        double partialSum = 0.0;

        // Usage Checking
        if (args.length != 2) {
            System.err.println(
                    "Usage: java DIYAppWorker <controller ip address> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                Scanner in = new Scanner(
                        new InputStreamReader(clientSocket.getInputStream()));
        ) {
            String fromServerDS = "";
            boolean initate_contact = true;
            while (!fromServerDS.equals("") || initate_contact)
            {
                initate_contact = false;
                if (workerState == REQUEST_DATA_SLICE) // Request data slice from controller
                {
                    out.println("Idling...Send data slice");
                    fromServerDS = in.nextLine();
                    workerState = SEND_PARTIAL_SUM;
                }
                else if (workerState == SEND_PARTIAL_SUM) // Calculate partial sum and send to controller
                {
                    partialSum = 0.0;
                    String[] data = fromServerDS.split(","); // Split data slice into individual numbers
                    for (String num : data) // Calculate partial sum of data slice
                    {
                        partialSum += Double.parseDouble(num);
                    }
                    out.println(String.valueOf(partialSum)); // Send partial sum back to controller
                    workerState = REQUEST_DATA_SLICE;
                }
            }

            // Worker finished, send termination message to controller
            out.println("Worker Terminated");

        } catch (UnknownHostException e)
        {
            System.err.println("Unknown Hostname " + hostName);
            System.exit(1);
        } catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
