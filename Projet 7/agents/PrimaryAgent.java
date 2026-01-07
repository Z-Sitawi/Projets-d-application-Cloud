package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;

public class PrimaryAgent extends Agent {

    @Override
    protected void setup() {
        System.out.println("PRIMARY: I am alive. Starting Heartbeat...");

        // Task 1: Heartbeat (Send "PING" every 1 second)
        addBehaviour(new TickerBehaviour(this, 1000) {
            @Override
            protected void onTick() {
                ACLMessage ping = new ACLMessage(ACLMessage.REQUEST);
                ping.setContent("PING");
                // We assume the backup is named "BackupAgent"
                ping.addReceiver(new AID("BackupAgent", AID.ISLOCALNAME));
                send(ping);
                System.out.println("PRIMARY: Sent PING");
            }
        });

        // Task 2: Simulate Shutdown (Crash after 5 seconds)
        addBehaviour(new WakerBehaviour(this, 5000) {
            @Override
            protected void onWake() {
                System.out.println("PRIMARY: ### SIMULATING FAILURE (CRASH) ###");
                doDelete(); // kills the agent
            }
        });
    }
}