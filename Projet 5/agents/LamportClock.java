package agents;

public class LamportClock {
    private int time = 0;

    public synchronized void tick() {
        time++;
    }

    public synchronized int sendEvent() {
        time++;
        return time;
    }

    public synchronized void receiveEvent(int receivedTime) {
        time = Math.max(time, receivedTime) + 1;
    }

    public synchronized int getTime() {
        return time;
    }
}

