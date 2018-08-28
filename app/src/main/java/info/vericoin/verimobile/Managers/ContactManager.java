package info.vericoin.verimobile.Managers;

import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import info.vericoin.verimobile.Models.Contact;

public class ContactManager {

    private final static String CONTACT_LIST = "contactList";

    private SharedPreferences sharedPref;

    public ContactManager(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public ArrayList<Contact> getContactList() {
        String contactListJson = sharedPref.getString(CONTACT_LIST, "");
        if (contactListJson.isEmpty()) {
            return new ArrayList<>(); //Return empty list
        } else {
            Gson gson = new Gson();
            return gson.fromJson(contactListJson, new TypeToken<ArrayList<Contact>>() {
            }.getType());
        }
    }

    public void updateContact(int index, Contact contact){
        ArrayList<Contact> contactList = getContactList();
        contactList.remove(index);
        contactList.add(index, contact);
        saveContactList(contactList);
    }

    public void removeContact(int index){
        ArrayList<Contact> contactList = getContactList();
        contactList.remove(index);
        saveContactList(contactList);
    }

    public void addContact(Contact contact) {
        ArrayList<Contact> contactList = getContactList();
        contactList.add(contact);
        saveContactList(contactList);
    }

    public void removeContact(Contact contact) {
        ArrayList<Contact> contactList = getContactList();
        contactList.remove(contact);
        saveContactList(contactList);
    }

    private void saveContactList(ArrayList<Contact> contactList) {
        Gson gson = new Gson();
        String contactListJson = gson.toJson(contactList);
        sharedPref.edit().putString(CONTACT_LIST, contactListJson).apply();
    }
}
