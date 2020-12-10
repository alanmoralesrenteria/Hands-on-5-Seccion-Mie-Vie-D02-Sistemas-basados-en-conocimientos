/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import java.sql.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author Alan
 */
public class MySqlConnection {
    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    public String DB_URL;
    static final String USER = "root";
    static final String PASS = "";
    
    Connection conn;

       
   public MySqlConnection(){
      DB_URL = "jdbc:mysql://localhost:3306/handson";
   }
   
   public MySqlConnection(String sellerAgent){
       
      switch(sellerAgent){
          case "AmazonAgent@192.168.56.1:1099/JADE":
              DB_URL = "jdbc:mysql://localhost:3306/amazon";
              break;
          case "WaterStonesAgent@192.168.56.1:1099/JADE":
              DB_URL = "jdbc:mysql://localhost:3306/waterstones";
              break;
          case "BarnesAndNobleAgent@192.168.56.1:1099/JADE":
              DB_URL = "jdbc:mysql://localhost:3306/barnesandnoble";
              break;
          default: 
              DB_URL = "jdbc:mysql://localhost:3306/handson";
              break;
      }
   }
   
   public ArrayList getcatalogitemsfrombd(String nameCatalogItems){
       
       ArrayList items = new ArrayList();
       
       try{
             Class.forName("com.mysql.jdbc.Driver");
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             String sql = "SELECT * FROM catalogitems WHERE nameCatalogItems='"+nameCatalogItems+"'";
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery();
             
             
             if (rs.next() == false) { 
                    System.out.println("No se encotraron items para el vendedor: " + nameCatalogItems); 
                    items = null;
             } else { 
                 do { 
                     int partNumber = rs.getInt("partNumber");
                     String description = rs.getString("description");
                     Double price = rs.getDouble("price");
                     
                     items.add(new CatalogItem(description, partNumber, price.floatValue()));
                     
                     //System.out.println("Obteniendo de la BD: " + partNumber + " " + description + " price: " + price); 
                 } while (rs.next());
                 
                 System.out.println(" "); 
             }
             
             preparedStatement.close();
             conn.close(); 
             
             
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());

       }
        return items;
   }
   
   public void aniadircatalogitem(int partnumber, String description, Double price){
   try{
             Class.forName("com.mysql.jdbc.Driver");
             
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             String sql = "INSERT INTO catalogitems (partNumber, description, price,nameCatalogItems)" +
            "VALUES (?, ?, ?,?)";
             
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             
             preparedStatement.setInt(1,partnumber);
             preparedStatement.setString(2, description);
             preparedStatement.setDouble(3, price);
             preparedStatement.setString(4,"");
             
             preparedStatement.executeUpdate(); 
             preparedStatement.close();
             
             conn.close();  
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());

       }
   }
   
   public void inicializarbd(){
       try{
             Class.forName("com.mysql.jdbc.Driver");
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             String sql = "DELETE FROM catalogitems WHERE 1";
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             preparedStatement.execute(); 
             preparedStatement.close();
             
             conn.close();  
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());

       }
       
       aniadircatalogitem(1234,"CD Writer",199.99);
       aniadircatalogitem(782321,"CD-RW Disks",5.99);
       aniadircatalogitem(4323,"AA Batteries",4.99);
       aniadircatalogitem(9876,"Gold-tipped cable",19.99);
       aniadircatalogitem(222123,"Amplifier",29.99);
       aniadircatalogitem(34526,"Incredibles DVD",399.99);
       
       aniadircatalogitem(111,"iPhone 11 Pro",25000.00);
       aniadircatalogitem(222,"Samsung Note 11",18000.00);
       aniadircatalogitem(333,"MacBook Air",30000.00);
       
   };
   
   
   public void testconection(){
       try{
             Class.forName("com.mysql.jdbc.Driver");
             System.out.println("Conectandose a la base de datos...");
             
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             System.out.println("Conexion a la base de datos exitosa...");
             System.out.println("Insertando datos en la tabla...");

             String sql = "INSERT INTO catalogitems (partNumber, descripcion, price)" +
            "VALUES (?, ?, ?)";
             
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             
             preparedStatement.setInt(1,1);
             preparedStatement.setString(2, "Prueba catalogItems");
             preparedStatement.setDouble(3, 300.00);
             
             preparedStatement.executeUpdate(); 
             preparedStatement.close();
             
             conn.close();  
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());

       }
   }
   
   
   
   public int getItemId(String itemName){
       int number = 0;
       try{
             Class.forName("com.mysql.jdbc.Driver");
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             String sql = "SELECT partNumber FROM catalogitems WHERE description='"+itemName+"'";
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery();
             
             
             if (rs.next() == false) { 
                    System.out.println("No se encontro el id del producto:" + itemName);
             } else { 
                 do { 
                     number = rs.getInt("partNumber");
                     //System.out.println("Obteniendo de la BD: " + partNumber + " " + description + " price: " + price); 
                 } while (rs.next());
                 
                 System.out.println(" "); 
             }
             
             preparedStatement.close();
             conn.close(); 
             
             
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());
          number = 0;

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());
          number = 0;

       }
       
       return number;
   }
   
   public boolean hasStock(String item, String seller, int quantity){
       
       int number = 0;
       int stock = 0;
       boolean band = false;
       try{
             Class.forName("com.mysql.jdbc.Driver");
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             String sql = "SELECT partNumber, stock FROM catalogitems WHERE description='"+item+"' and nameCatalogItems='"+seller+"'";
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery();
             
             
             if (rs.next() == false) { 
                    System.out.println("No se encontro el id del producto:" + item);
             } else { 
                 do { 
                     number = rs.getInt("partNumber");
                     stock = rs.getInt("stock");
                     
                     if(quantity <= stock){
                         band = true;
                         System.out.println("Si hay stock en la BD del producto");
                     }
                     //System.out.println("Obteniendo de la BD: " + partNumber + " " + description + " price: " + price); 
                 } while (rs.next() && !band);
                 
                 System.out.println(" "); 
             }
             
             preparedStatement.close();
             conn.close(); 
             
             
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());
          number = 0;

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());
          number = 0;

       }
       
       return band;
   }
   
   public boolean updateStock(String idItem, int quantity){
       
       int number = 0;
       boolean band = false;
       try{
             Class.forName("com.mysql.jdbc.Driver");
             conn = DriverManager.getConnection(DB_URL, USER, PASS);
             
             String sql = "CALL reserveItem (?, ?)";
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             preparedStatement.setString(1, idItem);
             preparedStatement.setInt(2, quantity);
             ResultSet rs = preparedStatement.executeQuery();
             
             preparedStatement.close();
             conn.close(); 
             band = true;
             
             
       }catch(SQLException se){
          //Handle errors for JDBC
          System.out.println(se.toString());
          number = 0;

       }catch(Exception e){
          //Handle errors for Class.forName
          System.out.println(e.toString());
          number = 0;

       }
       
       return band;
       
   }
   
   
}
