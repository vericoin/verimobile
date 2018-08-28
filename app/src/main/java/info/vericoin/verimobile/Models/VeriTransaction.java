package info.vericoin.verimobile.Models;

import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;

import java.io.Serializable;

public class VeriTransaction implements Serializable {

    public static final Coin DEFAULT_STATIC_FEE = Coin.valueOf(50_000);

    private Coin amount;

    private Address address;

    private Coin fee;

    private String password;

    public void setAmount(Coin amount) {
        this.amount = amount;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setFee(Coin fee) {
        this.fee = fee;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Coin getAmount() {
        return amount;
    }

    public Address getAddress() {
        return address;
    }

    public Coin getFee() {
        return fee;
    }

    public Coin getTotal(){
        return getAmount().add(getFee());
    }
}
