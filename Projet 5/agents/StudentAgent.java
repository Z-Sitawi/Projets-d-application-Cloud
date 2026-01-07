package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class StudentAgent extends Agent {

    private LamportClock clock = new LamportClock();

    protected void setup() {
        Object[] args = getArguments();

        // I Assume the resource agent is named "Printer" by default if no argument is
        // passed
        String resourceName = (args != null && args.length > 0) ? (String) args[0] : "Printer";

        System.out.println("Student " + getLocalName() + " wants to print.");

        // Lamport rule 1: tick before sending
        int timestamp = clock.sendEvent();

        // 1. Send RESERVE request
        ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
        msg.addReceiver(new AID(resourceName, AID.ISLOCALNAME));
        msg.setContent("RESERVE");
        msg.setConversationId(String.valueOf(timestamp)); // We attach the Lamport timestamp
        send(msg);

        // 2. Wait for response
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage reply = receive();
                if (reply != null) {
                    // Lamport rule 2: update clock on receive
                    int receivedTimestamp = Integer.parseInt(reply.getConversationId());
                    clock.receiveEvent(receivedTimestamp);

                    String content = reply.getContent();

                    if (content.equals("ACCEPTED")) {
                        System.out.println(getLocalName()
                                + ": Resource granted at logical time "
                                + clock.getTime());
                        
                        // Simulate work (5 seconds delay)
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                        }

                        // Lamport rule 1: tick before sending
                        int releaseTime = clock.sendEvent();
                        
                        // 3. Release the resource
                        ACLMessage release = new ACLMessage(ACLMessage.INFORM);
                        release.addReceiver(new AID(resourceName, AID.ISLOCALNAME));
                        release.setContent("RELEASE");
                        release.setConversationId(String.valueOf(releaseTime)); // Attach Lamport timestamp
                        send(release);

                        System.out.println(getLocalName() + ": Done. Resource released.");
                        doDelete(); // The student has finished their task and leaves
                    } else if (content.equals("WAITING")) {
                        System.out.println(getLocalName() + ": Added to waiting queue...");
                    }
                } else {
                    block();
                }
            }
        });
    }
}