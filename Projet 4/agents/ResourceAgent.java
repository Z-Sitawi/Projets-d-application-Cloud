package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.LinkedList;
import java.util.Queue;

public class ResourceAgent extends Agent {
    private boolean available = true;
    private Queue<AID> waitingQueue = new LinkedList<>(); // The FIFO Queue

    protected void setup() {
        System.out.println("======> Resource Agent (FIFO) " + getLocalName() + " is ready.");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    ACLMessage reply = msg.createReply();
                    String content = msg.getContent();

                    if (content.equals("RESERVE")) {
                        if (available) {
                            available = false;
                            reply.setContent("ACCEPTED");
                            System.out.println("======> Resource: Granted directly to " + msg.getSender().getLocalName());
                        } else {
                            // FIFO Strategy: Add to queue
                            waitingQueue.add(msg.getSender());
                            reply.setContent("WAITING");
                            System.out.println("======> Resource: Busy. " + msg.getSender().getLocalName() + " added to queue.");
                        }
                        send(reply);
                    } 
                    else if (content.equals("RELEASE")) {
                        System.out.println("======> Resource: Released by " + msg.getSender().getLocalName());
                        
                        if (!waitingQueue.isEmpty()) {
                            // Pick the next agent in the list (FIFO)
                            AID nextStudent = waitingQueue.poll();
                            
                            // Send ACCEPTED to the next student
                            ACLMessage notification = new ACLMessage(ACLMessage.INFORM);
                            notification.addReceiver(nextStudent);
                            notification.setContent("ACCEPTED");
                            send(notification);
                            
                            System.out.println("======> Resource: Automatically passed to " + nextStudent.getLocalName());
                            // Note: 'available' remains false because we passed the token immediately
                        } else {
                            // No one is waiting
                            available = true;
                            System.out.println("======> Resource: Now free.");
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }
}