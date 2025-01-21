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
import static Objects.Map.isValid;
import Objects.Soldier;
import Objects.Wall;
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
    public static final int buttonShift = -50;
    public Map map = new Map();
    private boolean recievedColor = false; //Color has been communicated to the server
    private boolean confirmedPassword = false; //Password has been sent to and recieved by the server
    public int selectedRow = -1;
    public int selectedCol = -1;
    private String selectedButton = "";
    private int soldierLevel = 1;
    private int towerLevel = 1;
    private int shipLevel = 1;
    private int wallLevel = 1;
    private int farmPrice = 12;
    private int money = 10;
    private boolean gameStarted = false;
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
        recievedColor = false;
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
        recievedColor = false;
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
        if(gameStarted) g.drawString("Started!", 500, 25);
        else g.drawString("Waiting for players", 500, 25);
        g.drawString("$" + money, 1020, 55); 
        if(map.getIncome(myColor) > 0) g.drawString("+" + map.getIncome(myColor), 1100, 55); 
        else g.drawString(map.getIncome(myColor) + "", 1100, 55); 
        map.draw(g, xOffset, yOffset, selectedRow, selectedCol);
        
        /** Buttons */
        //Soldier
        g.setColor(Color.BLACK);
        g.drawRect(1020, 170+buttonShift, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("soldier")) g.fillRect(1021, 171+buttonShift, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + soldierLevel + " Soldier ($" + soldierPrice(soldierLevel) + ", -" + soldierCost(soldierLevel) + ")", 1030, 210+buttonShift);
        
        //Farm
        g.setColor(Color.BLACK);
        g.drawRect(1020, 270+buttonShift, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("farm")) g.fillRect(1021, 271+buttonShift, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Farm ($" + farmPrice + ", +4)", 1030, 310+buttonShift);
        
        //Tower
        g.setColor(Color.BLACK);
        g.drawRect(1020, 370+buttonShift, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("tower")) g.fillRect(1021, 371+buttonShift, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + towerLevel + " Tower ($" + towerPrice(towerLevel) + ", -" + towerCost(towerLevel) + ")", 1030, 410+buttonShift);
        
        //Battleship
        g.setColor(Color.BLACK);
        g.drawRect(1020, 470+buttonShift, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("battleship")) g.fillRect(1021, 471+buttonShift, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + shipLevel + " Ship ($" + shipPrice(shipLevel) + ", -" + shipCost(shipLevel) + ")", 1030, 510+buttonShift);
        
        //Wall
        g.setColor(Color.BLACK);
        g.drawRect(1020, 570+buttonShift, 150, 60);
        g.setColor(Color.GREEN);
        if(selectedButton.equals("wall")) g.fillRect(1021, 571+buttonShift, 149, 59);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        g.drawString("Level " + wallLevel + " Wall ($" + wallPrice(wallLevel) + ", -" + wallCost(wallLevel) + ")", 1030, 610+buttonShift);
        
        //Farm price
        farmPrice = map.farmPrice(myColor);
        
        return g;
    }//--end of renderFrame method--
    
    //Returns the price to buy a soldier
    public int soldierPrice(int level) {
        if(level <= 3) return 10*level;
        if(level == 4) return 60;
        if(level == 5) return 150;
        return 0; //Hmmm...
    }
    
    //Returns the income deduction for maintaining a soldier
    public int soldierCost(int level) {
        if(level == 1) return 2;
        if(level == 2) return 6;
        if(level == 3) return 18;
        if(level == 4) return 36;
        return 72;
    }
    
    //Returns the income deduction for maintaining a tower
    public int towerCost(int level) {
        if(level == 1) return 1;
        if(level == 2) return 6;
        return 15;
    }
    
    //Returns the income deduction for maintaining a battleship
    public int shipCost(int level) {
        if(level == 1) return 2;
        if(level == 2) return 4;
        if(level == 3) return 8;
        if(level == 4) return 12;
        if(level == 5) return 18;
        return 0; //Level 0!
    }
    
    //Returns the income deduction for maintaining a battleship
    public int wallCost(int level) {
        return 0;
    }
    
    //-------------------------------------------------------
    //Respond to Mouse Events
    //-------------------------------------------------------
    public void mouseClicked(MouseEvent e)  
    {   
        int row = -1;
        int col = -1;
        int x = mouseX - xOffset;
        int y = mouseY - yOffset;
        
        if(selectedButton.equals("wall")) {
            int side = -1;
            
            //Central rectangle
            if((x+8) % 24 < 16) {
                col = (x+8) / 24;
                if(col % 2 == 0) {
                    row = 2*((y+14)/28);
                    if((y/7) % 4 == 1) side = 3;
                    if((y/7) % 4 == 2) side = 0;
                }
                else {
                    row = 2*(y/28)+1;
                    if((y/7) % 4 == 0) side = 0;
                    if((y/7) % 4 == 3) side = 3;
                }
            } 
            
            //Left and right sides  
            else {
                col = (x+8) / 24;
                if(col % 2 == 0) {
                    if((y/14) % 2 == 0) {
                       if(Map.defineHexagon(y/14, col, 0, 0, 0).contains(x,y)) {
                           row = y/14;
                           side = 2;
                       } else {
                           col++;
                           row = (y/14) + 1;
                           side = 5;
                       }
                    } else {
                        if(Map.defineHexagon(y/14, col+1, 0, 0, 0).contains(x,y)) {
                           col++;
                           row = y/14;
                           side = 4;
                       } else {
                           row = (y/14) + 1;
                           side = 1;
                       }
                    }
                } else {
                    if((y/14) % 2 == 1) {
                       if(Map.defineHexagon(y/14, col, 0, 0, 0).contains(x,y)) {
                           row = y/14;
                           side = 2;
                       } else {
                           col++;
                           row = (y/14) + 1;
                           side = 5;
                       }
                    } else {
                        if(Map.defineHexagon(y/14, col+1, 0, 0, 0).contains(x,y)) {
                           col++;
                           row = y/14;
                           side = 4;
                       } else {
                           row = (y/14) + 1;
                           side = 1;
                       }
                    }
                }
            }
            
            //Place wall
            if(0 <= row && row < 40 && 0 <= col && col < 40 && 0 <= side && side < 6) {
                if(map.walls[row][col][side] == null) comm.sendMessage("WALL" + row + "," + col + ";" + side + "/" + wallLevel);
                else if(map.walls[row][col][side].level < wallLevel) comm.sendMessage("WALL" + row + "," + col + ";" + side + "/" + wallLevel);
            }
        }
            
        //Figure out which hexagon clicked
        if((x+8) % 24 < 16) {
             col = (x+8) / 24;
             if(col % 2 == 0) row = 2*((y+14)/28);
             else row = 2*(y/28)+1;
        } else {
                col = (x+8) / 24;
                if(col % 2 == 0) {
                    if((y/14) % 2 == 0) {
                       if(Map.defineHexagon(y/14, col, 0, 0, 0).contains(x,y)) {
                           row = y/14;
                       } else {
                           col++;
                           row = (y/14) + 1;
                       }
                    } else {
                        if(Map.defineHexagon(y/14, col+1, 0, 0, 0).contains(x,y)) {
                           col++;
                           row = y/14;
                       } else {
                           row = (y/14) + 1;
                       }
                    }
                } else {
                    if((y/14) % 2 == 1) {
                       if(Map.defineHexagon(y/14, col, 0, 0, 0).contains(x,y)) {
                           row = y/14;
                       } else {
                           col++;
                           row = (y/14) + 1;
                       }
                    } else {
                        if(Map.defineHexagon(y/14, col+1, 0, 0, 0).contains(x,y)) {
                           col++;
                           row = y/14;
                       } else {
                           row = (y/14) + 1;
                       }
                    }
                }
            }
        
        //Place soldier
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(selectedButton.equals("soldier") 
                    && map.besideColorIncludeWalls(row, col, myColor, soldierLevel)
                    && map.water[row][col] == false) { 
                    if(map.tileColors[row][col].equals(myColor)) {
                        if(map.objects[row][col].equals("")) {
                            comm.sendMessage("SOL" + soldierLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                        }
                    } else if(map.getDefense(row, col) < soldierLevel) {
                        if(map.objects[row][col] instanceof Soldier) {
                            Soldier sol = (Soldier)map.objects[row][col];
                            if(soldierLevel > sol.level) {
                                comm.sendMessage("SOL" + soldierLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                            }
                        } else comm.sendMessage("SOL" + soldierLevel + "," + row + "," + col + "," + Map.packColor(myColor));
                    }
            }
        }
        
        //Place soldier onto ship
        if(0 <= row && row < 40 && 0 <= col && col < 40) {
            if(selectedButton.equals("soldier") && map.objects[row][col] instanceof Battleship && map.besideColor(row, col, myColor)) {
                Battleship ship = (Battleship) map.objects[row][col];
                if(ship.captain == null && ship.thisColor.equals(myColor)) {
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
                    if(str.startsWith("capital")) {
                        int level = Integer.parseInt(str.substring(7,8));
                        if(towerLevel > level) comm.sendMessage("CAP" + towerLevel + "," + row + "," + col);
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
                        && map.besideColor(row, col, myColor) 
                        && Map.distance(selectedRow, selectedCol, row, col) <= 4 
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
        if(1020 < mouseX && mouseX < 1170 && 170+buttonShift < mouseY && mouseY < 230+buttonShift) {
            if(selectedButton.equals("soldier")) {
                if(soldierLevel < 5) soldierLevel++;
                else soldierLevel = 1;
            } else selectedButton = "soldier";
        } else
        if(1020 < mouseX && mouseX < 1170 && 270+buttonShift < mouseY && mouseY < 330+buttonShift) {
            selectedButton = "farm";
        } else
        if(1020 < mouseX && mouseX < 1170 && 370+buttonShift < mouseY && mouseY < 430+buttonShift) {
            if(selectedButton.equals("tower")) {
                if(towerLevel < 3) towerLevel++;
                else towerLevel = 1;
            } else selectedButton = "tower";
        } else
        if(1020 < mouseX && mouseX < 1170 && 470+buttonShift < mouseY && mouseY < 530+buttonShift) {
            if(selectedButton.equals("battleship")) {
                if(shipLevel < 5) shipLevel++;
                else { 
                    if(money < 500) shipLevel = 1;
                    else shipLevel = 0; //Level 0 ship easter egg!
                }
            } else selectedButton = "battleship";
        } else
        if(1020 < mouseX && mouseX < 1170 && 570+buttonShift < mouseY && mouseY < 630+buttonShift) {
            if(selectedButton.equals("wall")) {
                if(wallLevel < 3) wallLevel++;
                else { 
                    wallLevel = 1;
                }
            } else selectedButton = "wall";
        } else if(!selectedButton.equals("wall")) selectedButton = "";
        
        selectedRow = row;
        selectedCol = col;
    }

    public static int towerPrice(int level) {
        if(level == 1) return 15;
        if(level == 2) return 35;
        if(level == 3) return 85;
        return 10000; //You can't buy level 4!
    }
    
    public static int shipPrice(int level) {
        if(level == 1) return 15;
        if(level == 2) return 25;
        if(level == 3) return 40;
        if(level == 4) return 60;
        if(level == 5) return 100;
        return 500; //You can't buy level 6! (or can you... ?)
    }
    
    public static int wallPrice(int level) {
        if(level == 1) return 1;
        if(level == 2) return 2;
        if(level == 3) return 5;
        return 10000;
    }
    
    //-------------------------------------------------------
    //Respond to Keyboard Events
    //-------------------------------------------------------
    public void keyTyped(KeyEvent e) 
    {
        if(e.getKeyChar() == 'q') {
            if(selectedButton.equals("soldier")) {
                if(soldierLevel < 5) soldierLevel++;
                else soldierLevel = 1;
            } else selectedButton = "soldier";
        }
        if(e.getKeyChar() == 'w') selectedButton = "farm";
        if(e.getKeyChar() == 'e') {
            if(selectedButton.equals("tower")) {
                if(towerLevel < 3) towerLevel++;
                else towerLevel = 1;
            } else selectedButton = "tower";
        }
        if(e.getKeyChar() == 'r') {
            if(selectedButton.equals("battleship")) {
                if(shipLevel < 5) shipLevel++;
                else { 
                    if(money < 500) shipLevel = 1;
                    else shipLevel = 0; //Level 0 ship easter egg!
                }
            } else selectedButton = "battleship";
        }
        if(e.getKeyChar() == 't') {
            if(selectedButton.equals("wall")) {
                if(wallLevel < 3) wallLevel++;
                else { 
                    wallLevel = 1;
                }
            } else selectedButton = "wall";
        }
        if(e.getKeyChar() == '1') {
            if(selectedButton.equals("soldier")) soldierLevel = 1;
            if(selectedButton.equals("tower")) towerLevel = 1;
            if(selectedButton.equals("battleship")) shipLevel = 1;
            if(selectedButton.equals("wall")) wallLevel = 1;
        }
        if(e.getKeyChar() == '2') {
            if(selectedButton.equals("soldier")) soldierLevel = 2;
            if(selectedButton.equals("tower")) towerLevel = 2;
            if(selectedButton.equals("battleship")) shipLevel = 2;
            if(selectedButton.equals("wall")) wallLevel = 2;
        }
        if(e.getKeyChar() == '3') {
            if(selectedButton.equals("soldier")) soldierLevel = 3;
            if(selectedButton.equals("tower")) towerLevel = 3;
            if(selectedButton.equals("battleship")) shipLevel = 3;
            if(selectedButton.equals("wall")) wallLevel = 3;
        }
        if(e.getKeyChar() == '4') {
            if(selectedButton.equals("soldier")) soldierLevel = 4;
            if(selectedButton.equals("battleship")) shipLevel = 4;
        }
        if(e.getKeyChar() == '5') {
            if(selectedButton.equals("soldier")) soldierLevel = 5;
            if(selectedButton.equals("battleship")) shipLevel = 5;
        }
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
        if(!recievedColor) {
            comm.sendMessage("COLOR" + Map.packColor(myColor));
        } else if(!confirmedPassword) {
            comm.sendPassword();
        } else comm.sendMessage("update");
        response = comm.getMostRecentMessage();
        if(response == null)
        { response = "No response."; }
        else {
            //When unpacking a map, it returns the player unpacking it.  
           processMessage(response);
        }
    }   //end of actionPerformed()
    public void processMessage(String input) {
        if(input.equals("RECIEVEDCOLOR")) {
            recievedColor = true;
            return;
        }
        if(input.equals("RECIEVEDPASSWORD")) {
            confirmedPassword = true;
            return;
        }
        if(input.startsWith("legit_3.3")) {
            if(input.substring(input.indexOf("#") + 1, input.indexOf("/")).equals("true")) gameStarted = true;
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

