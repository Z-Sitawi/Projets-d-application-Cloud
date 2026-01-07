package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.PriorityQueue;

public class ResourceAgent extends Agent {
    private boolean available = true;
    private PriorityQueue<Request> waitingQueue = new PriorityQueue<>(); // The FIFO Queue

    protected void setup() {
        System.out.println("======> Resource Agent " + getLocalName() + " is ready. at logical time 0");

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                
                if (msg != null) {
                    int timestamp = Integer.parseInt(msg.getConversationId());

                    ACLMessage reply = msg.createReply();
                    reply.setConversationId(String.valueOf(timestamp)); // Echo back the Lamport timestamp
                    String content = msg.getContent();

                    if (content.equals("RESERVE")) {
                        if (available) {
                            available = false;
                            reply.setContent("ACCEPTED");
                            System.out.println("======> Resource: Granted directly to " + msg.getSender().getLocalName() + " at logical time " + timestamp);
                        } else {
                            // FIFO Strategy: Add to queue
                            waitingQueue.add(new Request(msg.getSender(), timestamp));
                            reply.setContent("WAITING");
                            System.out.println("======> Resource: Busy. " + msg.getSender().getLocalName() + " added to queue at logical time " + timestamp);
                        }
                        send(reply);
                    } 
                    else if (content.equals("RELEASE")) {
                        System.out.println("======> Resource: Released by " + msg.getSender().getLocalName() + " at logical time " + timestamp);
                        
                        if (!waitingQueue.isEmpty()) {
                            // Pick the next agent in the list (FIFO)
                            Request nextStudent = waitingQueue.poll();
                            
                            // Send ACCEPTED to the next student
                            ACLMessage notification = new ACLMessage(ACLMessage.INFORM);
                            notification.addReceiver(nextStudent.sender);
                            notification.setContent("ACCEPTED");
                            notification.setConversationId(String.valueOf(timestamp)); // Attach Lamport timestamp
                            send(notification);
                            
                            System.out.println("======> Resource: Automatically passed to " + nextStudent.sender.getLocalName() + " at logical time " + timestamp);
                            // Note: 'available' remains false because we passed the token immediately
                        } else {
                            // No one is waiting
                            available = true;
                            System.out.println("======> Resource: Now free." + " at logical time " + timestamp);
                        }
                    }
                } else {
                    block();
                }
            }
        });
    }
}