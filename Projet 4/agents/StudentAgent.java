package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class StudentAgent extends Agent {
    
    protected void setup() {
        Object[] args = getArguments();

        // I Assume the resource agent is named "Printer" by default if no argument is passed
        String resourceName = (args != null && args.length > 0) ? (String) args[0] : "Printer";
        
        System.out.println("Student " + getLocalName() + " wants to print.");

        // 1. Send RESERVE request
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(resourceName, AID.ISLOCALNAME));
        msg.setContent("RESERVE");
        send(msg);

        // 2. Wait for response
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage reply = receive();
                if (reply != null) {
                    String content = reply.getContent();
                    
                    if (content.equals("ACCEPTED")) {
                        System.out.println(getLocalName() + ": Resource granted! Working...");
                        
                        // Simulate work (5 seconds delay)
                        try { Thread.sleep(5000); } catch (InterruptedException e) {}
                        
                        // 3. Release the resource
                        ACLMessage release = new ACLMessage(ACLMessage.INFORM);
                        release.addReceiver(new AID(resourceName, AID.ISLOCALNAME));
                        release.setContent("RELEASE");
                        send(release);
                        
                        System.out.println(getLocalName() + ": Done. Resource released.");
                        doDelete(); // The student has finished their task and leaves
                    } 
                    else if (content.equals("WAITING")) {
                        System.out.println(getLocalName() + ": Added to waiting queue...");
                    }
                } else {
                    block();
                }
            }
        });
    }
}