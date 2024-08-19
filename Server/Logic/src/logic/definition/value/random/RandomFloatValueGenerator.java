package logic.definition.value.random;

import java.util.Random;

public class RandomFloatValueGenerator extends AbstractNumericRandomGenerator<Float>{
    private Random random;

    public RandomFloatValueGenerator(Float from, Float to) {
        super(from, to);
        random = new Random();
    }

    @Override
    public Float generateValue() {
        return from + random.nextFloat() * (to - from);
    }
}
