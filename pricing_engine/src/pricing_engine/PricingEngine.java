/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import java.io.File;
import jess.*;

import java.util.Iterator;

public class PricingEngine {
    private Rete engine;
    private WorkingMemoryMarker marker;
    private Database database;

    public PricingEngine(Database aDatabase,String file) throws JessException {
        // Create a Jess rule engine
        engine = new Rete();
        engine.reset();

        // Load the pricing rules
        engine.batch(file);

        // Load the catalog data into working memory
        database = aDatabase;
        engine.addAll(database.getCatalogItems());

        // Mark end of catalog data for later
        marker = engine.mark();
    }

    private void loadOrderData(int orderNumber) throws JessException {
        // Retrive the order from the database
    	Order order = database.getOrder(orderNumber);
        
    	if (order != null) {
    		// Add the order and its contents to working memory
    		engine.add(order);
    		engine.add(order.getCustomer());
    		engine.addAll(order.getItems());
    	}
    }

    public Iterator run(int orderNumber) throws JessException {
        // Remove any previous order data, leaving only catalog data
    	engine.resetToMark(marker);
    	
    	// Load data for this order
        loadOrderData(orderNumber);
        
        // Fire the rules that apply to this order
        engine.run();
        
        // Return the list of offers created by the rules
        return engine.getObjects(new Filter.ByClass(Offer.class));
    }
}