/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A toy implementation of the Database interface with some
 * hard-coded order data.
 */

public class DemoDatabase implements Database {

	private ArrayList items;
	private Map orders;

	public DemoDatabase(String nameCatalogItems) {
                //unicamente obtiene los items que corresponden a su catalogo
		createCatalogItems(nameCatalogItems);
                // Se comenta la linea de abajo para no hardcodear las ordenes
                // createOrders();
	}
        
        public DemoDatabase(ArrayList eItems) {
                items = eItems;
	}
        
        public DemoDatabase() {
                
	}
        
	public void createOrder(int customerAgent,int orderNumber,ArrayList orderItems, String tarjeta) {
		orders = new HashMap();
		
		Customer customer = new Customer(customerAgent);
                
		//orderItems.add(new OrderItem("CD Writer", 1234, 199.99f, 1));
		//orderItems.add(new OrderItem("AA Batteries", 4323, 4.99f, 2));
		
                orders.put(new Integer(orderNumber), new Order(orderItems, customer, tarjeta));
		
	}

	private void createCatalogItems(String nameCatalogItems) {
            
            
            // creamos el catalogo de items en base a lo que hay en la base de datos
            // y el idCatlogItems de el agente
            items = connection().getcatalogitemsfrombd(nameCatalogItems);
            
            //si no hay itesm en la bd, inicializa la bd y recarga los items
            if(items == null){
                connection().inicializarbd();
                items = connection().getcatalogitemsfrombd(nameCatalogItems);
            }
            
           
            
	}

	public Collection getCatalogItems() {
		return items;
	}

	public Order getOrder(int orderNumber) {
		return (Order) orders.get(new Integer(orderNumber));
	}

        public MySqlConnection connection() {
            MySqlConnection conection = new MySqlConnection();
            return conection;
        }
}
