/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;

/**
 *
 * @author 20hebbarv
 */
public class GuidedShip extends Battleship{
    public Map map;
    public int targetRow;
    public int targetCol;
    public int myRow;
    public int myCol;
    public GuidedShip(int a, Color c, int row, int col, Map m) {
        super(a,c);
        targetRow = row;
        targetCol = col;
        map = m;
    }
    public void makeMove() {
        int record = 10000;
        int bestRow = myRow;
        int bestCol = myCol;
        for(int r = 0; r < 40; r++) {
            for(int c = 0; c < 40; c++) {
                if((r+c) % 2 == 0) {
                    if(Map.distance(myRow, myCol, r, c) <= 4 && map.water[r][c] && map.objects[r][c] == null) {
                        if(Map.distance(r, c, targetRow, targetCol) < record) {
                            bestRow = r;
                            bestCol = c;
                            record = Map.distance(r, c, targetRow, targetCol);
                        } 
                    }
                }
            }
        }
        
    }
}
