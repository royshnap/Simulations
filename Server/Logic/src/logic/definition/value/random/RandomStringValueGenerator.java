package logic.definition.value.random;

import logic.definition.value.api.ValueGenerator;

import java.util.Random;

public class RandomStringValueGenerator extends AbstractRandomValueGenerator<String> {
    private Random random;

    public RandomStringValueGenerator(){
        random = new Random();
    }

    @Override
    public String generateValue() {
        String allowedCharacters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ 0123456789!?,_-().";
        int maxLength = 50;
        Random random = new Random();
        int length = random.nextInt(maxLength) + 1;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(allowedCharacters.length());
            char randomChar = allowedCharacters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }
        return stringBuilder.toString();
    }
}
