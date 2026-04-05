package xjanua.backend.util.Enum;

public class FlashSaleEnum {

    public enum status {
        INACTIVE(0),
        ACTIVE(1),
        ENDED(2);

        private final int value;

        status(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static status fromValue(int value) {
            for (status s : values()) {
                if (s.value == value)
                    return s;
            }
            throw new IllegalArgumentException("Invalid status value: " + value);
        }
    }

}