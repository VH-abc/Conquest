package server;
//TODO: Improve Assign fair teams and starting locations
//TODO: processInput for PowerUps
//TODO: Fix glitchyness
//TODO: interactions between objects
//TODO: determine if the game is won
//TODO: release people from jail after a certain amount of time

import Objects.Bot;
import Objects.Battleship;
import Objects.EasyBot;
import Objects.Map;
import Objects.Soldier;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import javax.swing.JOptionPane;
import util.AnimationPanel;
import static server.PlayerAccount.turnLength;
import util.ArcadeDemo;
import util.GenericComm;

public class SS_GameEngine extends AnimationPanel {

    public static final double moveDelay = turnLength; //How many seconds befor a soldier can be moved again
    public Map map;
    public ArrayList<Bot> bots = new ArrayList<Bot>();
    public ArrayList<PlayerAccount> players = new ArrayList<PlayerAccount>();
    Random randy = new Random();
    public boolean gameStarted = false;
    public int numOfPlayers = 10000;
    //Constructor

    public SS_GameEngine() {
        super("Conquest - SERVER", 1200, 700);
        map = new Map();
        String players = JOptionPane.showInputDialog
                         ("How many players?", "");
        if(players.equals("")) numOfPlayers = 10000;
        else numOfPlayers = Integer.parseInt(players);
    }

    /**
     * When a new player joins on the server, this method is called to add the
     * player to the game. Returns the message to be send to the client
     *
     * @param p
     */
    public void addNewPlayer(int id) {
        System.out.println("added");
    }

