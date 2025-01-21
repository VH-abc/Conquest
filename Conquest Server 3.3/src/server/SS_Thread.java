package server;

import Objects.Map;
import java.awt.Color;
import util.GenericComm;
import java.net.*;
import java.util.ArrayList;

//=========================================================================
//THIS IS THE SERVER-SIDE 'LISTENER' THREAD...
// A seperate instance of this class is created for each user's connection.
// Static methods in this class can run and organize the game.  
//=========================================================================

public class SS_Thread extends Thread 
{
    //--static class variables--
    public static ArrayList<SS_Thread> connections = new ArrayList<SS_Thread>();
    private static int numConnections = 0;  //Counts the total number of connections
    private SS_GameEngine gameEngine;
    //--private instance variables--
    private int myNumber = 0; //Which connection number am I?
    private GenericComm comm = null; //Generic communication object
    Socket mine;
    public Color myColor;
    public PlayerAccount myAccount;
    private boolean checkedPassword = false;
    public SS_Thread(Socket socket, SS_GameEngine theEngine) //Constructor
    {        
        super("SS_Thread");
        mine = socket;
        gameEngine = theEngine;
        //***There is a new connection, and I'm it!
        numConnections++;
        myNumber = numConnections;
        //***Initialize the GenericComm object.  
        comm = new GenericComm(socket);
        comm.setDebugValues("SS_Thread", myNumber);
        checkedPassword = false;
        SS_Thread.connections.add(this);
    }

    //This method is automatically called once the Thread is running.  
    //---------------------------------------------------------------
    public void run() 
    {
        String inputLine;
//        Player myself = new Player(mine.getPort());
        gameEngine.addNewPlayer(mine.getPort());
        comm.sendMessage("WELCOME" + mine.getPort() ); //Send a welcome. 
        System.out.println("SS: Welcome" + mine.getPort() );
        //This loop constantly waits for input from Client and responds...
        //----------------------------------------------------------------
        while ((inputLine = comm.getMessage()) != null){
            if(myColor == null) {
                //When client chooses a color, set myColor to that color
                if(inputLine.startsWith("COLOR")) {
                    if(inputLine.length() > 5) myColor = Map.unpackColor(inputLine.substring(5,6));
                    comm.sendMessage("RECIEVEDCOLOR");
                }
            } else if(!checkedPassword) {
                    //Password entered
                    if(inputLine.startsWith("PASSWORD")) {
                        String password = inputLine.substring(8);
                        //If account exists for this color, check password
                        for(int i = 0; i < gameEngine.players.size(); i++) {
                            if(gameEngine.players.get(i).myColor.equals(myColor)) {
                                if(gameEngine.players.get(i).checkPassword(password)) {
                                    System.out.println("Login successful" + Map.packColor(myColor));
                                    myAccount = gameEngine.players.get(i);
                                    checkedPassword = true;
                                    comm.sendMessage("RECIEVEDPASSWORD");
                                } else {
                                    System.out.println("Login failed" + Map.packColor(myColor));
                                    connections.remove(this);
                                    comm.closeNicely();
                                    return;
                                }
                            }
                        }
                        
                        //Check to make sure that this color isn't taken by a bot
                        for(int i = 0; i < gameEngine.bots.size(); i++) {
                            if(gameEngine.bots.get(i).getColor().equals(myColor)) {
                                System.out.println("Color taken" + Map.packColor(myColor));
                                connections.remove(this);
                                comm.closeNicely();
                                return;
                            } 
                        }
                        
                        //Otherwise create a new account for this color
                        if(myAccount == null) {
                            myAccount = new PlayerAccount(password, myColor, gameEngine.map);
                            gameEngine.players.add(myAccount);
                        }
                    }
            } else {
                if(inputLine.equals("update"))
                {
                    String gameData = gameEngine.getStatusUpdate(myAccount);
                    comm.sendMessage(gameData);
                } 
                else
                {
                    String gameData = gameEngine.processInput(inputLine, myAccount);
                    comm.sendMessage(gameData);
                }
            }
        }
        System.out.println("END OF WHILE!!! - SS_Thread");
        connections.remove(this);
        //Clean things up by closing streams and sockets.
        //-----------------------------------------------
        comm.closeNicely();
    } //--end of run() method--
    

} //--end of SS_Thread class--

