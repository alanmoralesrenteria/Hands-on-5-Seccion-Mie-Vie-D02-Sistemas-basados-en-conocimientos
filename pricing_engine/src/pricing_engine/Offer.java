/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import java.text.DecimalFormat;
import java.util.Objects;

public class Offer {
    private final float amount;
    private final String description;
    private static DecimalFormat formatter = new DecimalFormat("-$0.00");
    
    private String tarjeta = null;
    private Boolean ofertarvales = false;
    private int type;
    
    // type = 0     solo se ingresa la descripcion de la oferta, 
    // type = 1     descripcion + monto
    // type = 2     vales
    // type = 3     description + item + amount
    // type = 4     tarjeta
    
    public Offer(String aDescription) {
	description = aDescription;
	amount = 0;
        type = 0;
    }
    
    public Offer(String aDescription, float anAmount) {
	description = aDescription;
	amount = anAmount;
        type = 1;
    }
    
    public Offer(String aDescription,float anAmount, String eVales) {
	description = aDescription;
	amount = anAmount;
        if(Objects.equals("true", eVales)){
            ofertarvales = true;
        }
        type = 2;
        
    }
    
    
    public Offer(String aDescription,String itemDescription, float anAmount) {
	description = aDescription + itemDescription;
	amount = anAmount;
        type = 3;
    }
    
    //sobrecarga para descuento con iPhone
    public Offer(String aDescription, String eTarjeta) {
	description = aDescription;
	amount = 0;
        tarjeta = eTarjeta;
        
        type = 4;
    }
    
    public float getAmount() {
	return amount;
    }

    public String getDescription() {
	return description;
    }

    public String toString() {
        
        switch(type){
            case 0:{
                return description;
            }
            
            case 1:{
                return description + ": " + formatter.format(amount);
            }
            
            case 2:{
                return description + ", actualmente obtienes: " + Math.round(amount / 1000) + " vales de 100 pesos";
            }
            
            case 3:{
                return description + ": " + formatter.format(amount);
            }
            
            case 4 : {
                return description;
            }
        }
        
        return "Error, Oferta no valida (?)";
    }
	
	
}