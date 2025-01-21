/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;
import java.awt.Graphics;
/**
 *
 * @author 20hebbarv
 */
public class Soldier {
    public int moveTimer; //Time in frames until next move
    public int level; //Strength
    public Soldier(int lev, int mt) {
        level = lev;
        moveTimer = mt;
    }
    public boolean canMove() {
        return moveTimer == 0;
    }
    //Draws at a particular location, updates move timer
    public void draw(Graphics g, int row, int col, int xOff, int yOff) {
        //Highlight
        g.setColor(Color.BLACK);
        if(canMove()) g.fillOval(xOff + 24*col - 7, yOff + 14*row - 7, 14, 14); 
        //Body
        g.setColor(Color.WHITE);
        g.fillOval(xOff + 24*col - 5, yOff + 14*row - 5, 10, 10);
        
        //Level 2
        if(level == 2) {
            g.setColor(Color.BLACK);
            g.fillRect(xOff + 24*col - 2, yOff + 14*row - 2, 5, 5);
        }
        
        //Level 3
        if(level == 3) {
            g.setColor(Color.RED);
            g.fillRect(xOff + 24*col - 2, yOff + 14*row - 2, 5, 5);
        }
        
        //Level 4 and 5
        if(level > 3) {
            g.setColor(Color.BLUE);
            g.fillRect(xOff + 24*col - 2, yOff + 14*row - 2, 5, 5);
        }
        if(level == 5) {
            g.setColor(Color.RED);
            g.fillRect(xOff + 24*col - 1, yOff + 14*row - 1, 3, 3);
        }
        
        if(moveTimer > 0) moveTimer--;
        g.setColor(Color.BLACK);
    }
    public String toString() {
        return "SOL" + level + "" + moveTimer;
    }
}
