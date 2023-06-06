import java.net.*;
import java.io.*;
import java.util.Scanner;

public class DIYAppProtocol extends Thread {

    private static final int SEND_DATA_SLICE = 0;
    private static final int RECEIVE_PARTIAL_SUM = 1;
    private int state = SEND_DATA_SLICE;

    private Socket socket = null;
    DIYAppDataSlicer dataSlicer;
    ThreadGroup workerGroup;

    private static volatile double aggregatedSum = 0.0;
    private static final Object lock = new Object();

    public DIYAppProtocol(ThreadGroup grp, Socket socket, DIYAppDataSlicer dataSlicer)
    {
        super(grp,"ServerThread");
        this.workerGroup = grp;
        this.socket = socket;
        this.dataSlicer = dataSlicer;
    }

    public void run() {

        System.out.println("\nConnection started with: " + socket.getInetAddress());

        try (
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                Scanner in = new Scanner(new InputStreamReader(socket.getInputStream()));
        ) {
            String inputLine, outputLine;

            // While the worker has not sent a termination message, process dataset
            while (!(inputLine = in.nextLine()).equals("Worker Terminated"))
            {
                if (state == SEND_DATA_SLICE)
                {
                    outputLine = dataSlicer.getDataSliceFromQueue();
                    out.println(outputLine);
                    state = RECEIVE_PARTIAL_SUM;
                }
                else if (state == RECEIVE_PARTIAL_SUM)
                {
                    addAggregatedSum(Double.parseDouble(inputLine));
                    System.out.println(getAggregatedSum());
                    state = SEND_DATA_SLICE;
                }
                else
                {
                    break;
                }
            }

            // If this thread is the last active thread, send termination signal to controller
            synchronized (lock)
            {
                try {
                    sleep(1000);
                    System.out.println("Connection ended with: " + socket.getInetAddress() + "\n");
                    if ((workerGroup.activeCount() - 1) == 0) {
                        DIYAppController.terminateController();
                    }
                    socket.close();
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Accessor/Mutator methods for aggregatedSum, used by Worker threads
    public static double getAggregatedSum()
    {
        synchronized (lock)
        {
            return aggregatedSum;

        }
    }

    public static void addAggregatedSum(double d)
    {
        synchronized (lock)
        {
            aggregatedSum = getAggregatedSum() + d;
        }
    }


}
