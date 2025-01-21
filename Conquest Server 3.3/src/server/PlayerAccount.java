/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import Objects.Map;
import java.awt.Color;

/**
 * When a player logs on to the server, a PlayerAccount is created. The PlayerAccount keeps track of money.
 * If a player is disconnected, they can use a password to reenter the game. There is no encryption, so do not use an actual password!
 * @author 20hebbarv
 */
public class PlayerAccount {
    /** Login stuff */
    private String password;
    public Color myColor;
    public Map map;
    
    public boolean checkPassword(String s) {
        return s.equals(password);
    }
    
    public PlayerAccount(String s, Color c, Map m) {
        password = s;
        myColor = c;
        map = m;
        money = 10;
        moneyTimer = 0;
    }
    
    /** Economy */
    public int money = 10;
    public double moneyTimer = 0; //When this reaches 1, add 1 to money
    public static final double turnLength = 30; //Number of seconds per turn
    private int farmPrice = 12;
    
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
    }
}
