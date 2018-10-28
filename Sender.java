import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Sender
{
    Socket sender;      //socket is used to connect to machines (programs)
    ObjectOutputStream out;
    ObjectInputStream in;
    String packet,ack,str, msg;
    int len,i=0,sequence=0;
   // Sender(){}
    
    public void run()       //method to send data
    {
       
        try
        {
           BufferedReader br=new BufferedReader(new InputStreamReader(System.in));     
           System.out.println("Waiting for Connection....");
           sender = new Socket("localhost",1999);                   //port no. ie 1999 should be same for both sender and receiver in order to establish communication.
           sequence=0;
           out=new ObjectOutputStream(sender.getOutputStream());        //taking data to be sent
           out.flush();
           in=new ObjectInputStream(sender.getInputStream());               // creates an ObjectInputStream connected to the same file the ObjectOutputStream was connected to.
           str=(String)in.readObject();                                 //reads in an object from the ObjectInputStream and casts it to a String object.
           System.out.println("reciver ->    "+str);                    //prints the readed data  i.e. connected
           
           System.out.println("Enter the data to send....");
           
           packet=br.readLine();                                                //takes input string from user
           len=packet.length();
           do
           {
                try
                {

                    if(i<len)
                    {
                    msg=String.valueOf(sequence+" : ");               //gives i th letter from packet
                    msg=msg.concat(packet.substring(i,i+1));            //concates string and sequence no.
                    }
                    else if(i==len)
                    {
                        msg="end";
                        out.writeObject(msg);break;           //Termination message from sender
                    }
                    out.writeObject(msg);
                    sequence=(sequence==0)?1:0;
                    out.flush();
                    System.out.println("data sent -> "+msg);
                    ack=(String)in.readObject();
                    System.out.println("waiting for ack.....\n");
                    
                    if(ack.equals(String.valueOf(sequence)))
                    {
                        i++;
                        System.out.println("\t\t\t\t\t\t\t receiver -> "+" packet recieved. Ack  "+(sequence)+ "\n\n");
                    }
                    else
                    {    
                        System.out.println("\t\t\t\tTime out resending data....\n\n");
                        sequence=(sequence==0)?1:0;
                    }
                }
            catch(Exception e){System.out.println(e);}
            }
           while(i<len+1);
            
           System.out.println("\t\t\t\tAll data sent, exiting..");
        }
        catch(Exception e){System.out.println(e);}
        finally
        {
            try
            {
                in.close();                     //closing connection from sender
                out.close();                       //closing at receiver side
                sender.close();                 //closing the server
            }
            catch(Exception e){System.out.println(e);}
        }
    }
    public static void main(String args[])
    {
        String t;
        Scanner sc=new Scanner(System.in);
        Sender s=new Sender();                      //object of sender
        s.run();                                            //call to run      
    }
}