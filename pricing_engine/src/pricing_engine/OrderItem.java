/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;


import java.text.DecimalFormat;

public class OrderItem {
    private static DecimalFormat formatter = new DecimalFormat("0.00");	
    private float price;
    private int partNumber;
    private String description;
    private int quantity;
    //private String tarjeta=null;

    public OrderItem(String aDescription, int aPartNumber, float aPrice, int aQuantity) {
	partNumber = aPartNumber;
	description = aDescription;
	price = aPrice;
	quantity = aQuantity;
    }
    
    public OrderItem(String aDescription, int aPartNumber, float aPrice, int aQuantity, String eTarjeta) {
	partNumber = aPartNumber;
	description = aDescription;
	price = aPrice;
	quantity = aQuantity;
      //  tarjeta=eTarjeta;
    }
	
    public int getQuantity() {
	return quantity;
    }
	
    public float getPrice() {
	return price;
    }
	
    public int getPartNumber() {
	return partNumber;
    }

    public float getTotal() {
	return price * quantity;
    }
	
    public String getDescription() {
	return description;
    }
    /*
    public String getTarjeta() {
	return tarjeta;
    }
    */
    public String toString() {
	return quantity + " " + description + ": " + formatter.format(getTotal());
    }
}