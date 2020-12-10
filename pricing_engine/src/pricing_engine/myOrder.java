/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import java.util.ArrayList;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Soporte2
 */

// clase auxiliar para crear una orden
public class myOrder {
    protected String sellerName;
    protected String orderNumber;
    protected String tarjeta;
    protected ArrayList<OrderItem> items;
    
    public myOrder(String eOrderNumber , String eSellerName ,ArrayList<OrderItem> eItems){
        orderNumber = eOrderNumber;
        sellerName = eSellerName;
        items = eItems;
        tarjeta = "null";
    }
    
    public myOrder(String eOrderNumber , String eSellerName ,ArrayList<OrderItem> eItems, String eTarjeta){
        orderNumber = eOrderNumber;
        sellerName = eSellerName;
        items = eItems;
        tarjeta = eTarjeta;
        
    }
    
    public String getorderNumber(){return orderNumber;}
    public ArrayList<OrderItem> getItems(){return items;}
    public String getSellerName(){return sellerName;}
    
    public String getJsonOrder() throws JSONException{
        
        JSONArray array = new JSONArray();

        JSONObject jsonSellerAgent = new JSONObject();
        jsonSellerAgent.put("sellerAgent", sellerName);

        JSONObject jsonOrderNumber = new JSONObject();
        jsonOrderNumber.put("orderNumber", orderNumber);
        
        JSONObject jsonTarjeta = new JSONObject();
        
        if(tarjeta != "null"){
            jsonTarjeta.put("tarjeta", tarjeta);
        }else{
            jsonTarjeta.put("tarjeta", "null");
        }
        
        JSONObject jsonItems = new JSONObject();
        jsonItems.put("items", Arrays.asList(items));
        
        array.put(jsonSellerAgent);
        array.put(jsonOrderNumber);
        array.put(jsonTarjeta);
        array.put(jsonItems);
        
        
        return "{pedido:"+array.toString()+"}";
        
    }
    
    
}
