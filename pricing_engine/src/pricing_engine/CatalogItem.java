/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;


public class CatalogItem {
    private String description;
    private int partNumber;
    private float price;

    public CatalogItem(String aDescription, int aPartNumber, float aPrice) {
	description = aDescription;
	partNumber = aPartNumber;
	price = aPrice;
    }

    public int getPartNumber() {
	return partNumber;
    }
	
    public String getDescription() {
	return description;
    }
	
    public float getPrice() {
	return price;
    }
}
