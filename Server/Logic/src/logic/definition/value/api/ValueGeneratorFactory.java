package logic.definition.value.api;

import logic.definition.value.fixed.FixedValueGenerator;
import logic.definition.value.random.RandomBooleanValueGenerator;
import logic.definition.value.random.RandomFloatValueGenerator;
import logic.definition.value.random.RandomIntegerValueGenerator;
import logic.definition.value.random.RandomStringValueGenerator;

public interface ValueGeneratorFactory {

    static <T> ValueGenerator<T> createFixedValue(T value) {
        return new FixedValueGenerator<>(value);
    }

    static ValueGenerator<Boolean> createRandomBoolean() {
        return new RandomBooleanValueGenerator();
    }

    static ValueGenerator<String> createRandomString() {
        return new RandomStringValueGenerator();
    }

    static ValueGenerator<Integer> createRandomInteger(Integer from, Integer to) {
        return new RandomIntegerValueGenerator(from, to);
    }

    static ValueGenerator<Float> createRandomFloat(Float from, Float to){
        return new RandomFloatValueGenerator(from, to);
    }
}
