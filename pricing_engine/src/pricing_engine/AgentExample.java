/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pricing_engine;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
/**
 *
 * @author Alan
 */
public class AgentExample extends Agent{
    protected void setup() {
    System.out.println("Agent "+getLocalName()+" started.");

    // Add the CyclicBehaviour
    addBehaviour(new CyclicBehaviour(this) {
      public void action() {
        System.out.println("Cycling");
      } 
    });

    // Add the generic behaviour
    addBehaviour(new FourStepBehaviour());
  } 

  /**
   * Inner class FourStepBehaviour
   */
  private class FourStepBehaviour extends Behaviour {
    private int step = 1;

    public void action() {
      switch (step) {
      case 1:
        // Perform operation 1: print out a message
        System.out.println("Operation 1");
        break;
      case 2:
        // Perform operation 2: Add a OneShotBehaviour
        System.out.println("Operation 2. Adding one-shot behaviour");
        myAgent.addBehaviour(new OneShotBehaviour(myAgent) {
          public void action() {
            System.out.println("One-shot");
          } 
        });
        break;
      case 3:
        // Perform operation 3: print out a message
        System.out.println("Operation 3");
        break;
      case 4:
        // Perform operation 3: print out a message
        System.out.println("Operation 4");
        break;
      }
      step++;
    } 

    public boolean done() {
      return step == 5;
    } 

    public int onEnd() {
      myAgent.doDelete();
      return super.onEnd();
    } 
  }    // END of inner class FourStepBehaviour
}
