package agents;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;

public class BackupAgent extends Agent {

    private long lastHeartbeatTime = 0;
    private static final long TIMEOUT = 3000; // 3 seconds tolerance
    private boolean isActive = false; // Status of this backup agent

    @Override
    protected void setup() {
        System.out.println("BACKUP: Monitoring PrimaryAgent...");
        lastHeartbeatTime = System.currentTimeMillis();

        // 1. Listen for PING
        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null) {
                    if (msg.getContent().equals("PING")) {
                        lastHeartbeatTime = System.currentTimeMillis();
                        System.out.println("BACKUP: Received PING from Primary. alive at " + readableTime(lastHeartbeatTime));
                        
                        // Optional: If primary comes back online, we could stand down
                        if (isActive) {
                             System.out.println("BACKUP: Primary returned. Standing down.");
                             isActive = false;
                        }
                    }
                } else {
                    block();
                }
            }
        });

        // 2. Periodic Check for Failure (The Watchdog)
        addBehaviour(new TickerBehaviour(this, 500) { // Check every 0.5 seconds
            @Override
            protected void onTick() {
                if (!isActive) {
                    long currentTime = System.currentTimeMillis();
                    
                    // Task 3: Detect Failure (If silence > 3000ms)
                    if (currentTime - lastHeartbeatTime > TIMEOUT) {
                        System.out.println("BACKUP: XXXXXX---- Alert! Primary Agent is silent. ----XXXXXX");
                        activateBackup();
                    }
                }
            }
        });
    }

    // Task 4: Activate BackupAgent
    private void activateBackup() {
        isActive = true;
        System.out.println("BACKUP: >>> ACTIVATING BACKUP MODE <<< at " + readableTime(System.currentTimeMillis()));
        System.out.println("BACKUP: I am now the main controller.");
    }

    public String readableTime(long millis) {
        String readableTime = Instant.ofEpochMilli(millis)
                .atZone(ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return readableTime;
    }
}