/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

/**
 *
 * @author Alan
 */
public class CreditCard {
    
    private final String name;
    private final String type;
    
    public CreditCard(String eName, String eType) {
	name = eName;
        type = eType;
    }
    
    public String getName(){
        return name;
    }
    
    public String getType(){
        return type;
    }
    
}
