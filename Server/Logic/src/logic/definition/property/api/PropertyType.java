package logic.definition.property.api;

public enum PropertyType {

    DECIMAL {

        public Integer convert(Object value) {
            if (!(value instanceof Integer)) {
                throw new IllegalArgumentException("value " + value + " is not of a Decimal type (expected Integer class)");
            }
            return (Integer) value;
        }
    }, BOOLEAN {

        public Boolean convert(Object value) {

            if (!(value instanceof Boolean)) {
                throw new IllegalArgumentException("value " + value + " is not of a Boolean type (expected Boolean class)");
            }
            return (Boolean) value;
        }
    }, FLOAT {

        public Float convert(Object value) {
            if (!(value instanceof Number)) {
                throw new IllegalArgumentException("value " + value + " is not of a Float type (expected Float class)");
            }
            if(value instanceof Integer){
                return ((Integer) value).floatValue();
            }
            return (Float) value;
        }
    }, STRING {

        public String convert(Object value) {
            if (!(value instanceof String)) {
                throw new IllegalArgumentException("value " + value + " is not of a String type (expected String class)");
            }
            return (String) value;
        }

    };

    public abstract <T> T convert(Object value);
}
