package agents;
import jade.core.AID;

class Request implements Comparable<Request> {
    AID sender;
    int timestamp;

    Request(AID sender, int timestamp) {
        this.sender = sender;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(Request other) {
        if (this.timestamp != other.timestamp) {
            return Integer.compare(this.timestamp, other.timestamp);
        }
        return this.sender.getLocalName()
                .compareTo(other.sender.getLocalName());
    }
}

