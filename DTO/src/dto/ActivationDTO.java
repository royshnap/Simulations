package dto;

public class ActivationDTO {
    private final int ticks;
    private final double probability;

    public ActivationDTO(int ticks, double probability) {
        this.ticks = ticks;
        this.probability = probability;
    }

    public int getTicks() {
        return ticks;
    }

    public double getProbability() {
        return probability;
    }
}
