import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.Scanner;
import java.util.StringJoiner;

public class DIYAppDataSlicer extends Thread
{
    Scanner datasetSC = null;
    BlockingQueue<String> bQueue = new ArrayBlockingQueue<String>(20);

    public DIYAppDataSlicer(Scanner datasetSC)
    {
        super("DataSlicer");
        this.datasetSC = datasetSC;
    }

    public void run()
    {

        try
        {
            String dataslice = getDataSliceFromFile();

            // Retrieve and Store dataslice into bQueue
            // When the EOF is reached, bQueue will be repeatedly put with empty strings to terminate workers
            while(true)
            {
                bQueue.put(dataslice);
                dataslice = getDataSliceFromFile();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Used internally, retrieves a dataslice from the file
    public String getDataSliceFromFile()
    {
        String num = "";

        // Create a data slice containing up to 50 numbers from the dataset
        StringJoiner dataslice = new StringJoiner(",");
        for (int i = 0; i < 50; i++)
        {
            if (datasetSC.hasNext() && (num = datasetSC.nextLine()) != null)
            {
                dataslice.add(num);
            }
            else {
                break;
            }
        }
        return dataslice.toString();
    }

    // Used by the controller thread, retrieves a dataslice from queue
    public synchronized String getDataSliceFromQueue()
    {
        try
        {
            return bQueue.take();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
