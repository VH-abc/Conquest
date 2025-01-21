package util;

import Objects.Map;
import java.awt.Color;
import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

//************************************************************
// The GenericComm class contains all of the simple communication 
// methods wrapped up nicely to be used by servers and clients.  
// Methods include: 
//   public void sendMessage(String message)
//   public String getMessage()
//   public void closeNicely()
//   (also two constructors are provided for establishing connections.) 
//************************************************************

public class GenericComm
{   //Declare variables
//    private final String defaultIP = "192.168.1.254";
    private final String defaultIP = "10.17.21.80";
    //--Two things to keep track of who's talking--
    private int myNumber = 0;
    private String myTitle = "???";
    //--The communication objects--
    private PrintWriter out = null;
    private BufferedReader in = null;
    private Socket socket = null;
    public static boolean debugMode = false;
    private Color color = Color.GRAY;
    
    //--------------------------------------------------------
    //Constructors
    public GenericComm(Socket in_socket)
    {   //Establish connection via socket.
        socket = in_socket;
        createStreams(socket);
    }
    public Socket getSocket(){
        return socket;
    }
    public GenericComm() 
    {   //Don't know the socket details yet... need to connect.
        System.out.println("In Generic Comm - CS");
        socket = askUserForIPaddress();
        createStreams(socket);
        String clr = JOptionPane.showInputDialog
                         ("Please enter your color code", "");
        color = Map.unpackColor(clr);
    }
    //--------------------------------------------------------    
    // PUBLIC Methods
    //--------------------------------------------------------
    public Color getColor() {
        return color;
    } 
    public void sendMessage(String message)
    {
        out.println(message); //Send a message 
        debugMsg("<S<",message);
    }
    //--------------------------------------------------------
    public String getMessage()
    {
        String message = null;
        try
        {
           message = in.readLine();
        }
        catch (IOException e) 
        {   //This means the connection is severed! (in some cases at least)
            debugMsg("catch!","I/O exception occurred in G_Comm:getMessage()");
            closeNicely();
        }        
        if(message != null) debugMsg(">R>",message);
        else debugMsg(">R>", " null message recieved.");
        
        return message;
    }
    //------------------------------------------------------------
    public String getMostRecentMessage()
    {
        String message = null;
        try
        {
            while(in.ready())
                message = getMessage();
        } 
        catch (IOException e) 
        {   //This means the connection is severed! (in some cases at least)
            debugMsg("catch!","I/O exception occurred in G_Comm:getMostRecentMessage()");
            closeNicely();
        }      
        return message;
    }
    //------------------------------------------------------------
    public boolean messageWaiting()
    {
        boolean result = false;
        try
        {
            result = in.ready();
        } 
        catch (IOException e) 
        {   //This means the connection is severed! (in some cases at least)
            debugMsg("catch!","I/O exception occurred in G_Comm:messageWaiting()");
            closeNicely();
        }   
        return result;
    }
    //------------------------------------------------------------
    public void closeNicely()
    {
        try
        {   //Close things up nicely when you are done.
            out.close();
            in.close();
            socket.close();
        }
        catch (IOException e) 
        {
            debugMsg("catch!","I/O exception occurred in G_Comm:closeNicely()");
            System.exit(1);
        }                
    }
    //------------------------------------------------------------
    // PRIVATE Methods
    //------------------------------------------------------------
    private void createStreams(Socket socket)
    {   //Establish connection via socket.
        try 
        {
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } 
        catch (IOException e) 
        {
            debugMsg("catch!","I/O exception occurred in G_Comm:createStreams");
            System.exit(1);
        }        
    }
    //------------------------------------------------------------
    private Socket askUserForIPaddress()
    {
        Socket theSocket = null;
        String serverName = JOptionPane.showInputDialog
                         ("Please enter the Server IP address", defaultIP);
        try 
        {
            theSocket = new Socket(serverName, 4444);
        } 
        catch (UnknownHostException e) 
        {
            debugMsg("catch!","Don't know about host: "+serverName+" in G_Comm:askUserForIPaddress()");
            System.exit(1);
        } 
        catch (IOException e) 
        {
            debugMsg("catch!","I/O exception occurred in G_Comm:askUserForIPaddress()");
            System.exit(1);
        }        
        return theSocket;
    }
    
    //===========================================================
    // These two methods are used for debugging the communication.
    //===========================================================
    public void setDebugValues(String title, int num)
    {
        myNumber = num;
        myTitle = title;
    }
    
    public void debugMsg(String type, String text)
    {   //Displays a debug message in the output window.  
        if(debugMode)
            System.out.println(type+myTitle+"("+myNumber+"):  "+text);
    }
    //===========================================================

} //--end of GenericComm class--
