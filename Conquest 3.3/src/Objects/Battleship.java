/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Polygon;

/**
 *
 * @author 20hebbarv
 */
public class Battleship extends Ship {
    public Soldier captain;
    public int level;
    public Color thisColor;
    public Battleship(int a, Color c) {
        level = a;
        thisColor = c;
    }
    public String toString() {
       if(captain != null) return "BSP" + level + "/" + captain.toString() + ":" + Map.packColor(thisColor);
       return "BSP" + level + ":" + Map.packColor(thisColor);
    }
    public void draw(Graphics g, int row, int col, int xOff, int yOff) {
        //Ship
        g.setColor(new Color(150,100,50));
        g.fillPolygon(defineTrapezoid(row, col, xOff, yOff, 2));
        g.setColor(thisColor);
        g.fillRect(xOff + 24*col - 7, yOff + 14*row - 12, 15, 10);
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 11));
        g.drawString(level + "", xOff + 24*col - 2, yOff + 14*row - 3);
        
        //Occupant
        if(captain != null) captain.draw(g, row, col, xOff, yOff + 5);
    }
    public static Polygon defineTrapezoid(int row, int col, int xOff, int yOff, int inset) {
        int[] xCoords = new int[4];
        int[] yCoords = new int[4];
        xCoords[0] = xOff + 24*col - 15 + inset;
        xCoords[1] = xOff + 24*col + 16 - inset;
        xCoords[2] = xOff + 24*col + 8 - inset/2;
        xCoords[3] = xOff + 24*col - 7 + inset/2;
        yCoords[0] = yOff + 14*row;
        yCoords[1] = yOff + 14*row;
        yCoords[2] = yOff + 14*row + 14 - inset;
        yCoords[3] = yOff + 14*row + 14 - inset;
        return new Polygon(xCoords, yCoords, 4);
    }
}
