/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

public class Customer {	
    private int orderCount;
    
    public Customer(int anOrderCount) {
	orderCount = anOrderCount;
    }	
    
    public int getOrderCount() {
	return orderCount;
    }
}