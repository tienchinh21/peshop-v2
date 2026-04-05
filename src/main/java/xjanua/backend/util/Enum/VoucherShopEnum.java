package xjanua.backend.util.Enum;

public class VoucherShopEnum {

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

    public enum type {
        FIXED_AMOUNT(0),
        PERCENTAGE(1);

        private final int value;

        type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public static type fromValue(int value) {
            for (type t : values()) {
                if (t.value == value)
                    return t;
            }
            throw new IllegalArgumentException("Invalid type value: " + value);
        }
    }
}
