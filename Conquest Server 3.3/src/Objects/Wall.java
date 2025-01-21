/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;

/**
 *
 * @author 20hebbarv
 */
public class Wall {
    public Color myColor;
    public int level;
    
    public Wall(int a, Color c) {
        myColor = c;
        level = a;
    }
    
    public void draw(Graphics g, int row, int col, int side, int xOff, int yOff) {
        if(level == 1) g.setColor(new Color(125,75,40));
        if(level == 2) g.setColor(new Color(150,150,150));
        if(level == 3) g.setColor(Color.BLACK);
        g.fillPolygon(defineTrapezoid(row,col,xOff,yOff,side));
        g.setColor(myColor);
        if(side == 0) g.fillRect(24*col + xOff - 1, 14*row + yOff - 14, 3, 5);
        if(side == 1) g.fillPolygon(new int[]{24*col + xOff + 12 ,24*col + xOff + 13, 24*col + xOff + 9, 24*col + xOff + 8}, new int[]{14*row + yOff - 8, 14*row + yOff - 5, 14*row + yOff - 3, 14*row + yOff - 6}, 4);
        if(side == 2) g.fillPolygon(new int[]{24*col + xOff + 12 ,24*col + xOff + 13, 24*col + xOff + 9, 24*col + xOff + 8}, new int[]{14*row + yOff + 8, 14*row + yOff + 5, 14*row + yOff + 3, 14*row + yOff + 6}, 4);
        if(side == 3) g.fillRect(24*col + xOff - 1, 14*row + yOff + 10, 3, 5);
        if(side == 4) g.fillPolygon(new int[]{24*col + xOff - 12 ,24*col + xOff - 13, 24*col + xOff - 9, 24*col + xOff - 8}, new int[]{14*row + yOff + 8, 14*row + yOff + 5, 14*row + yOff + 3, 14*row + yOff + 6}, 4);
        if(side == 5) g.fillPolygon(new int[]{24*col + xOff - 12 ,24*col + xOff - 13, 24*col + xOff - 9, 24*col + xOff - 8}, new int[]{14*row + yOff - 8, 14*row + yOff - 5, 14*row + yOff - 3, 14*row + yOff - 6}, 4);
        g.setColor(Color.BLACK);        
        g.drawPolygon(defineTrapezoid(row,col,xOff,yOff,side)); 
    }
    
    public String toString() {
        return "WALL" + Map.packColor(myColor) + level;
    }
    
    public static Polygon defineTrapezoid(int row, int col, int xOff, int yOff, int side) {
        int[] xCoords = new int[4];
        int[] yCoords = new int[4];
        int[][] outerHex = hexagonCoordinates(row, col, xOff, yOff, 0);
        int[][] innerHex = hexagonCoordinates(row, col, xOff, yOff, 5);
        xCoords[0] = outerHex[0][(side+1)%6];
        yCoords[0] = outerHex[1][(side+1)%6];
        xCoords[1] = outerHex[0][(side+2)%6];
        yCoords[1] = outerHex[1][(side+2)%6];
        xCoords[2] = innerHex[0][(side+2)%6];
        yCoords[2] = innerHex[1][(side+2)%6];
        xCoords[3] = innerHex[0][(side+1)%6];
        yCoords[3] = innerHex[1][(side+1)%6];
        return new Polygon(xCoords, yCoords, 4);
    }
    
    public static int[][] hexagonCoordinates(int row, int col, int xOff, int yOff, int inset) {
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
        int[][] xy = new int[2][6];
        xy[0] = xCoords;
        xy[1] = yCoords;
        return xy;
    }
}
