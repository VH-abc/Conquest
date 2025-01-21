/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Objects;

import java.awt.Color;

/**
 * A generic AI for various AI strategies to implement
 * @author 20hebbarv
 */
public interface Bot {
    public void update();
    public Color getColor();
    public int getMoney();
    public void makeMove();
}
