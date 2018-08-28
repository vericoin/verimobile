package info.vericoin.verimobile.Models;

import java.io.Serializable;

public class Contact implements Serializable{

    private String address;

    private String name;

    public Contact(String address) {
        this.address = address;
    }

    public Contact(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
