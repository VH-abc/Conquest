package server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.*;
import java.io.*;
import javax.swing.JFrame;

public class SS_Main //The Server-side main (MULTI_SERVER!)
{
    
    public static void main(String[] args) throws IOException 
    {      
        SS_Main main = new SS_Main();
    }
    
    int FPS = 30;   //Frames per second (animation speed)
    SS_GameEngine theEngine;
    JFrame myFrame;
//    ArrayList<SS_Thread> threads = new ArrayList<SS_Thread>();

    public SS_Main() throws IOException
    {
        System.out.println("Beginning of SS_Main");
        initGameEngine();
        System.out.println("SS_GameEngine initialized");
        acceptClients();
        System.out.println("End of SS_Main");
    }
    
    public void initGameEngine()
    {
        theEngine = new SS_GameEngine();
        myFrame = new JFrame();
        myFrame.addWindowListener(new SS_Main.Closer());
        addFrameComponents();
        startAnimation();
        myFrame.setSize(theEngine.getPreferredSize());
        myFrame.setVisible(true);
    }
    
    public void acceptClients() throws IOException
    {
        ServerSocket sendSocket = null;
        boolean listening = true;

        //Declare and establish the Server Socket...
        //-----------------------------------------
        try  
        {
            sendSocket = new ServerSocket(4444);
        } 
        catch (IOException e) 
        {
            System.err.println("Could not listen on port: 4444. or 4445.?");
            System.exit(-1);
        }
//        System.out.println("Channel: " + sendSocket.getChannel());
//        System.out.println("toString: " + sendSocket.toString() );
        //Create a new thread whenever someone tries to connect...
        //-----------------------------------------
        while (listening)
        {
          System.out.println("Listening for new clients.");
          new SS_Thread(sendSocket.accept(), theEngine).start();
         
        }

        //Clean things up by closing socket.
        //-----------------------------------------------
        sendSocket.close();
    }
    
    public void addFrameComponents() 
    {
        myFrame.setTitle(theEngine.getMyName() + " - " + theEngine.getPreferredSize().width+"x"+theEngine.getPreferredSize().height);
        myFrame.add(theEngine);
    }
    
    public void startAnimation() 
    {
        javax.swing.Timer t = new javax.swing.Timer(1000/FPS, new ActionListener() 
        {   //This is something you may not have seen before...
            //We are coding a method within the ActionListener object during it's construction!
            public void actionPerformed(ActionEvent e) 
            {
                myFrame.getComponent(0).repaint();
                myFrame.setSize(myFrame.getComponent(0).getPreferredSize());
            }
        }); //--end of construction of Timer--
        t.start();
    }    
    
    private static class Closer extends java.awt.event.WindowAdapter 
    {   
        public void windowClosing (java.awt.event.WindowEvent e) 
        {   System.exit (0);
        }   //======================
    }      

}


