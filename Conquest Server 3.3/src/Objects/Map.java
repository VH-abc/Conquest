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
    public Wall[][][] walls = new Wall[40][40][6]; //Third index is which side of the tile it is on
    public boolean[][] water = new boolean[40][40];
    public static final Color ORANGE = new Color(255,128,0);
    public String mode = "small"; //"small", "island", or "continent" (default)
    
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
        /**
        for(int row = 0; row < 40; row++) { 
            for(int col = 0; col < 40; col++) {
                if((row+col) % 2 == 0 && !water[row][col] && !tileColors[row][col].equals(Color.GRAY)) {
                    walls[row][col][(int)(6*Math.random())] = new Wall((int)(3*Math.random()), Color.GREEN);
                }
            }
        } */
    }
     
    public void addColor(Color myColor) {
        for(int distance = 10; distance > 0; distance--) {
            int counter = 0;
            while(counter < 100000) {
                int row = (int)(36*Math.random() + 2);
                int col = (int)(38*Math.random() + 1);
                boolean okay = true;
                if((row+col) % 2 == 0) {
                    for(int r = 0; r < 40; r++) { 
                        for(int c = 0; c < 40; c++) {
                            if((r+c) % 2 == 0) {
                                if((tileColors[r][c].equals(Color.GREEN) || tileColors[r][c].equals(Color.RED) || tileColors[r][c].equals(Color.YELLOW) || tileColors[r][c].equals(Color.MAGENTA) || tileColors[r][c].equals(Map.ORANGE) || tileColors[r][c].equals(Color.CYAN))
                                        && distance(row, col, r, c) < distance) okay = false;
                            }
                        }
                    }
                    if(tileColors[row][col].equals(Color.GRAY) && !water[row][col] && okay) {
                        ArrayList<int[]> neighbors = neighbors(row,col);
                        for(int[] tile : neighbors) {
                            int r = tile[0];
                            int c = tile[1];
                            if(tileColors[r][c].equals(Color.GRAY)) {
                                tileColors[r][c] = myColor;
                                water[r][c] = false;
                            }
                        }
                        objects[row][col] = "capital1";
                        return;
                    }
                }
                counter++;
            }
        }
    }
    
    public int[][] randomTerrain() {
        if(mode.equals("small")) {
            int[][] terrain = new int[40][40];
            Random rand = new Random();
            for(int row = 0; row < 40; row++) { 
                for(int col = 0; col < 40; col++) {
                    terrain[row][col] = 0;
                }
            }
            for(int i = 0; i < 10; i++) {
                int row = rand.nextInt(15) + 13;
                int col = rand.nextInt(15) + 13;
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if(Map.distance(r,c,row,col) <= 4) terrain[r][c]++;
                    }
                }
            }
            for(int i = 0; i < 5; i++) {
                int row = rand.nextInt(15) + 13;
                int col = rand.nextInt(15) + 13;
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if(Map.distance(r,c,row,col) <= 4) terrain[r][c]--;
                    }
                }
            }
            for(int i = 0; i < 25; i++) {
                int row = rand.nextInt(15) + 13;
                int col = rand.nextInt(30) + 5;
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if(Map.distance(r,c,row,col) <= 1) terrain[r][c] += 2*rand.nextInt(2) - 1;
                    }
                }
            }
            for(int i = 0; i < 25; i++) {
                int row = rand.nextInt(15) + 13;
                int col = rand.nextInt(30) + 5;
                terrain[row][col] += 2*rand.nextInt(2) - 1;
            }
            return terrain;
        }
        else if(mode.equals("island")) {
            int[][] terrain = new int[40][40];
            Random rand = new Random();
            //Initialize
            for(int row = 0; row < 40; row++) { 
                for(int col = 0; col < 40; col++) {
                    terrain[row][col] = 0;
                }
            }
            //Islands
            for(int i = 0; i < 100; i++) {
                int row = rand.nextInt(40);
                int col = rand.nextInt(40);
                if(row < 10 || row > 30 || col < 10 || col > 30) {
                    if(terrain[row][col] <= 0) {
                        boolean nope = false;
                        for(int r = 0; r < 40; r++) { 
                            for(int c = 0; c < 40; c++) {
                                if(Map.distance(r,c,row,col) <= 3 && terrain[r][c] > 0) nope = true;
                            }
                        }
                        if(!nope) {
                            for(int r = 0; r < 40; r++) { 
                                for(int c = 0; c < 40; c++) {
                                    if(Map.distance(r,c,row,col) <= 1) terrain[r][c]+=2;
                                    if(Map.distance(r,c,row,col) == 2 && Math.random() < 0.25) terrain[r][c]+=2;
                                }
                            }
                        }
                    }
                }
            }
            /**
            //Large bodies of water
            for(int i = 0; i < 7; i++) {
                int row = rand.nextInt(20) + 10;
                int col = rand.nextInt(20) + 10;
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if(Map.distance(r,c,row,col) <= 5) terrain[r][c]--;
                    }
                }
            }
            //Small islands
            for(int i = 0; i < 30; i++) {
                int row = rand.nextInt(20) + 10;
                int col = rand.nextInt(20) + 10;
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if(Map.distance(r,c,row,col) <= 2) terrain[r][c] += 2*rand.nextInt(2) - 1;
                    }
                }
            } */
            //Small terrain fluctuations
            /**
            for(int i = 0; i < 10; i++) {
                int row = rand.nextInt(20) + 10;
                int col = rand.nextInt(40);
                for(int r = 0; r < 40; r++) { 
                    for(int c = 0; c < 40; c++) {
                        if(Map.distance(r,c,row,col) <= 1) terrain[r][c] += 2*rand.nextInt(2) - 1;
                    }
                }
            } 
            */
            //Micro islands
            for(int i = 0; i < 20; i++) {
                int row = rand.nextInt(40);
                int col = rand.nextInt(40);
                terrain[row][col] += 1;
            }
            return terrain;
        } else {
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
                        if(str.startsWith("capital")) {
                            drawCapital(g,row,col,xOff,yOff,str);
                        }
                    }
                    for(int i = 0; i < 6; i++) {
                        if(walls[row][col][i] != null) walls[row][col][i].draw(g,row,col,i,xOff,yOff);
                    }
                    if(walls[row][col][0] != null) {
                        Wall wall = (Wall)walls[row][col][0];
                        if(row <= 1) wall.myColor = tileColors[row][col];
                        else if(!wall.myColor.equals(tileColors[row][col]) && !wall.myColor.equals(tileColors[row-2][col])) wall.myColor = tileColors[row][col];
                    }
                    if(walls[row][col][1] != null) {
                        Wall wall = (Wall)walls[row][col][1];
                        if(row == 0 || col == 39) wall.myColor = tileColors[row][col];
                        else if(!wall.myColor.equals(tileColors[row][col]) && !wall.myColor.equals(tileColors[row-1][col+1])) wall.myColor = tileColors[row][col];
                    }
                    if(walls[row][col][2] != null) {
                        Wall wall = (Wall)walls[row][col][2];
                        if(row == 39 || col == 39) wall.myColor = tileColors[row][col];
                        else if(!wall.myColor.equals(tileColors[row][col]) && !wall.myColor.equals(tileColors[row+1][col+1])) wall.myColor = tileColors[row][col];
                    }
                    if(walls[row][col][3] != null) {
                        Wall wall = (Wall)walls[row][col][3];
                        if(row >= 38) wall.myColor = tileColors[row][col];
                        else if(!wall.myColor.equals(tileColors[row][col]) && !wall.myColor.equals(tileColors[row+2][col])) wall.myColor = tileColors[row][col];
                    }
                    if(walls[row][col][4] != null) {
                        Wall wall = (Wall)walls[row][col][4];
                        if(row == 39 || col == 0) wall.myColor = tileColors[row][col];
                        else if(!wall.myColor.equals(tileColors[row][col]) && !wall.myColor.equals(tileColors[row+1][col-1])) wall.myColor = tileColors[row][col];
                    }
                    if(walls[row][col][5] != null) {
                        Wall wall = (Wall)walls[row][col][5];
                        if(row == 0 || col == 0) wall.myColor = tileColors[row][col];
                        else if(!wall.myColor.equals(tileColors[row][col]) && !wall.myColor.equals(tileColors[row-1][col-1])) wall.myColor = tileColors[row][col];
                    }
                }
            }
        }
        g.setColor(Color.BLACK);
        if(0 <= selectedRow && selectedRow < 40 && 0 <= selectedCol && selectedCol < 40) g.drawPolygon(defineHexagon(selectedRow, selectedCol, xOff, yOff, 1));
    }
    
    //Used by EasyBot to assess the required defense level of a border tile based on enemy troops within 4 tiles distance
    public int requiredDefense(int row, int col, Color myColor) {
        int defense = 0;
        for(int r = row - 8; r < row + 8; r++) {
            for(int c = col - 8; c < col + 8; c++) {
                if(0 <= r && r < 40 && 0 <= c && c < 40 && (r+c)%2 == 0) {
                    if(Map.distance(row, col, r, c) <= 4) {
                        if(!tileColors[r][c].equals(myColor)) {
                            if(objects[r][c] instanceof Soldier) {
                                Soldier sol = (Soldier) objects[r][c];
                                if(sol.level > defense) defense = sol.level;
                            }
                        }
                    }
                }
            }
        }
        return defense;
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
    
     public void drawCapital(Graphics g, int row, int col, int xOff, int yOff, String str) {
        int level = Integer.parseInt(str.substring(7,8));
        g.setColor(Color.BLACK);
        g.fillRect(xOff + 24*col - 2, yOff + 14*row - 6, 5, 13);
        g.setColor(Color.WHITE);
        g.fillRect(xOff + 24*col - 1, yOff + 14*row - 3, 3, 3);
        if(level == 2) g.setColor(Color.BLUE);
        if(level == 3) g.setColor(Color.RED);
        g.fillRect(xOff + 24*col, yOff + 14*row - 2, 2, 2);
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
                    if(str.startsWith("capital")) {
                        int level = Integer.parseInt(str.substring(7,8));
                        if(level + 1 > defense) defense = level + 1;
                    }
                }
            }
        }
        return defense;
    }
    
    /*
    //Return tiles reachable by a level x soldier within a distance 
    public ArrayList<int[]> reachableTiles(int row, int col, int level, int distance) {
        ArrayList<int[]> tiles = new ArrayList<int[]>();
    }
    */
    
    public boolean besideColor(int row, int col, Color c) {
        ArrayList<int[]> neighbors = neighbors(row, col);
        for(int[] tile : neighbors) {
            if(tileColors[tile[0]][tile[1]].equals(c) && water[tile[0]][tile[1]] == false) {
                return true;
            }
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
    
    //Returns an arraylist of coordinate pairs representing a hexagon's valid neighbors
    public boolean besideColorIncludeWalls(int row, int col, Color myColor, int level) {
        if(isValid(row, col)) {
            if(tileColors[row][col].equals(myColor)) return true;
        }
        if(isValid(row - 2, col)) {
            if(tileColors[row - 2][col].equals(myColor) && crossable(walls[row - 2][col][3], myColor, level) && crossable(walls[row][col][0], myColor, level)) return true;
        }
        if(isValid(row - 1, col + 1)) {
            if(tileColors[row - 1][col + 1].equals(myColor) && crossable(walls[row - 1][col + 1][4], myColor, level) && crossable(walls[row][col][1], myColor, level)) return true;
        }
        if(isValid(row + 1, col + 1)) {
            if(tileColors[row + 1][col + 1].equals(myColor) && crossable(walls[row + 1][col + 1][5], myColor, level) && crossable(walls[row][col][2], myColor, level)) return true;
        }
        if(isValid(row + 2, col)) {
            if(tileColors[row + 2][col].equals(myColor) && crossable(walls[row + 2][col][0], myColor, level) && crossable(walls[row][col][3], myColor, level)) return true;
        }
        if(isValid(row + 1, col - 1)) {
            if(tileColors[row + 1][col - 1].equals(myColor) && crossable(walls[row + 1][col - 1][1], myColor, level) && crossable(walls[row][col][4], myColor, level)) return true;
        }
        if(isValid(row - 1, col - 1)) {
          if(tileColors[row - 1][col - 1].equals(myColor) && crossable(walls[row - 1][col - 1][2], myColor, level) && crossable(walls[row][col][5], myColor, level)) return true;
        }
        return false;
    }
    
    public boolean crossable(Wall wall, Color c, int level) {
        return wall == null || wall.myColor.equals(c) || wall.level + 1 < level;
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
                        if(objects[row][col] instanceof String) {
                            String str = (String)objects[row][col];
                            if(str.startsWith("capital")) income += 8;
                        }
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
                    String wall = "";
                    for(int side = 0; side < 6; side++) {
                        if(walls[row][col][side] != null) {
                            wall += walls[row][col][side].toString();
                            wall += side;
                        }
                    }
                    str = str + isWater + packedColor + "." + objects[row][col].toString() + "!" + wall + ",";
                }
            }
        }
        return str;
    }
    
    public void unpack(String s) {
        objects = new Object[40][40];
        tileColors = new Color[40][40];
        water = new boolean[40][40];
        walls = new Wall[40][40][6];
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
                    String objString = location.substring(location.indexOf(".") + 1, location.indexOf("!"));
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
                    
                    //Walls
                    String wallString = location.substring(location.indexOf("!") + 1);
                    for(int i = 0; i < wallString.length(); i += 7) {
                        //Unpack this wall
                        Color c = unpackColor(wallString.substring(i+4,i+5));
                        int level = Integer.parseInt(wallString.substring(i+5,i+6));
                        int side = Integer.parseInt(wallString.substring(i+6,i+7));
                        walls[row][col][side] = new Wall(level,c);
                    }
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

