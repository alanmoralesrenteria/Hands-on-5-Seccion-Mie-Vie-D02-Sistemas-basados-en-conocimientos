/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jess.JessException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

/**
 *
 * @author Alan
 */
public class SellerAgent extends Agent {
	// The catalogue of books for sale (maps the title of a book to its price)
	private Hashtable catalogue;

	// Put agent initializations here
	protected void setup() {
		// Create the catalogue
		catalogue = new Hashtable();

		// Create and show the GUI 
                //System.out.println("Iniciando agente vendedor");
		// Registrar el vendedor en las paginas amarillas
                
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("product-selling");
		sd.setName(getName());
		dfd.addServices(sd);
                //System.out.println("El vendedor es: "+ getName() );
                
                /*
                Incializar la base de datos, requiere disparar un agente SelleAgent:
                
                    DemoDatabase db = new DemoDatabase("TBA");
                    db.connection().inicializarbd();
                
                */
                updateCatalogue(getName());
                
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from buyer agents
		addBehaviour(new OfferRequestsServer());

		// Add the behaviour serving purchase orders from buyer agents
		addBehaviour(new PurchaseOrdersServer());
                
                // Añade el comportamiento de procesar ordenes
                addBehaviour (new ProcessOrderBehaviur());
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Quitar de las paginas amarillas
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
                
		// Printout a dismissal message
		System.out.println("Finalizando agente vendedor: "+getAID().getName());
	}

	/**
     This is invoked by the GUI when the user adds a new book for sale
	 */
	
        /* Esto es lo que añade un libro al catalogo
        public void updateCatalogue(final String title, final int price) {
		addBehaviour(new OneShotBehaviour() {
			public void action() {
				catalogue.put(title, new Integer(price));
				System.out.println(title+" inserted into catalogue. Price = "+price);
			}
		} );
	}
        */
        
        public void updateCatalogue(String nameCatalogItems) {
		
                addBehaviour(new OneShotBehaviour() {
			public void action() {
                                MySqlConnection connection = new MySqlConnection(nameCatalogItems);
                                
                                ArrayList items = connection.getcatalogitemsfrombd(nameCatalogItems);
                                
                                boolean ans = items.isEmpty();
                                
                                if(ans == false){
                                    items.forEach(
                                        (item) -> 
                                                catalogue.put( ((CatalogItem)item).getDescription(),((CatalogItem)item).getPrice() ) 
                                    );
                                }
                                
                                
			}
		} );
	}
        
	/**
	   Inner class OfferRequestsServer.
	   This is the behaviour used by Book-seller agents to serve incoming requests 
	   for offer from buyer agents.
	   If the requested book is in the local catalogue the seller agent replies 
	   with a PROPOSE message specifying the price. Otherwise a REFUSE message is
	   sent back.
	 */
	private class OfferRequestsServer extends CyclicBehaviour {
		public void action() {
                        
                    //System.out.println("Iniciando OfferRequestServer.");
                        
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
                        
			if (msg != null) {
				// CFP Message received. Process it
                                //System.out.println("MSG no nulo");
                                
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();
                                
                                //System.out.println("MSG:"+msg.getContent());
                                //System.out.println( catalogue );
                               
                                float price = 0;
                                String valueAsString = null;
                                
                                Iterator<Map.Entry>  it;
                                Map.Entry            entry;
                                
                                it = catalogue.entrySet().iterator();
                                while (it.hasNext()) {
                                    entry = it.next();
                                    //System.out.println(
                                    //    "Comparando:" +
                                    //    entry.getKey().toString() + " " +
                                    //    entry.getValue().toString());
                                    
                                    if(title.equals(entry.getKey().toString())){
                                        
                                        valueAsString = entry.getValue().toString();
                                        price = (float)entry.getValue();
                                        
                                        //System.out.println("Precio de venta: " + price);
                                    }
                                    
                                }
                                
                                
				if (price != 0) {
					// The requested book is available for sale. Reply with the price
                                        //System.out.println("construyendo oferta...");
					reply.setPerformative(ACLMessage.PROPOSE);
					//System.out.println("obteniendo precio...");
                                        reply.setContent(valueAsString);
				}
				else {
					// The requested book is NOT available for sale.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("No disponible");
				}
                                //System.out.println("Enviando respuesta...");
				myAgent.send(reply);
                                //System.out.println("Respuesta enviada");
			}
			else {
                                //System.out.println("Bloqueando.");
				block();
			}
		}
	}  // End of inner class OfferRequestsServer

	/**
	   Inner class PurchaseOrdersServer.
	   This is the behaviour used by Book-seller agents to serve incoming 
	   offer acceptances (i.e. purchase orders) from buyer agents.
	   The seller agent removes the purchased book from its catalogue 
	   and replies with an INFORM message to notify the buyer that the
	   purchase has been sucesfully completed.
	 */
	private class PurchaseOrdersServer extends CyclicBehaviour {
		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// ACCEPT_PROPOSAL Message received. Process it
				String title = msg.getContent();
				ACLMessage reply = msg.createReply();

