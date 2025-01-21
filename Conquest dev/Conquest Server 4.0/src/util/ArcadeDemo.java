package util;


/**
 * Class ArcadeDemo
 * This class contains demos of many of the things you might
 * want to use to make an animated arcade game.
 * 
 * Adapted from the AppletAE demo from years past. 
 */


import Objects.Battleship;
import Objects.Map;
import Objects.Soldier;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.applet.AudioClip;   
import java.awt.Font;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.Timer;
import util.AnimationPanel;


public class ArcadeDemo extends AnimationPanel implements ActionListener
{
    private int myID;
    private Color myColor = Color.GRAY;
    public static final int xOffset = 50;
    public static final int yOffset = 50;
    public Map map = new Map();
    public int selectedRow = -1;
    public int selectedCol = -1;
    private String selectedButton = "";
    private int soldierLevel = 1;
    private int towerLevel = 1;
    private int shipLevel = 1;
    private int farmPrice = 12;
    private int money = 10;
    //Constants
    //-------------------------------------------------------

    //Instance Variables
    //-------------------------------------------------------

    //Constructor
    //-------------------------------------------------------
    public ArcadeDemo(Color c)
    {   //Enter the name and width and height.  
        super("Conquest", 1200, 700);
        comm = new GenericComm();
        comm.setDebugValues("C_COMM",0); 
        //This is how a new instance gets it's id...
        myID = comm.getSocket().getLocalPort();
        initTimer();
        myColor = c;
        comm.sendMessage("COLOR" + Map.packColor(myColor));
    }
    
    public ArcadeDemo()
    {   //Enter the name and width and height.  
        super("Conquest", 1200, 700);
        comm = new GenericComm();
        comm.setDebugValues("C_COMM",0); 
        //This is how a new instance gets it's id...
        myID = comm.getSocket().getLocalPort();
        initTimer();
        myColor = Color.GRAY;
    }
    
    //Network variables
    //-------------------------------------------------------
    private Timer timer;
    private final int DELAY = 100; //delay in mSec
    private GenericComm comm;
    String response = "No response.";
    
    //The renderFrame method is the one which is called each time a frame is drawn.
    //-------------------------------------------------------
    protected Graphics renderFrame(Graphics g) 
    {
        g.setFont(new Font("Arial", 0, 24));
        g.setColor(Color.BLACK);
        g.drawString("$" + money, 1020, 55); 
        if(map.getIncome(myColor) > 0) g.drawString("+" + map.getIncome(myColor), 1100, 55); 
        else g.drawString(map.getIncome(myColor) + "", 1100, 55); 
        map.draw(g, xOffset, yOffset, selectedRow, selectedCol);
        
        /** Buttons */
        //Soldier
        g.setColor(Color.BLACK);
        g.drawRect(1020, 170, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("soldier")) g.fillRect(1021, 171, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + soldierLevel + " Soldier ($" + 10*soldierLevel + ")", 1030, 210);
        
        //Farm
        g.setColor(Color.BLACK);
        g.drawRect(1020, 270, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("farm")) g.fillRect(1021, 271, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Farm ($" + farmPrice + ")", 1030, 310);
        
        //Tower
        g.setColor(Color.BLACK);
        g.drawRect(1020, 370, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("tower")) g.fillRect(1021, 371, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + towerLevel + " Tower ($" + towerPrice(towerLevel) + ")", 1030, 410);
        
        //Battleship
        g.setColor(Color.BLACK);
        g.drawRect(1020, 470, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("battleship")) g.fillRect(1021, 471, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + shipLevel + " Battleship ($" + shipPrice(shipLevel) + ")", 1030, 510);
        
        //Farm price
        farmPrice = map.farmPrice(myColor);
        
        return g;
    }//--end of renderFrame method--
    
    //-------------------------------------------------------
    //Respond to Mouse Events
    //-------------------------------------------------------
    public void mouseClicked(MouseEvent e)  
    { 
        //Figure out which hexagon clicked
        int row = -1;
        int col = -1;
        int x = mouseX - xOffset;
        int y = mouseY - yOffset;
        if((x+8) % 24 < 16) {
             col = (x+8) / 24;
             if(col % 2 == 0) row = 2*((y+14)/28);
             else row = 2*(y/28)+1;
        }

        //Place soldier
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(selectedButton.equals("soldier") 
                    && (map.tileColors[row][col].equals(myColor) || map.besideColor(row, col, myColor))
                    && map.water[row][col] == false) { 
                    if(map.tileColors[row][col].equals(myColor)) {
                        if(map.objects[row][col].equals("")) {
                            comm.sendMessage("SOL" + soldierLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                        }
                    } else if(map.getDefense(row, col) < soldierLevel) {
                        comm.sendMessage("SOL" + soldierLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                    }
            }
        }
        
        //Place soldier onto ship
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(selectedButton.equals("soldier") && map.objects[row][col] instanceof Battleship) {
                Battleship ship = (Battleship) map.objects[row][col];
                if(ship.captain == null) {
                    comm.sendMessage("SOL" + soldierLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                }
            }
        }
        
        //Place battleship
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            //System.out.println(selectedButton);
            if(selectedButton.equals("battleship") 
                    && map.besideColor(row, col, myColor)
                    && map.water[row][col]) { 
                        if(map.objects[row][col].equals("")) {
                            comm.sendMessage("SHP" + shipLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                        }
            }
        }
        
        //Place tower
        Object obj;
        String str;
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            obj = map.objects[row][col];
            //System.out.println(selectedButton);
            if(selectedButton.equals("tower") 
                    && map.tileColors[row][col].equals(myColor)
                    && map.water[row][col] == false) {
                if(obj instanceof String) {
                    str = (String)(obj);
                    if(str.equals("") || (str.length() > 5 && str.substring(0,5).equals("tower") && Integer.parseInt(str.substring(5,6)) < towerLevel)) {
                        comm.sendMessage("TOW" + towerLevel + "," + row + "," + col);
                    }
                }
            }
        }
        
        //Place farm
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            //System.out.println(selectedButton);
            if(selectedButton.equals("farm") 
                    && map.tileColors[row][col].equals(myColor)
                    && map.objects[row][col].equals("")
                    && map.water[row][col] == false){
                comm.sendMessage("FAR" + row + "," + col);
            }
        }
        
        //Move soldier
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(0 <= selectedRow && selectedRow < 40 && 0 <= selectedCol && selectedCol < 40) {
                if(map.objects[selectedRow][selectedCol] instanceof Soldier 
                        && ((Soldier)(map.objects[selectedRow][selectedCol])).canMove() 
                        && Map.distance(selectedRow, selectedCol, row, col) <= 4 
                        && map.besideColor(row, col, myColor)
                        && map.water[row][col] == false) {
                    comm.sendMessage("MOV" + selectedRow + "C" + selectedCol + "r" + row + "c" + col + "x" + Map.packColor(myColor));
                }
            }
        }
        
