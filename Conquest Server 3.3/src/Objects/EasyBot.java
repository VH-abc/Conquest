/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;
import static server.SS_GameEngine.moveDelay;
import util.ArcadeDemo;
import static server.PlayerAccount.turnLength;
import server.SS_GameEngine;
import static server.SS_GameEngine.shipMovementCooldown;
import static server.SS_GameEngine.soldierMovementCooldown;
import static util.ArcadeDemo.shipPrice;
import static util.ArcadeDemo.towerCost;

/**
 * Beginner level AI
 * @author 20hebbarv
 */
public class EasyBot implements Bot{
    public Color myColor;
    public Map map;
    public SS_GameEngine engine;
    public int money;
    public double moneyTimer; //When this reaches 1, add 1 to money
    public int farmPrice;
    private int mode;
    private static final int SOLDIER = 0;
    private static final int FARM = 1;
    private static final int TOWER = 2;
    private static final int SHIP = 3;
    private double[] tendencies; //How likely to place a soldier vs. a farm vs. a tower
    private int actionDelay = (int)turnLength; //Number of frames between actions
    private int cooldown; //Number of frames till next action can occur
    private static final int countLimit = 1000;
    
    public EasyBot(Color clr, Map m, SS_GameEngine theEngine) {
        myColor = clr;
        map = m;
        money = 10;
        moneyTimer = 0;
        farmPrice = map.farmPrice(myColor);
        //Random tendencies
        double a = Math.random();
        double b = Math.random();
        double c = Math.random();
        tendencies = new double[3];
        tendencies[0] = a/(a+b+c);
        tendencies[1] = b/(a+b+c);
        tendencies[2] = c/(a+b+c);
        mode = selectMode();
        cooldown = actionDelay;
        engine = theEngine;
    }
    
    public Color getColor() {
        return myColor;
    }
    
    public int getMoney() {
        return money;
    }
    
    public void update() {
        //Update money
        moneyTimer += (double)(map.getIncome(myColor))/(30*turnLength);
        while(moneyTimer >= 1) {
            money++;
            moneyTimer--;
        }
        while(moneyTimer <= -1) {
            money--;
            moneyTimer++;
        }

        //Farm price
        farmPrice = map.farmPrice(myColor);

        //Starvation
        if(money <= 0 && map.getIncome(myColor) < 0) map.starve(myColor);
        
        cooldown--;
    }
    
