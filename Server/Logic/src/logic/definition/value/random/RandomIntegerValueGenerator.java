package logic.definition.value.random;

import java.util.Random;

public class RandomIntegerValueGenerator extends AbstractNumericRandomGenerator<Integer> {
    private Random random;
    public RandomIntegerValueGenerator(Integer from, Integer to){
        super(from,to);
        random = new Random();
    }

    @Override
    public Integer generateValue() {

        return random.nextInt(to - from + 1) + from;
    }
}
