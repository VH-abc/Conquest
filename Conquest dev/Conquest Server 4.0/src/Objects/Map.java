/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author 20hebbarv
 */
public class Map {
    public Color[][] tileColors = new Color[40][40];
    public Object[][] objects = new Object[40][40];
    public boolean[][] water = new boolean[40][40];
    public static final Color ORANGE = new Color(255,128,0);
    
    public Map() {
        int[][] terrain = randomTerrain();
        for(int row = 0; row < 40; row++) { 
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    tileColors[row][col] = Color.GRAY;
                    objects[row][col] = "";
                    water[row][col] = terrain[row][col] <= 0;
                    /**
                    if(water[row][col] && Math.random() < 0.1) {
                        Battleship b;
                        if(Math.random() < 0.5) b = new Battleship(1 + (int)(3*Math.random()),Color.RED);
                        else b = new Battleship(1 + (int)(3*Math.random()),Color.GREEN);
                        b.captain = new Soldier(1,1);
                        objects[row][col] = b;
                    }
                    */
                }
            }
        }
        
        addColor(Color.GREEN);
        addColor(Color.RED);
        addColor(Color.YELLOW);
        addColor(Color.MAGENTA);
        addColor(ORANGE);
        addColor(Color.CYAN);
    }
     
    public void addColor(Color myColor) {
        int counter = 0;
        double spacing = 5;
        while(counter < 10000) {
        	boolean okay = true;
        	spacing -= 0.1;
            int row = 2+(int)(36*Math.random());
            int col = 2+(int)(36*Math.random());
            if((row+col) % 2 == 0) {
                //Spacing (not working) <
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if((r+c) % 2 == 0) {
                            if((tileColors[r][c].equals(Color.GREEN) || tileColors[r][c].equals(Color.RED) || tileColors[r][c].equals(Color.YELLOW) || tileColors[r][c].equals(Color.MAGENTA) || tileColors[r][c].equals(Color.CYAN) || tileColors[r][c].equals(ORANGE))
                                    && distance(row, col, r, c) < spacing) okay = false;
                        }
                    }
                }
                if(okay) {
	                if(tileColors[row][col].equals(Color.GRAY) && !water[row][col]) {
	                    ArrayList<int[]> neighbors = neighbors(row,col);
	                    for(int[] tile : neighbors) {
	                        int r = tile[0];
	                        int c = tile[1];
	                        if(tileColors[r][c].equals(Color.GRAY)) {
	                            tileColors[r][c] = myColor;
	                            water[r][c] = false;
	                        }
	                    }
	                    objects[row][col] = "tower1";
	                    return;
	                }
                }
            }
            counter++;
        }
    }
    
    public int[][] randomTerrain() {
        int[][] terrain = new int[40][40];
        Random rand = new Random();
        for(int row = 0; row < 40; row++) { 
            for(int col = 0; col < 40; col++) {
                terrain[row][col] = 0;
            }
        }
        for(int i = 0; i < 10; i++) {
            int row = rand.nextInt(20) + 10;
            int col = rand.nextInt(20) + 10;
            for(int r = 0; r < 40; r++) { 
                for(int c = 0; c < 40; c++) {
                    if(Map.distance(r,c,row,col) <= 5) terrain[r][c]++;
                }
            }
        }
        for(int i = 0; i < 5; i++) {
            int row = rand.nextInt(20) + 10;
            int col = rand.nextInt(20) + 10;
            for(int r = 0; r < 40; r++) { 
                for(int c = 0; c < 40; c++) {
                    if(Map.distance(r,c,row,col) <= 5) terrain[r][c]--;
                }
            }
        }
        for(int i = 0; i < 50; i++) {
            int row = rand.nextInt(20) + 10;
            int col = rand.nextInt(40);
            for(int r = 0; r < 40; r++) { 
                for(int c = 0; c < 40; c++) {
                    if(Map.distance(r,c,row,col) <= 1) terrain[r][c] += 2*rand.nextInt(2) - 1;
                }
            }
        }
        for(int i = 0; i < 50; i++) {
            int row = rand.nextInt(20) + 10;
            int col = rand.nextInt(40);
            terrain[row][col] += 2*rand.nextInt(2) - 1;
        }
        return terrain;
    }
    
    public void draw(Graphics g, int xOff, int yOff, int selectedRow, int selectedCol) {
        for(int row = 0; row < 40; row++) { 
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    //Draw hexagon
                    Polygon hex = defineHexagon(row, col, xOff, yOff, 0);
                    if(water[row][col]) g.setColor(Color.BLUE);
                    else g.setColor(tileColors[row][col]);
                    g.fillPolygon(hex);
                    g.setColor(Color.BLACK);
                    g.drawPolygon(hex);
                    Polygon hex2 = defineHexagon(row, col, xOff, yOff, 1);
                    if(row == selectedRow && col == selectedCol) g.drawPolygon(hex2);
                    if(objects[row][col] instanceof Soldier) {
                        Soldier s = (Soldier)(objects[row][col]);
                        s.draw(g, row, col, xOff, yOff);
                    }
                    
                    if(objects[row][col] instanceof Battleship) {
                        Battleship s = (Battleship)(objects[row][col]);
                        s.draw(g, row, col, xOff, yOff);
                    }
                    
                    if(objects[row][col].equals("farm")) {
                        drawFarm(g, row, col, xOff, yOff);
                    }
                    if(objects[row][col] instanceof String) {
                        String str = (String)(objects[row][col]);
                        if(str.length() > 5 && str.substring(0,5).equals("tower")) {
                            int level = Integer.parseInt(str.substring(5,6));
                            drawTower(g, row, col, xOff, yOff, level);
                        }
                    }
                }
            }
        }
    }
    
    public int farmPrice(Color myColor) {
        int price = 12;
        for(int row = 0; row < 40; row++) { 
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    if(objects[row][col].equals("farm") && tileColors[row][col].equals(myColor)) price += 2;
                }
            }
        }
        return price;
    }
    
    //Kills all soldiers of a specified color
    public void starve(Color myColor) {
        for(int row = 0; row < 40; row++) { 
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    if(tileColors[row][col].equals(myColor) && objects[row][col] instanceof Soldier) objects[row][col] = "";
                    if(objects[row][col] instanceof Battleship) {
                        if(((Battleship)objects[row][col]).thisColor.equals(myColor)) {
                            objects[row][col] = "";
                        }
                    }
                }
            }
         }    
    }
    
    public void drawFarm(Graphics g, int row, int col, int xOff, int yOff) {
        g.setColor(Color.WHITE);
        g.fillRect(xOff + 24*col - 4, yOff + 14*row - 4, 9, 9);
        g.setColor(Color.BLACK);
    }
    
    public void drawTower(Graphics g, int row, int col, int xOff, int yOff, int level) {
        g.setColor(Color.BLACK);
        g.fillRect(xOff + 24*col - 2, yOff + 14*row - 6, 5, 13);
        if(level > 1) {
            if(level == 2) g.setColor(Color.BLUE);
            if(level == 3) g.setColor(Color.RED);
            g.fillRect(xOff + 24*col - 1, yOff + 14*row - 3, 3, 3);
            g.setColor(Color.BLACK);
        }
    }
    
    public static Polygon defineHexagon(int row, int col, int xOff, int yOff, int inset) {
        int[] xCoords = new int[6];
        int[] yCoords = new int[6];
        xCoords[0] = xOff + 24*col - 16 + inset;
        xCoords[1] = xOff + 24*col - 8 + inset/2;
        xCoords[2] = xOff + 24*col + 8 - inset/2;
        xCoords[3] = xOff + 24*col + 16 - inset;
        xCoords[4] = xOff + 24*col + 8 - inset/2;
        xCoords[5] = xOff + 24*col - 8 + inset/2;
        yCoords[0] = yOff + 14*row;
        yCoords[1] = yOff + 14*row - 14 + inset;
        yCoords[2] = yOff + 14*row - 14 + inset;
        yCoords[3] = yOff + 14*row;
        yCoords[4] = yOff + 14*row + 14 - inset;
        yCoords[5] = yOff + 14*row + 14 - inset;
        return new Polygon(xCoords, yCoords, 6);
    }
    
    //Returns the distance between two coordinate pairs on the hexagonal grid
    public static int distance(int r1, int c1, int r2, int c2) {
        int rowDiff = Math.abs(r1 - r2);
        int colDiff = Math.abs(c1 - c2);
        if(rowDiff > colDiff) return (rowDiff + colDiff)/2;
        else return colDiff;
    }
    
    //Returns the defense value of a tile
    public int getDefense(int row, int col) {
        int defense = 0;
        ArrayList<int[]> neighbors = neighbors(row, col);
        Color thisColor = tileColors[row][col];
        for(int[] tile : neighbors) {
            int r = tile[0];
            int c = tile[1];
            if(tileColors[r][c].equals(thisColor)) {
                Object obj = objects[r][c];
                if(obj instanceof Soldier) {
                    int level = ((Soldier)(obj)).level;
                    if(level > 3) level--;
                    if(level > defense) defense = level;
                }
                if(obj instanceof String) {
                    String str = (String)(obj);
                    if(str.length() > 5) {
                        if(str.substring(0,5).equals("tower")) {
                            int level = Integer.parseInt(str.substring(5,6));
                            if(level + 1 > defense) defense = level + 1;
                        }
                    }
                }
            }
        }
        return defense;
    }
    
    public boolean besideColor(int row, int col, Color c) {
        ArrayList<int[]> neighbors = neighbors(row, col);
        for(int[] tile : neighbors) {
            if(tileColors[tile[0]][tile[1]].equals(c) && water[tile[0]][tile[1]] == false) return true;
        }
        return false;
    }
    
    //True if the tile given is beside a hostile color
    public boolean besideOpponent(int row, int col, Color c) {
        ArrayList<int[]> neighbors = neighbors(row, col);
        for(int[] tile : neighbors) {
            if(!tileColors[tile[0]][tile[1]].equals(c) && !tileColors[tile[0]][tile[1]].equals(Color.GRAY) && !water[tile[0]][tile[1]]) return true;
        }
        return false;
    }
    
    //Returns an arraylist of coordinate pairs representing a hexagon's valid neighbors
    public static ArrayList<int[]> neighbors(int row, int col) {
        ArrayList<int[]> tiles = new ArrayList<int[]>();
        if(isValid(row, col)) {
            int[] tile = new int[2];
            tile[0] = row;
            tile[1] = col;
            tiles.add(tile);
        }
        if(isValid(row - 2, col)) {
            int[] tile = new int[2];
            tile[0] = row - 2;
            tile[1] = col;
            tiles.add(tile);
        }
        if(isValid(row - 1, col + 1)) {
            int[] tile = new int[2];
            tile[0] = row - 1;
            tile[1] = col + 1;
            tiles.add(tile);
        }
        if(isValid(row + 1, col + 1)) {
            int[] tile = new int[2];
            tile[0] = row + 1;
            tile[1] = col + 1;
            tiles.add(tile);
        }
        if(isValid(row + 2, col)) {
            int[] tile = new int[2];
            tile[0] = row + 2;
            tile[1] = col;
            tiles.add(tile);
        }
        if(isValid(row + 1, col - 1)) {
            int[] tile = new int[2];
            tile[0] = row + 1;
            tile[1] = col - 1;
            tiles.add(tile);
        }
        if(isValid(row - 1, col - 1)) {
            int[] tile = new int[2];
            tile[0] = row - 1;
            tile[1] = col - 1;
            tiles.add(tile);
        }
        return tiles;
    }
    
    public static boolean isValid(int row, int col) {
        return 0 <= row && row < 40 && 0 <= col && col < 40 && (row+col)%2 == 0;
    }
    
    //Returns the income of a given player
    public int getIncome(Color myColor) {
        int income = 0;
        for(int row = 0; row < 40; row++) {
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    if(objects[row][col] instanceof Battleship) {
                        Battleship ship = (Battleship) objects[row][col];
                        if(ship.thisColor.equals(myColor)) {
                            if(ship.level == 1) income -= 2;
                            if(ship.level == 2) income -= 4;
                            if(ship.level == 3) income -= 8;
                            if(ship.level == 4) income -= 12;
                            if(ship.level == 5) income -= 18;
                            if(ship.captain != null) {
                                int level = ((Soldier)(ship.captain)).level;
                                if(level == 1) income -= 2;
                                if(level == 2) income -= 6;
                                if(level == 3) income -= 18;
                                if(level == 4) income -= 36;
                                if(level == 5) income -= 72;
                            }
                        }
                    }
                    if(tileColors[row][col].equals(myColor) && water[row][col] == false) {
                        income++;
                        if(objects[row][col] instanceof Soldier) {
                            int level = ((Soldier)(objects[row][col])).level;
                            if(level == 1) income -= 2;
                            if(level == 2) income -= 6;
                            if(level == 3) income -= 18;
                            if(level == 4) income -= 36;
                            if(level == 5) income -= 72;
                        }
                        if(objects[row][col].equals("farm")) income += 4;
                        if(objects[row][col].equals("tower1")) income -= 1;
                        if(objects[row][col].equals("tower2")) income -= 6;
                        if(objects[row][col].equals("tower3")) income -= 15;
                    }
                }
            }
        }
        return income;
    }
    
    public String pack() {
        String str = "MAP";
        for(int row = 0; row < 40; row++) {
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    String isWater = "l"; //Land
                    if(water[row][col]) isWater = "w";

                    String packedColor = packColor(tileColors[row][col]);
                    
                    String thing = objects[row][col].toString(); //Soldier, farm, tower, etc.
                    str = str + isWater + packedColor + "." + objects[row][col].toString() + ",";
                }
            }
        }
        return str;
    }
    
    public void unpack(String s) {
        objects = new Object[40][40];
        tileColors = new Color[40][40];
        water = new boolean[40][40];
        int comma1 = 2;
        int comma2 = s.indexOf(",");
        for(int row = 0; row < 40; row++) {
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0) {
                    //Get the substring corresponding to this location
                    String location = s.substring(comma1 + 1, comma2);
                    comma1 = comma2;
                    comma2 = s.indexOf(",", comma2 + 1);
                    
                    //Water or land?
                    if(location.substring(0,1).equals("w")) water[row][col] = true;
                    else water[row][col] = false;
                    
                    //Color
                    tileColors[row][col] = Color.GRAY;
                    if(location.length() > 1) {
                        if(location.substring(1,2).equals("g")) tileColors[row][col] = Color.GREEN;
                        if(location.substring(1,2).equals("r")) tileColors[row][col] = Color.RED;
                        if(location.substring(1,2).equals("y")) tileColors[row][col] = Color.YELLOW;
                        if(location.substring(1,2).equals("m")) tileColors[row][col] = Color.MAGENTA;
                        if(location.substring(1,2).equals("o")) tileColors[row][col] = ORANGE;
                        if(location.substring(1,2).equals("c")) tileColors[row][col] = Color.CYAN;
                    }
                    
                    //Object
                    String objString = location.substring(location.indexOf(".") + 1, location.length());
                    if(objString.length() > 3) {
                        if(objString.substring(0,3).equals("SOL")) {
                            int level = Integer.parseInt(objString.substring(3,4));
                            int moveTimer = Integer.parseInt(objString.substring(4,objString.length()));
                            objects[row][col] = new Soldier(level, moveTimer);
                        } else if(objString.substring(0,3).equals("BSP")) {
                            int level = Integer.parseInt(objString.substring(3,4));
                            int solLevel = -1;
                            int moveTimer = -1;
                            if(objString.contains("/")) {
                                String sol = objString.substring(5,objString.indexOf(":"));
                                solLevel = Integer.parseInt(sol.substring(3,4));
                                moveTimer = Integer.parseInt(sol.substring(4,sol.length()));
                            }
                            Soldier troop = null;
                            if(solLevel > 0 && moveTimer != -1) troop = new Soldier(solLevel, moveTimer); 
                            String color = objString.substring(objString.indexOf(":") + 1, objString.indexOf(":") + 2);
                            Color c = unpackColor(color);
                            Battleship battle = new Battleship(level, c);
                            if(troop != null) battle.captain = troop;
                            objects[row][col] = battle;
                        } else objects[row][col] = objString;
                    } else objects[row][col] = objString;
                }
            }
        }
    }
    
    //Packs a color into a string
    public static String packColor(Color c) {
        if(c.equals(Color.GREEN)) return "g";
        if(c.equals(Color.MAGENTA)) return "m";
        if(c.equals(Color.RED)) return "r";
        if(c.equals(Color.YELLOW)) return "y";
        if(c.equals(ORANGE)) return "o";
        if(c.equals(Color.CYAN)) return "c";
        return "";
    }
    
   //Packs a color into a string
   public static Color unpackColor(String s) {
        if(s.equals("g")) return Color.GREEN;
        if(s.equals("r")) return Color.RED;
        if(s.equals("y")) return Color.YELLOW;
        if(s.equals("m")) return Color.MAGENTA;
        if(s.equals("o")) return ORANGE;
        if(s.equals("c")) return Color.CYAN;
        return Color.GRAY;
    }
}