    public void makeMove() {
        if(cooldown <= 0) {
            //Soldier placement
            if(mode == SOLDIER) {
                boolean cannotPlace = true;
                for(int soldierLevel = 1; soldierLevel <= 5; soldierLevel++) {
                    int count = 0;
                    while(count < countLimit) {
                            count++;
                            int row = (int)(40*Math.random());
                            int col = (int)(40*Math.random());
                            if((row+col) % 2 == 0) {
                                if(!map.tileColors[row][col].equals(myColor) && map.besideColorIncludeWalls(row, col, myColor, soldierLevel) && map.water[row][col] == false) { 
                                    if(map.getDefense(row, col) < soldierLevel) {
                                        if(map.objects[row][col] instanceof Soldier) {
                                            Soldier sol = (Soldier) map.objects[row][col];
                                            if(sol.level < soldierLevel) {
                                                if(money >= soldierPrice(soldierLevel)) {
                                                    map.objects[row][col] = new Soldier(soldierLevel, (int)(soldierMovementCooldown(soldierLevel) * moveDelay));
                                                    map.tileColors[row][col] = myColor;
                                                    money -= soldierPrice(soldierLevel);
                                                    cooldown = actionDelay;
                                                    mode = selectMode();
                                                    makeMove();
                                                    return;
                                                } else cannotPlace = false; //There is an opportunity, just need to save money!
                                            }
                                        } else {
                                            if(money >= soldierPrice(soldierLevel)) {
                                                    map.objects[row][col] = new Soldier(soldierLevel, (int)(soldierMovementCooldown(soldierLevel) * moveDelay));
                                                    map.tileColors[row][col] = myColor;
                                                    money -= soldierPrice(soldierLevel);
                                                    cooldown = actionDelay;
                                                    mode = selectMode();
                                                    makeMove();
                                                    return;
                                            } else cannotPlace = false; //There is an opportunity, just need to save money!
                                        }
                                    }
                                }
                                if(map.besideColor(row, col, myColor) && map.objects[row][col] instanceof Battleship) {
                                    Battleship ship = (Battleship) map.objects[row][col];
                                    if(ship.captain == null) {
                                        int lvl = (int)(1.0/Math.random());
                                        if(lvl > 5) lvl = 5;
                                        if(money >= soldierPrice(lvl)) {
                                            ship.thisColor = myColor;
                                            ship.captain = new Soldier(lvl, shipMovementCooldown(ship.level));
                                            money -= soldierPrice(lvl);
                                            cooldown = actionDelay;
                                            mode = selectMode();
                                            makeMove();
                                            return;
                                        } else cannotPlace = false; //There is an opportunity, just need to save money!
                                    }
                                }
                            }
                    }
                }
                if(cannotPlace) mode = SHIP; //If there is nowhere to place a soldier, pick a new mode!
            }
            
            //Ship placement
            if(mode == SHIP) {
                boolean cannotPlace = true;
                    int count = 0;
                    while(count < countLimit) {
                            count++;
                            int row = (int)(40*Math.random());
                            int col = (int)(40*Math.random());
                            if((row+col) % 2 == 0) {
                                if(map.besideColor(row, col, myColor) && map.water[row][col]) { 
                                        int shipLevel = (int)(5*Math.random()) /* + 1 */; //Sometimes buys level 0 ships! +1 fixes it, but I left it in as an easter egg
                                        if(money >= shipPrice(shipLevel)) {
                                            map.objects[row][col] = new Battleship(shipLevel, myColor);
                                            money -= shipPrice(shipLevel);
                                            cooldown = actionDelay;
                                            mode = selectMode();
                                            makeMove();
                                            return;
                                        } else cannotPlace = false; //There is an opportunity, just need to save money!
                                }
                            }
                    }
                if(cannotPlace) mode = selectMode(); //If there is nowhere to place a soldier, pick a new mode!
            }
            
            //Farm placement
            if(mode == FARM) {
                boolean cannotPlace = true;
                for(int defense = 4; defense >= 0; defense--) {
                        int count = 0;
                        while(count < countLimit) {
                            count++;
                            int row = (int)(40*Math.random());
                            int col = (int)(40*Math.random());
                            if((row+col) % 2 == 0) {
                                if(map.tileColors[row][col].equals(myColor) && map.water[row][col] == false && map.objects[row][col].equals("")) { 
                                    if(map.getDefense(row, col) >= defense) {
                                        if(money >= farmPrice) {
                                            map.objects[row][col] = "farm";
                                            money -= farmPrice;
                                            cooldown = actionDelay;
                                            mode = selectMode();
                                            makeMove();
                                            return;
                                        } else cannotPlace = false;
                                    }
                                }
                            }
                        }
                }
                if(cannotPlace) mode = selectMode();
            }
            
            //Tower placement
            if(mode == TOWER) {
                boolean cannotPlace = true;
                for(int towerLevel = 1; towerLevel <= 3; towerLevel++) {
                    if(map.getIncome(myColor) <= towerCost(towerLevel)) {
                        mode = selectMode();
                        makeMove();
                        return;
                    }
                    int count = 0;
                    while(count < countLimit) {
                            count++;
                            int row = (int)(40*Math.random());
                            int col = (int)(40*Math.random());
                            if((row+col) % 2 == 0) {
                                if(map.tileColors[row][col].equals(myColor) && map.besideOpponent(row, col, myColor) && !map.water[row][col]) {
                                    if(map.getDefense(row, col) < towerLevel + 1) {
                                        int necessaryDefense = map.requiredDefense(row, col, myColor);
                                        if(necessaryDefense < 2) necessaryDefense = 2;
                                        if(necessaryDefense == towerLevel + 1) {
                                            String str = map.objects[row][col].toString();
                                            if(str.equals("") || str.contains("tower")) {
                                                if(money >= ArcadeDemo.towerPrice(towerLevel)) {
                                                    map.objects[row][col] = "tower" + towerLevel;
                                                    money -= ArcadeDemo.towerPrice(towerLevel);
                                                    cooldown = actionDelay;
                                                    mode = selectMode();
                                                    makeMove();
                                                    return;
                                                } else cannotPlace = false;
                                            }
                                            if(str.contains("capital")) {
                                                if(money >= ArcadeDemo.towerPrice(towerLevel)) {
                                                    map.objects[row][col] = "capital" + towerLevel;
                                                    money -= ArcadeDemo.towerPrice(towerLevel);
                                                    cooldown = actionDelay;
                                                    mode = selectMode();
                                                    makeMove();
                                                    return;
                                                } else cannotPlace = false;
                                            }
                                        }
                                    }
                                }
                            }
                    }
                }
                if(cannotPlace) mode = selectMode();
            }
            
            //Soldier attack
            for(int soldierLevel = 5; soldierLevel >= 1; soldierLevel--) {
                int count = 0;
                while(count < countLimit) {
                    count++;
                    int row = (int)(40*Math.random());
                    int col = (int)(40*Math.random());
                    if((row+col)%2 == 0) {
                        if(map.tileColors[row][col].equals(myColor) && map.objects[row][col] instanceof Soldier) {
                            Soldier sol = (Soldier) map.objects[row][col];
                            if(sol.canMove() && sol.level >= soldierLevel) {
                                for(int defense = sol.level - 1; defense >= 0; defense--) {
                                    boolean done = false;
                                    int count2 = 0;
                                    while(count2 < countLimit && !done) {
                                            count2++;
                                            int r = (int)(40*Math.random());
                                            int c = (int)(40*Math.random());
                                            if((r+c) % 2 == 0 && Map.distance(row,col,r,c) <= 4) {
                                                if(!map.tileColors[r][c].equals(myColor) && map.besideColor(r, c, myColor) && map.water[r][c] == false) {
                                                    if(map.getDefense(r, c) == defense) {
                                                        if(engine.tryAttack(row, col, r, c, myColor, sol)) {
                                                            done = true;
                                                            cooldown = actionDelay;
                                                            mode = selectMode();
                                                            makeMove();
                                                            return;
                                                        }
                                                    }
                                                }
                                            }
                                    }
                                }   
                            }
                        }
                    }
                }
            }
            
            //Soldier movement within territory or onto ship, ship movement and landing
            for(int soldierLevel = 5; soldierLevel >= 1; soldierLevel--) {
                int count = 0;
                while(count < countLimit) {
                    count++;
                    int row = (int)(40*Math.random());
                    int col = (int)(40*Math.random());
                    if((row+col)%2 == 0) {
                        //Soldier movement
                        if(map.tileColors[row][col].equals(myColor) && map.objects[row][col] instanceof Soldier) {
                            Soldier sol = (Soldier) map.objects[row][col];
                            if(sol.canMove() && sol.level >= soldierLevel) {
                                boolean done = false;
                                int count2 = 0;
                                while(count2 < countLimit && !done) {
                                        count2++;
                                        int r = (int)(40*Math.random());
                                        int c = (int)(40*Math.random());
                                        if((r+c) % 2 == 0 && Map.distance(row,col,r,c) <= 4) {
                                            //Onto land
                                            if(map.tileColors[r][c].equals(myColor) && map.objects[r][c].equals("") && map.water[r][c] == false) {
                                                    done = true;
                                                    map.objects[r][c] = sol;
                                                    sol.moveTimer = (int)(30*moveDelay);
                                                    map.objects[row][col] = "";
                                                    cooldown = actionDelay;
                                                    mode = selectMode();
                                                    makeMove();
                                                    return;
                                            }
                                            
                                            //Onto ship
                                            if(map.besideColor(r, c, myColor) && map.objects[r][c] instanceof Battleship) {
                                                Battleship ship = (Battleship) map.objects[r][c];
                                                if(ship.captain == null) {
                                                    done = true;
                                                    ship.captain = sol;
                                                    ship.thisColor = myColor;
                                                    sol.moveTimer = shipMovementCooldown(ship.level);
                                                    map.objects[row][col] = "";
                                                    cooldown = actionDelay;
                                                    mode = selectMode();
                                                    makeMove();
                                                    return;
                                                } else {
                                                    Soldier occupant = ship.captain;
                                                    if(!ship.thisColor.equals(myColor) && occupant.level < sol.level) {
                                                        done = true;
                                                        ship.captain = sol;
                                                        ship.thisColor = myColor;
                                                        sol.moveTimer = shipMovementCooldown(ship.level);
                                                        map.objects[row][col] = "";
                                                        cooldown = actionDelay;
                                                        mode = selectMode();
                                                    }
                                                }
                                            }
                                        }
                                }
                            }
                        }
                        
                        //Ship movement
                        if(map.objects[row][col] instanceof Battleship) {
                            Battleship ship = (Battleship) map.objects[row][col];
                            if(ship.thisColor.equals(myColor) && ship.captain != null) {
                                Soldier sol = ship.captain;
                                if(sol.canMove() && sol.level >= soldierLevel) {
                                    boolean done = false;
                                    int count2 = 0;
                                   
                                    //Landing
                                    while(count2 < countLimit && !done) {
                                                count2++;
                                                int r = (int)(40*Math.random());
                                                int c = (int)(40*Math.random());
                                                if(Map.distance(row, col, r, c) == 1) {
                                                    if((r+c) % 2 == 0 && !map.water[r][c] && !map.tileColors[r][c].equals(myColor) && sol.level > map.getDefense(r, c)) {
                                                        if(engine.tryLanding(row, col, r, c, myColor, sol, ship)) {
                                                            done = true;
                                                            cooldown = actionDelay;
                                                            mode = selectMode();
                                                            makeMove();
                                                            return;
                                                        }
                                                    }
                                                }
                                    }
                                
                                    //Movement over water
                                    done = false;
                                    count2 = 0;
                                    while(count2 < countLimit && !done) {
                                            count2++;
                                            int r = (int)(40*Math.random());
                                            int c = (int)(40*Math.random());
                                            if((r+c) % 2 == 0 && Map.distance(row,col,r,c) <= 4) {
                                                if(map.objects[r][c].equals("") && map.water[r][c]) {
                                                        done = true;
                                                        map.objects[r][c] = ship;
                                                        sol.moveTimer = shipMovementCooldown(ship.level);
                                                        map.objects[row][col] = "";
                                                        cooldown = actionDelay;
                                                        mode = selectMode();
                                                        makeMove();
                                                        return;
                                                }
                                            }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    //What mode to go to next? Soldier placement, farm placement, towers... ?
    public int selectMode() {
        double rand = Math.random();
        if(rand < tendencies[0]) return SOLDIER;
        if(rand < tendencies[1] + tendencies[0]) return FARM;
        return TOWER;
    }
    
    //Returns the price to buy a soldier
    public int soldierPrice(int level) {
        if(level <= 3) return 10*level;
        if(level == 4) return 60;
        if(level == 5) return 150;
        return 0; //Hmmm...
    }
}

