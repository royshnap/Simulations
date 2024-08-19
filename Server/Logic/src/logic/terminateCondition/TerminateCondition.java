package logic.terminateCondition;

public abstract class TerminateCondition {
    private int count;
    private int seconds;

    public TerminateCondition(int time){
        count = time;
    }

    public TerminateCondition(int count, int seconds) {
        this.count = count;
        this.seconds = seconds;
    }

    public int getSeconds() {
        return seconds;
    }

    public int getCount() {
        return count;
    }

    public TerminateCondition() {
    }
}
