package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import java.util.HashSet;
import java.util.Set;

public class CoordinatorAgent extends Agent {

    private int myId; // Variable to store this agent's unique ID
    private int leaderId = -1; // Variable to store the elected leader's ID. -1 means no leader yet.
    private Set<Integer> receivedIds = new HashSet<>(); // A Set to keep track of which neighbors we have heard from (avoids duplicates)

    // The setup() method is called once when the agent is initialized
    @Override
    protected void setup() {
        // Parse the agent's local name (e.g., "Agent3") to extract the number (3)
        // replace("Agent", "") removes the text, leaving only the digit string
        myId = Integer.parseInt(getLocalName().replace("Agent", ""));
        
        // Print a message to the console confirming the agent has started and knows its ID
        System.out.println(getLocalName() + " started with ID " + myId);

        // Step 1: Immediately broadcast my ID to all other agents in the network
        sendMyId();

        // Add a behaviour that runs continuously to listen for incoming messages
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                // Attempt to receive a message from the message queue
                ACLMessage msg = receive();
                
                // If a message was actually received (msg is not null)
                if (msg != null) {
                    // Process the message using a helper method
                    handleMessage(msg);
                } else {
                    // If no message is present, block this behaviour to save CPU resources
                    // It wakes up automatically when a new message arrives
                    block();
                }
            }
        });
    }

    // Helper method to broadcast this agent's ID to others
    private void sendMyId() {
        // Create a new ACL message with the communicative act INFORM
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        
        // Set the content of the message to "ID:" followed by this agent's number
        msg.setContent("ID:" + myId);

        // Broadcast (simplified): Loop through expected agents named Agent1 to Agent5
        for (int i = 1; i <= 5; i++) {
            // Construct the name of the target agent (e.g., "Agent1")
            String name = "Agent" + i;
            
            // Check to ensure we don't send the message to ourselves
            if (!name.equals(getLocalName())) {
                // Add the target agent as a receiver using their local name
                msg.addReceiver(new AID(name, AID.ISLOCALNAME));
            }
        }
        // Actually send the message to all added receivers
        send(msg);
    }

    // Helper method to process incoming messages based on their content
    private void handleMessage(ACLMessage msg) {

        // Scenario 1: The message is an ID announcement from another agent
        if (msg.getContent().startsWith("ID:")) {
            // Extract the ID number from the string "ID:5" (split at ':', take the second part)
            int receivedId = Integer.parseInt(msg.getContent().split(":")[1]);
            
            // Add this ID to our set of known neighbors
            receivedIds.add(receivedId);

            // Step 2: Immediate comparison logic (Part of Bully algorithm)
            // If the neighbor's ID is higher than mine, they are a candidate for leader
            if (receivedId > myId) {
                leaderId = receivedId; // Temporarily accept them as the highest so far
            }

            // Check if we have heard from enough people to finalize the election
            checkElectionResult();
        }

        // Scenario 2: The message is a formal declaration from the winner
        else if (msg.getContent().startsWith("LEADER:")) {
            // Parse the leader's ID from the message
            leaderId = Integer.parseInt(msg.getContent().split(":")[1]);
            
            // Print to console that this agent accepts the new leader
            System.out.println(getLocalName() + " recognizes leader: " + leaderId);
        }
    }

    // Method to check if the election process is complete
    private void checkElectionResult() {
        // We wait until we have received IDs from 4 other agents (assuming 5 total in system)
        if (receivedIds.size() >= 4) { 
            
            // If current leaderId is -1 (nobody higher found) OR the highest I saw is still lower than me
            // (Note: In this specific logic, if nobody higher was found, leaderId would still be -1 or smaller)
            if (leaderId == -1 || leaderId < myId) {
                // I am the highest ID in the network, so I become the leader
                leaderId = myId;
                
                // Announce my victory to everyone else
                announceLeader();
            }
        }
    }

    // Method to broadcast the final result
    private void announceLeader() {
        // Create a new INFORM message
        ACLMessage leaderMsg = new ACLMessage(ACLMessage.INFORM);
        
        // Set content to "LEADER:" followed by my ID
        leaderMsg.setContent("LEADER:" + leaderId);

        // Loop through all 5 agents to add them as receivers
        for (int i = 1; i <= 5; i++) {
            // Add Agent1, Agent2, etc., as receivers
            leaderMsg.addReceiver(new AID("Agent" + i, AID.ISLOCALNAME));
        }

        // Send the victory message
        send(leaderMsg);
        
        // Print to console that I have taken the leadership role
        System.out.println(getLocalName() + " is elected as LEADER");
    } 
}