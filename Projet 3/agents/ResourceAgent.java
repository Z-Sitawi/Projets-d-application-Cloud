package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;


public class ResourceAgent extends Agent {
    private boolean available = true;

    protected void setup() {
        System.out.println(getLocalName() + " lancé");
        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals("RESERVE")) {
                        ACLMessage reply = msg.createReply();
                        if (available) {
                            available = false;
                            reply.setContent("ACCEPTED");
                        } else {
                            reply.setContent("REFUSED");
                        }
                        send(reply);
                    }
                } else {
                    block();
                }
            }
        });
    }
}
