package logic.rule;

import java.util.Random;

public class Activation {

    private int ticks = 1;
    private double probability = 1;

    public Activation(int tick, double prob){
        ticks = tick;
        probability = prob;
    }

    public Activation(int tick){
        ticks = tick;
    }

    public Activation(double prob){
        probability = prob;
    }

    public Activation(){}
    public boolean isActive(int tickNumber){
        Random random = new Random();
        if(tickNumber % ticks == 0){
            return random.nextDouble() <= probability;
        }
        return false;

    }

    public int getTicks() {
        return ticks;
    }

    public double getProbability() {
        return probability;
    }
}