                                float price = 0;
                                String valueAsString = null;
                                
                                Iterator<Map.Entry>  it;
                                Map.Entry            entry;
                                
                                it = catalogue.entrySet().iterator();
                                while (it.hasNext()) {
                                    entry = it.next();
                                    
                                    if(title.equals(entry.getKey().toString())){
                                        
                                        valueAsString = entry.getValue().toString();
                                        price = (float)entry.getValue();

                                    }
                                    
                                }
                                
                                
                                //System.out.println("se vendio la wea");
                                
				if (price != 0) {
					reply.setPerformative(ACLMessage.INFORM);
					//System.out.println(title+" fue aparatado para "+msg.getSender().getName());
				}
				else {
					// The requested book has been sold to another buyer in the meanwhile .
					reply.setPerformative(ACLMessage.FAILURE);
					reply.setContent("No disponible");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class OfferRequestsServer
   

        private class ProcessOrderBehaviur extends CyclicBehaviour {

            public void action() {
                MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
                ACLMessage msg = myAgent.receive(mt);
                
                
                if(msg != null){
                    String JSONorder = msg.getContent();
                    System.out.println("Pedido recibido: "+JSONorder);
                    
                    JSONObject json;
                    JSONArray data = null;
                    
                    JSONObject jsonItems;
                    JSONArray dataItems = null;
                    
                    int customerID = 0;
                    int orderID = 0;
                    String sellerAgent;
                    String tarjeta;
                            
                    
                    
                    try {
                        
                        json = new JSONObject(JSONorder);
                        data = json.getJSONArray("pedido");
                        
                        JSONObject jsonObjectOrderNumber = data.getJSONObject(1);
                        orderID = Integer.parseInt(jsonObjectOrderNumber.optString("orderNumber"));
                        
                        JSONObject jsonObjectSellerAgent = data.getJSONObject(0);
                        sellerAgent = jsonObjectSellerAgent.optString("sellerAgent");
                        
                        JSONObject jsonObjectTarjeta = data.getJSONObject(2);
                        tarjeta = jsonObjectTarjeta.optString("tarjeta");
                        
                        JSONObject jsonObjectItems = data.getJSONObject(3);
                        JSONArray ItemsArray = jsonObjectItems.getJSONArray("items");
                        
                        JSONArray jsonObjectDataItems = (JSONArray)ItemsArray.get(0);
                        
                        int itemCount = jsonObjectDataItems.length();
                        
                        ArrayList items = new ArrayList();
                        
                        MySqlConnection aux = new MySqlConnection(sellerAgent.toString());
                        
                        for(int x = 0; x < itemCount; x++){
                            
                            JSONObject myItem = (JSONObject)jsonObjectDataItems.get(x);
                            
                            String desc = myItem.get("description").toString();
                            
                            int partN = (int)myItem.get("partNumber");
                            float price = Float.parseFloat(myItem.get("price").toString());
                            int quantity = (int)myItem.get("quantity");
                            items.add(new OrderItem(desc, partN, price, quantity));
                            
                            if(!aux.updateStock(myItem.get("partNumber").toString(), quantity))
                                System.out.println("Error al actualizar el Stock en la BD");
                            
                        }
                        
                        //para este caso, el numero de orden y el numero de comprador es el mismo
                        DemoDatabase database = new DemoDatabase(getName());
                        database.createOrder(orderID, orderID, items,tarjeta);
                        
                        
                        String filename = new String();
                        switch(getName()){
                            case "AmazonAgent@192.168.56.1:1099/JADE":
                                filename = "amazon.clp";
                                break;

                            case "WaterStonesAgent@192.168.56.1:1099/JADE":
                                filename = "waterstones.clp";
                                break;

                            case "BarnesAndNobleAgent@192.168.56.1:1099/JADE":
                                filename = "barnesandnoble.clp";
                                break;
                        }
                        
                        try {
                            
                            PricingEngine engine = new PricingEngine(database,filename);
                            processOrder(database, engine, orderID);

                        } catch (JessException e) {
                                e.printStackTrace();
                        }
                        
                        
                        
                    } catch (JSONException ex) {
                        System.out.println(ex.toString());
                    }
                    
                    
                }
                
            } 
          
        
            private void processOrder(DemoDatabase database, PricingEngine engine, int orderNumber) throws JessException {
		Iterator items;
		Iterator offers;
		System.out.println("Items para la orden " + orderNumber + ":");
		items = database.getOrder(orderNumber).getItems();
                Order orden = database.getOrder(orderNumber);
                
		while (items.hasNext()) {
			System.out.println("   " + items.next());
		}
                System.out.println("Subtotal: " + orden.getTotal());
		
		offers = engine.run(orderNumber);
		System.out.println("Ofertas posibles para la orden " + orderNumber + ":");
		while (offers.hasNext()) {
			System.out.println("  -" + offers.next());
		}
		System.out.println();
            }
        }  
}

