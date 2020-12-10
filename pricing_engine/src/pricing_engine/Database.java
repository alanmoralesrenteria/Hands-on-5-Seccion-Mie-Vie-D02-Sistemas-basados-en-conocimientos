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

import java.util.Collection;

public interface Database {
	public Collection getCatalogItems();
	public Order getOrder(int orderNumber);
        public MySqlConnection connection();
}