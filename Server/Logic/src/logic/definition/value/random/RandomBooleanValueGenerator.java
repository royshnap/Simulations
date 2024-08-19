package logic.definition.value.random;

import logic.definition.value.api.ValueGenerator;

import java.util.Random;

public class RandomBooleanValueGenerator extends AbstractRandomValueGenerator<Boolean> {
    private Random random;

    public RandomBooleanValueGenerator(){
        random = new Random();
    }

    @Override
    public Boolean generateValue() {
        return random.nextBoolean();
    }
}
