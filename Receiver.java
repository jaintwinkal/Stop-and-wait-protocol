import java.io.*;
import java.net.*;

public class Receiver
{
    ServerSocket reciever;
    Socket connection = null;
    ObjectOutputStream out;
    ObjectInputStream in;
    String packet, ack, data = "";
    int i = 0, sequence = 0;

    //Receiver() {}

    public void run()
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            reciever = new ServerSocket(1999, 10);              //backlog(10) requested maximum length of the queue of incoming connections.
            System.out.println("waiting for connection...");
            connection = reciever.accept();                             //receives connection of the same port (max. 10 here)
            sequence = 0;
            System.out.println("Connection established :");
            out = new ObjectOutputStream(connection.getOutputStream());         //the OutputStream is used for writing data to a destination.
            out.flush();
            in = new ObjectInputStream(connection.getInputStream());           //and The InputStream is used to read data from a source 
            out.writeObject("connected.");                                    //it prints at sender side

            do {
                try
                {
                    packet = (String) in.readObject();                          //reads data from sender side
                    if (Integer.valueOf(packet.substring(0, 1)) == sequence)
                    {
                        data += packet.substring(1);
                        sequence = (sequence == 0) ? 1 : 0;
                        System.out.println("\n\nreceiver ->      " + packet);
                    }
                    else
                    {
                        System.out.println("\n\nreceiver -> " + packet + "   \t\t\t\tduplicate data");
                    }
                    if (i < 3)
                    {
                        out.writeObject(String.valueOf(sequence));          //sends ack to sender
                        i++;
                    }
                    else
                    {
                        out.writeObject(String.valueOf((sequence + 1) % 2));
                        i = 0;
                    }
                }
                catch (Exception e) {}
            }
            while (!packet.equals("end"));
            
            System.out.println("Data recived=    " + data.replace(" : ",""));
            out.writeObject("connection ended.");
        }
        catch (Exception e) {}
        finally
        {
            try
            {
                in.close();             //closing the connection at receiver side
                out.close();            //closing at sender side
                reciever.close();       //closing the server
            }
            catch (Exception e) {}
        }
    }

    public static void main(String args[])
    {
        Receiver s = new Receiver();
        while (true)
        {
            s.run();
        }
    }
}