        //Move ship
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(0 <= selectedRow && selectedRow < 40 && 0 <= selectedCol && selectedCol < 40) {
                if(map.objects[selectedRow][selectedCol] instanceof Battleship) {
                    Battleship ship = (Battleship) map.objects[selectedRow][selectedCol];
                    if(ship.captain != null && ship.thisColor.equals(myColor)) {
                        Soldier troop = (Soldier) ship.captain;
                        if(troop.canMove() && 
                                (Map.distance(selectedRow, selectedCol, row, col) <= 4 && map.water[row][col]) || 
                                (Map.distance(selectedRow, selectedCol, row, col) == 1)) {
                            comm.sendMessage("MOV" + selectedRow + "C" + selectedCol + "r" + row + "c" + col + "x" + Map.packColor(myColor));
                        }
                    }
                }
            }
        }
        
        //Move soldier onto ship
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(0 <= selectedRow && selectedRow < 40 && 0 <= selectedCol && selectedCol < 40) {
                if(map.objects[selectedRow][selectedCol] instanceof Soldier 
                        && ((Soldier)(map.objects[selectedRow][selectedCol])).canMove() 
                        && Map.distance(selectedRow, selectedCol, row, col) <= 4 
                        && map.besideColor(row, col, myColor)
                        && map.objects[row][col] instanceof Battleship) {
                    comm.sendMessage("MOV" + selectedRow + "C" + selectedCol + "r" + row + "c" + col + "x" + Map.packColor(myColor));
                }
            }
        }
        
        //Buttons
        if(1020 < mouseX && mouseX < 1170 && 170 < mouseY && mouseY < 230) {
            if(selectedButton.equals("soldier")) {
                if(soldierLevel < 5) soldierLevel++;
                else soldierLevel = 1;
            } else selectedButton = "soldier";
        } else
        if(1020 < mouseX && mouseX < 1170 && 270 < mouseY && mouseY < 330) {
            selectedButton = "farm";
        } else
        if(1020 < mouseX && mouseX < 1170 && 370 < mouseY && mouseY < 430) {
            if(selectedButton.equals("tower")) {
                if(towerLevel < 3) towerLevel++;
                else towerLevel = 1;
            } else selectedButton = "tower";
        } else
        if(1020 < mouseX && mouseX < 1170 && 470 < mouseY && mouseY < 530) {
            if(selectedButton.equals("battleship")) {
                if(shipLevel < 5) shipLevel++;
                else shipLevel = 1;
            } else selectedButton = "battleship";
        } else
        selectedButton = "";
        
        selectedRow = row;
        selectedCol = col;
    }

    //Returns the income deduction for maintaining a tower
    public static int towerCost(int level) {
        if(level == 1) return 1;
        if(level == 2) return 6;
        return 15;
    }
    
    //Returns the income deduction for maintaining a battleship
    public static int shipCost(int level) {
        if(level == 1) return 2;
        if(level == 2) return 4;
        if(level == 3) return 8;
        if(level == 4) return 12;
        return 18;
    }
    
    public static int towerPrice(int level) {
        if(level == 1) return 15;
        if(level == 2) return 35;
        if(level == 3) return 85;
        return 10000; //You can't buy level 4!
    }
    
    public static int shipPrice(int level) {
        if(level == 1) return 20;
        if(level == 2) return 30;
        if(level == 3) return 50;
        if(level == 4) return 80;
        if(level == 5) return 130;
        return 10000; //You can't buy level 6!
    }
    
    //-------------------------------------------------------
    //Respond to Keyboard Events
    //-------------------------------------------------------
    public void keyTyped(KeyEvent e) 
    {
      
    }
    
    public void keyPressed(KeyEvent e)
    {
   
    }

    public void keyReleased(KeyEvent e)
    {
       
    }
    
    private void initTimer()
    {   //Set up a timer that calls this object's action handler.
        timer = new Timer(DELAY, this);
        timer.setInitialDelay(DELAY);
        timer.setCoalesce(true);
        timer.start();
    }
    public void startTimer()
    {
        comm.sendMessage("Starting Timer");
        timer.start();
    }
    public void stopTimer()
    {
        comm.sendMessage("Stopping Timer");
        timer.stop();  
    }
    public void sendMessage(String choice)
    {
        comm.sendMessage(choice); 
        do {
            response = comm.getMessage();
        }  while (comm.messageWaiting());
    }
    public void actionPerformed(ActionEvent e) 
    {   //--GET UPDATE FROM SERVER--
        //Called whenever timer goes off (every DELAY msec.)
        if(myColor.equals(Color.GRAY)) myColor = comm.getColor();
        comm.sendMessage("update");
        response = comm.getMostRecentMessage();
        if(response == null)
        { response = "No response."; }
        else {
            //When unpacking a map, it returns the player unpacking it.  
           processMessage(response);
        }
    }   //end of actionPerformed()
    public void processMessage(String input) {
        if(input.startsWith("legit_3.1")) {
            money = Integer.parseInt(input.substring(input.indexOf("/") + 1, input.indexOf(";")));
            map.unpack(input.substring(input.indexOf("MAP")));
        }
    }
    //Waits for a login message from the server.
    /*
    public void detectLogin()  {
            String message = comm.getMostRecentMessage();
            if(message != null && message.length() > 1) {
                if(message.substring(0,2).equals("ID")) {
                    String afterComma1 = message.substring(message.indexOf(",") + 1, message.length());
                    String givenID = afterComma1.substring(0, afterComma1.indexOf(","));
                    String playerNum = afterComma1.substring(afterComma1.indexOf(",") + 1, afterComma1.length());
                    if(Integer.parseInt(givenID) == myID) {
                        playerNumber = Integer.parseInt(playerNum);
                    }
                    System.out.println(playerNumber);
                }
            }
    }
    */
    private void debugMsg(String m)
    {
        if(GenericComm.debugMode)
            System.out.println(m);
    }
    
    //-------------------------------------------------------
    //Initialize Graphics
    //-------------------------------------------------------
//-----------------------------------------------------------------------
/*  Image section... 
 *  To add a new image to the program, do three things.
 *  1.  Make a declaration of the Image by name ...  Image imagename;
 *  2.  Actually make the image and store it in the same directory as the code.
 *  3.  Add a line into the initGraphics() function to load the file. 
//-----------------------------------------------------------------------*/
   
    
    public void initGraphics() 
    {      
       
    } //--end of initGraphics()--
    
    //-------------------------------------------------------
    //Initialize Sounds
    //-------------------------------------------------------
//-----------------------------------------------------------------------
/*  Music section... 
 *  To add music clips to the program, do four things.
 *  1.  Make a declaration of the AudioClip by name ...  AudioClip clipname;
 *  2.  Actually make/get the .wav file and store it in the same directory as the code.
 *  3.  Add a line into the initMusic() function to load the clip. 
 *  4.  Use the play(), stop() and loop() functions as needed in your code.
//-----------------------------------------------------------------------*/
   
    
    public void initMusic() 
    {
		
    }

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
}//--end of ArcadeDemo class--

