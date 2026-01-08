package agents;

import jade.core.Agent;

public class ResourceAgent extends Agent {
    @Override
    protected void setup() {
        System.out.println("CLOUD RESOURCE: Online. ID: " + getAID().getName());
    }
}