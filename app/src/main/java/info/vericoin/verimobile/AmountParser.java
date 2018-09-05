package info.vericoin.verimobile;

public class AmountParser {

    private static final String DEFAULT_AMOUNT = "0";
    private static final int DEFAULT_MAX_DECIMAL_PLACES = 8;
    private static final int DEFAULT_MAX_INTEGER_PLACES = 8;
    private String amount = DEFAULT_AMOUNT;

    private int maxDecimalPlaces = DEFAULT_MAX_DECIMAL_PLACES;
    private int maxIntegerPlaces = DEFAULT_MAX_INTEGER_PLACES;

    public void setMaxDecimalPlaces(int maxDecimalPlaces) {
        this.maxDecimalPlaces = maxDecimalPlaces;
    }

    public void setMaxIntegerPlaces(int maxIntegerPlaces) {
        this.maxIntegerPlaces = maxIntegerPlaces;
    }

    public AmountParser(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void addDigit(String digit) {
        String newAmount;
        if (amount.equals("0")) {
            newAmount = digit;
        } else {
            newAmount = amount + digit;
        }
        if (isNumberValid(newAmount)) {
            amount = newAmount;
        }
    }

    public void dot() {
        String newAmount = amount + ".";
        if (isNumberValid(newAmount)) {
            amount = newAmount;
        }
    }

    public void backspace() {
        if (amount.length() < 2) {
            amount = DEFAULT_AMOUNT;
        } else {
            String newAmount = amount.substring(0, amount.length() - 1);
            if (isNumberValid(newAmount)) {
                amount = newAmount;
            } else {
                amount = DEFAULT_AMOUNT;
            }
        }
    }

    private boolean isNumberValid(String number) {

        int decimal_place = 0;
        int integer_place = 0;
        boolean dot = false;

        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);

            if (Character.isDigit(c)) {
                if (dot) {
                    decimal_place++;
                }else{
                    integer_place++;
                }
                if (decimal_place > maxDecimalPlaces || integer_place > maxIntegerPlaces) {
                    return false;
                }
            } else if (c == '.') {
                if (dot) { //Two dots
                    return false;
                } else {
                    dot = true;
                }
            }
        }
        return true;
    }
}