    /**
     * This method receives a message from a client and updates the game
     * accordingly.
     *
     * @param theInput
     * @param myConnNumber
     */
    public String processInput(String theInput, PlayerAccount account) {
        Color color = account.myColor;
        
        if(theInput.length() > 2 && gameStarted) {
            //Soldier placement
            if(theInput.substring(0, 3).equals("SOL")) {
                System.out.println(theInput);
                int solLevel = Integer.parseInt(theInput.substring(3, theInput.indexOf(",")));
                String afterComma1 = theInput.substring(theInput.indexOf(",") + 1, theInput.length());
                String row = afterComma1.substring(0, afterComma1.indexOf(","));
                String afterComma2 = afterComma1.substring(afterComma1.indexOf(",") + 1, afterComma1.length());
                String col = afterComma2.substring(0, afterComma2.indexOf(","));
                int r = Integer.parseInt(row);
                int c = Integer.parseInt(col);
                if(0 <= r && r < 40 && 0 <= c && c < 40 && account.money >= 10*solLevel) {
                    if(!map.water[r][c]) {
                        map.tileColors[r][c] = color;
                        map.objects[r][c] = new Soldier(solLevel, (int)(30 * moveDelay));
                        account.money -= 10*solLevel;
                    } else if(map.objects[r][c] instanceof Battleship) {
                        Battleship ship = (Battleship) map.objects[r][c];
                        ship.captain = new Soldier(solLevel, shipMovementCooldown(ship.level));
                        ship.thisColor = color;
                        account.money -= 10*solLevel;
                    }
                }
            }
            
            //Ship placement
            if(theInput.substring(0, 3).equals("SHP")) {
                System.out.println(theInput);
                int shipLevel = Integer.parseInt(theInput.substring(3, theInput.indexOf(",")));
                String afterComma1 = theInput.substring(theInput.indexOf(",") + 1, theInput.length());
                String row = afterComma1.substring(0, afterComma1.indexOf(","));
                String afterComma2 = afterComma1.substring(afterComma1.indexOf(",") + 1, afterComma1.length());
                String col = afterComma2.substring(0, afterComma2.indexOf(","));
                int r = Integer.parseInt(row);
                int c = Integer.parseInt(col);
                System.out.println(color);
                if(0 <= r && r < 40 && 0 <= c && c < 40 && account.money >= ArcadeDemo.shipPrice(shipLevel)) {
                    map.objects[r][c] = new Battleship(shipLevel,color);
                    account.money -= ArcadeDemo.shipPrice(shipLevel);
                }
            }
            
            //Tower placement
            if(theInput.substring(0, 3).equals("TOW")) {
                System.out.println(theInput);
                int towLevel = Integer.parseInt(theInput.substring(3, theInput.indexOf(",")));
                String afterComma1 = theInput.substring(theInput.indexOf(",") + 1, theInput.length());
                String row = afterComma1.substring(0, afterComma1.indexOf(","));
                String afterComma2 = afterComma1.substring(afterComma1.indexOf(",") + 1, afterComma1.length());
                String col = afterComma2.substring(0, afterComma2.length());
                int r = Integer.parseInt(row);
                int c = Integer.parseInt(col);
                if(0 <= r && r < 40 && 0 <= c && c < 40 && account.money >= ArcadeDemo.towerPrice(towLevel)) {
                    map.objects[r][c] = "tower" + towLevel;
                    account.money -= ArcadeDemo.towerPrice(towLevel);
                }
            }
            
            //Farm placement
            if(theInput.substring(0, 3).equals("FAR")) {
                System.out.println(theInput);
                String row = theInput.substring(3, theInput.indexOf(","));
                String col = theInput.substring(theInput.indexOf(",") + 1, theInput.length());
                int r = Integer.parseInt(row);
                int c = Integer.parseInt(col);
                int farmPrice = map.farmPrice(color);
                if(0 <= r && r < 40 && 0 <= c && c < 40 && account.money >= farmPrice) {
                    map.objects[r][c] = "farm";
                    account.money -= farmPrice;
                }
            }
            
            //Movement
            if(theInput.substring(0, 3).equals("MOV")) {
                System.out.println(theInput);
                int fromRow = Integer.parseInt(theInput.substring(theInput.indexOf("V") + 1, theInput.indexOf("C")));
                int fromCol = Integer.parseInt(theInput.substring(theInput.indexOf("C") + 1, theInput.indexOf("r")));
                int toRow = Integer.parseInt(theInput.substring(theInput.indexOf("r") + 1, theInput.indexOf("c")));
                int toCol = Integer.parseInt(theInput.substring(theInput.indexOf("c") + 1, theInput.indexOf("x")));
                Object obj = map.objects[fromRow][fromCol];
                
                //Soldier movement
                if(obj instanceof Soldier && map.tileColors[fromRow][fromCol].equals(color)) {
                    Soldier sol = (Soldier)obj;
                    //System.out.println(map.getDefense(toRow, toCol));
                    if(!map.water[toRow][toCol] &&
                            ((!map.tileColors[toRow][toCol].equals(color) && sol.level > map.getDefense(toRow, toCol))
                            || map.tileColors[toRow][toCol].equals(color) && map.objects[toRow][toCol].equals(""))) {
                        sol.moveTimer = (int)(30 * moveDelay);
                        map.objects[toRow][toCol] = sol;
                        map.tileColors[toRow][toCol] = color;
                        map.objects[fromRow][fromCol] = "";
                    }
                    if(map.objects[toRow][toCol] instanceof Battleship && map.besideColor(toRow, toCol, color)) {
                        Battleship ship = (Battleship) map.objects[toRow][toCol];
                        if(ship.captain == null) {
                            sol.moveTimer = shipMovementCooldown(ship.level);
                            ship.captain = sol;
                            ship.thisColor = color;
                            map.objects[fromRow][fromCol] = "";
                        } else {
                            Soldier occupant = ship.captain;
                            if(!ship.thisColor.equals(color) && occupant.level < sol.level) {
                                sol.moveTimer = shipMovementCooldown(ship.level);
                                ship.captain = sol;
                                ship.thisColor = color;
                                map.objects[fromRow][fromCol] = "";
                            }
                        }
                    }
                }
                
                //Ship movement
                if(obj instanceof Battleship) {
                    Battleship ship = (Battleship) obj;
                    if(ship.captain != null) {
                        if(ship.captain.canMove()) {
                            if(Map.distance(toRow, toCol, fromRow, fromCol) <= 4) {
                                if(map.water[toRow][toCol]) {
                                    if(map.objects[toRow][toCol] instanceof Battleship) {
                                        Battleship otherShip = (Battleship) map.objects[toRow][toCol];
                                        if(otherShip.thisColor.equals(color) || !ship.thisColor.equals(color)) return "legit_3.1" + "#" + gameStarted + "/" + account.money + ";" + map.pack();
                                        if(otherShip.captain == null) {
                                            map.objects[toRow][toCol] = ship;
                                            ship.captain.moveTimer = shipMovementCooldown(ship.level);
                                            map.objects[fromRow][fromCol] = "";
                                        } else {
                                            if(Math.random() < 1/(1+Math.pow(2,otherShip.level-ship.level))) {
                                                map.objects[toRow][toCol] = ship;
                                                ship.captain.moveTimer = shipMovementCooldown(ship.level);
                                                map.objects[fromRow][fromCol] = "";
                                            } else {
                                                map.objects[fromRow][fromCol] = "";
                                            }
                                        }
                                    } else {
                                        map.objects[toRow][toCol] = ship;
                                        ship.captain.moveTimer = shipMovementCooldown(ship.level);
                                        map.objects[fromRow][fromCol] = "";
                                    }
                                } else {
                                    Soldier sol = ship.captain;
                                    if(((!map.tileColors[toRow][toCol].equals(color) && sol.level > map.getDefense(toRow, toCol))
                                            || map.tileColors[toRow][toCol].equals(color) && map.objects[toRow][toCol].equals(""))) {
                                        sol.moveTimer = (int)(30 * moveDelay);
                                        map.objects[toRow][toCol] = sol;
                                        map.tileColors[toRow][toCol] = color;
                                        ship.captain = null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return "legit_3.1" + "#" + gameStarted + "/" + account.money + ";" + map.pack();
    }

    /**
     * This is the method that returns all of the game data to a client who
     * requests it.
     *
     * @param connectionNum
     * @return
     */
    public String getStatusUpdate(PlayerAccount account) {
        String gameData = "legit_3.1" + "#" + gameStarted + "/" + account.money + ";" + map.pack();

        return gameData;
    }
    /*
     public int assignTeam()
     public void assignStartingLocation()
    
     */

    protected Graphics renderFrame(Graphics g) {
        //Start when all players have logged on
        if(!gameStarted) {
            if(players.size() >= numOfPlayers) {
                startGame();
            }
        }
        //General text
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", 0, 12));
        g.drawString("SERVER WINDOW", 10, 12);
        g.drawString("frame=" + frameNumber, 200, 12);
        g.setFont(new Font("Arial", 0, 24));
        if(gameStarted) g.drawString("Started!", 500, 25);
        else g.drawString("Waiting for players", 500, 25);
        map.draw(g, 50, 50, -1, -1);
        if(gameStarted) {
            for(int i = 0; i < bots.size(); i++) {
                Bot bot = bots.get(i);
                g.setColor(bot.getColor());
                g.setFont(new Font("Arial", 0, 24));
                g.drawString("$" + bot.getMoney() + ", " + map.getIncome(bot.getColor()), 1050, 50 + 50*i);
                bot.makeMove();
            }
            for(int i = 0; i < players.size(); i++) {
                players.get(i).update();
            }
        }
        return g;
    }//--end of renderFrame method--

    //Starts the game
    public void startGame() {
        addBot(Color.GREEN);
        addBot(Color.RED);
        addBot(Color.MAGENTA);
        addBot(Color.YELLOW);
        addBot(Map.ORANGE);
        addBot(Color.CYAN);
        gameStarted = true;
    }
    
    //Adds a bot if and only if there is no player logged on to that color
    public void addBot(Color c) {
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).myColor.equals(c)) return;
        }
        bots.add(new EasyBot(c, map));
    }
    
    private void debugMsg(String msg) {
        if (GenericComm.debugMode) {
            System.out.println(msg);
        }
    }
    
    //Returns the number of frames before a ship can move again. Higher level ships have aa shorter cooldown.
    public static int shipMovementCooldown(int level) {
        return (int)(30.0 * moveDelay * (1.0 - ((level - 1.0) / 6.0)));
    }
}
