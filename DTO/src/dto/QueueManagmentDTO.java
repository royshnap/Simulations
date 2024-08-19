package dto;

public class QueueManagmentDTO {
    private int waitingSimulations;
    private int runningSimulations;
    private long finishedSimulations;

    public QueueManagmentDTO(int waitingSimulations, int runningSimulations, long finishedSimulations) {
        this.waitingSimulations = waitingSimulations;
        this.runningSimulations = runningSimulations;
        this.finishedSimulations = finishedSimulations;
    }

    public int getWaitingSimulations() {
        return waitingSimulations;
    }

    public int getRunningSimulations() {
        return runningSimulations;
    }

    public long getFinishedSimulations() {
        return finishedSimulations;
    }
}
