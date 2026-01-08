package agents;

public class MainContainer {
    public static void main(String[] args) {
        String agents = "PrimaryAgent:agents.PrimaryAgent;BackupAgent:agents.BackupAgent";
        jade.Boot.main(new String[] { "-agents", agents });
    }
}