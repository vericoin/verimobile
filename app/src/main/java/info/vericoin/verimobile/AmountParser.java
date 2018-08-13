package info.vericoin.verimobile;

public class AmountParser {

    private static final String DEFAULT_AMOUNT = "0";

    private String amount = DEFAULT_AMOUNT;

    private final static int MAX_DECIMAL_PLACES = 8;

    public AmountParser(String amount) {
        this.amount = amount;
    }

    public String getAmount(){
        return amount;
    }

    public void addDigit(String digit){
        String newAmount;
        if(amount.equals("0")){
            newAmount = digit;
        }else{
            newAmount = amount + digit;
        }
        if(isNumberValid(newAmount)){
            amount = newAmount;
        }
    }

    public void dot(){
        String newAmount = amount + ".";
        if(isNumberValid(newAmount)){
            amount = newAmount;
        }
    }

    public void backspace(){
        if(amount.length() < 2){
            amount = DEFAULT_AMOUNT;
        }else {
            String newAmount = amount.substring(0, amount.length() - 1);
            if (isNumberValid(newAmount)) {
                amount = newAmount;
            } else {
                amount = DEFAULT_AMOUNT;
            }
        }
    }

    private boolean isNumberValid(String number){

        int decimal_place = 0;
        boolean dot = false;

        for (int i = 0; i < number.length(); i++){
            char c = number.charAt(i);

            if(Character.isDigit(c)){
                if(dot){
                    decimal_place++;
                }
                if(decimal_place > MAX_DECIMAL_PLACES){
                    return false;
                }
            }else if(c == '.'){
                if(dot){ //Two dots
                    return false;
                }else{
                    dot = true;
                }
            }
        }
        return true;
    }
}
