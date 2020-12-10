package pricing_engine;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;

public class CustomerAgent extends Agent {
	// El actual item a buscar
	private String actualTarget;
        
        // Arreglo de todos los items que busca el cliente
        private ArrayList<String> targetItems;
        
        // Arreglo de las cantidades de los items a comprar
        private ArrayList<Integer> targetquantity;
        
        // Lista de los elementos de la orde; estos ya tienen la informacion proporcionada
        // por el Seller Agent
        protected ArrayList<OrderItem> items;
        
        // Arreglo de las ordenes, no se utilizara en este hands on
        // private String[] orders;
        
        // Conteno de ordenes
        // private int orderCount = 0;
        
        // Numero de orden
        private String orderNumber;
        // Objeto que contendra los datos de la orden
        private myOrder finalOrder;
        
        public String tipopago;
        
        // Controla el numero de items pendientes por comprar.
        private int pendingItems;
        
        private int actualItemSearching;
        
	// The list of known seller agents
	private AID[] sellerAgents;

	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hola agente Comprador: "+getAID().getName()+".");

		// Obtenenr el titulo de los items que llegan como argumento.
		Object[] args = getArguments();
                items = new ArrayList<OrderItem>();
                targetItems = new ArrayList<String>();
                targetquantity = new ArrayList<Integer>();
                tipopago = "ninguna";
                orderNumber = "" + 10;
                
		if (args != null && args.length > 0) {
			
                        String eItems = (String) args[0];
                        actualItemSearching = 0;
                        
                        // Obtiene el arreglo de elementos a buscar
                        //targetItems = String.join( " ", eItems ).split("[; ]");
                        
                        String[] argsItems = String.join( " ", eItems ).split("[; ]");
                        for(int i = 0;i < argsItems.length; i++){
                            
                            String[] aux = String.join( " ", argsItems[i] ).split("[| ]");
                            
                            String auxKey = aux[0].toString();
                            
                            if(auxKey.equals("tarjeta")){
                                tipopago = aux[1].toString();
                            }else{
                                targetItems.add(aux[0]);
                                targetquantity.add(Integer.parseInt(aux[1].toString()));
                            }
                            
                        }
                        // Como item a buscar, se inicializa el primero de la lista.
                        actualTarget = targetItems.get(0);
                        pendingItems = targetItems.size();

			// Add a TickerBehaviour that schedules a request to seller agents every N milliseconds
                        // Añade un TickerBehaviour que ejeucta un apeticion a los agentes vendedores cada 5 segundos
			addBehaviour(new TickerBehaviour(this, 3000) {
				protected void onTick() {
					//System.out.println("Intentando comprar: "+targetItem);
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("product-selling");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent, template); 
						System.out.println("Se encontraron los siguientes agentes vededores:");
						sellerAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							sellerAgents[i] = result[i].getName();
							System.out.println(sellerAgents[i].getName());
						}
					}
					catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer());
				}
			} );
		}
		else {
			// Make the agent terminate
			System.out.println("No se especifico ningun item a buscar");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Finalizando el agente comprador: "+getAID().getName());
	}

	/**
	   Inner class RequestPerformer.
	   This is the behaviour used by Book-buyer agents to request seller 
	   agents the target book.
	 */
	private class RequestPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer
                private AID finalSeller; // Es de quien compraremos todos los productos
		private float bestPrice;  // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;

		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
                                // Enviar "Cotizacion"  a todos los vendedores
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				} 
				cfp.setContent(actualTarget);
				cfp.setConversationId("item-trade");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("item-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
                                                // Esta es la oferta
                                                //System.out.println("Obteniendo respuesta...");
						float price = Float.parseFloat(reply.getContent());
						if (bestSeller == null || price < bestPrice) {
							// This is the best offer at present
                                                        //System.out.println("Esta es la mejor oferta " + price);
                                                        MySqlConnection conn = new MySqlConnection(reply.getSender().getName());
                                                        if(conn.hasStock(actualTarget, reply.getSender().getName(), targetquantity.get(actualItemSearching) ))
                                                        {
                                                            bestPrice = price;
                                                            bestSeller = reply.getSender();
                                                        }else{
                                                            System.out.println("Producto cotizado, pero sin stock...");
                                                        }
						}
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
				// Send the purchase order to the seller that provided the best offer
                                //System.out.println("Enviando orden de compra al mejor ofertante...");
				//System.out.println("Preparando mensaje...");
                                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(actualTarget);
				order.setConversationId("item-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
                                
                                //System.out.println("Enviando mensaje...");
                                
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("item-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:      
				// Receive the purchase order reply
                                //System.out.println("Obteniendo respuesta a la orden de compra...");
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
                                                
                                                if(finalSeller == null){
                                                    finalSeller = reply.getSender();
                                                }
                                                
						 System.out.println(actualTarget+" añadido al pedido para el agente: "+finalSeller.getName());
						 System.out.println("El precio cotizado fue: "+bestPrice);
                                                
                                                // System.out.println("Por el precio: "+bestPrice);

                                                // Añade el producto a la lista de items de la orden
                                                MySqlConnection con = new MySqlConnection(finalSeller.getName());
                                                
                                                items.add(new OrderItem(actualTarget,con.getItemId(actualTarget)  , bestPrice, targetquantity.get(actualItemSearching) ) );
                                                
                                                pendingItems = pendingItems-1;
                                                //si ya no hay mas items pendientes, finaliza el agente;
                                                
                                                if(pendingItems == 0){
                                                    step=4;
                                                    myAgent.doDelete();
                                                }else{
                                                    
                                                    //se "compro", pero aun hay pendientes, por lo que debe buscar el siguiente item;
                                                    
                                                    actualItemSearching++;
                                                    
                                                    actualTarget = targetItems.get(actualItemSearching);
                                                    
                                                    // hacemos null el bestSeller para que pueda volver a buscar el valor del producto.
                                                    bestSeller = null;
                                                    
                                                    step = 0;
                                                    
                                                }
                                                
					}
					else {
                                                //si no se pudo "comprar", intenta de nuevo
                                                System.out.println("El item  "+actualTarget+" No se pudo cotizar de "+reply.getSender().getName());
						System.out.println("Intento fallido: el item no esta a la venta.");
                                                step = 0;
					}

					
				}
				else {
					block();
				}
				break;
                        case 4:
                            
                            break;
                        }
                        
                        if(step == 4 ){
                            System.out.println("Todos lo productos se cotizaron con exito:");
                            
                            if("ninguna".equals(tipopago)){
                                finalOrder = new myOrder(orderNumber,finalSeller.getName(),items);
                            }else{
                                finalOrder = new myOrder(orderNumber,finalSeller.getName(),items,tipopago);
                            }
                            
                            
                            try {
                                System.out.println("Se genero la orden:" + finalOrder.getJsonOrder());
                            } catch (JSONException ex) {
                                Logger.getLogger(CustomerAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            System.out.println("Enviando mensaje de procesamiento de orden...");
                            //Realizar la peticion de procesar orden
                            ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
                            order.addReceiver(finalSeller);
                            
                            try {
                                order.setContent(finalOrder.getJsonOrder());
                            } catch (JSONException ex) {
                                Logger.getLogger(CustomerAgent.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            order.setConversationId("item-trade");
                            order.setReplyWith("order"+System.currentTimeMillis());
                            //System.out.println("Enviando mensaje...");

                            myAgent.send(order);
                            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("item-trade"),
                                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                        }
                        
		}

		public boolean done() {
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	}  // End of inner class RequestPerformer
}
