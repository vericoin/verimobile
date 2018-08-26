package info.vericoin.verimobile;

import android.content.SharedPreferences;

import info.vericoin.verimobile.Util.UtilMethods;

public class PasswordManager {

    private final static String PASSWORD_HASH_PREF = "passwordHash";

    private SharedPreferences sharedPref;

    public PasswordManager(SharedPreferences sharedPref) {
        this.sharedPref = sharedPref;
    }

    public boolean checkPassword(String password) {
        String passwordHash = sharedPref.getString(PASSWORD_HASH_PREF, "");
        if (passwordHash.isEmpty()) {
            return true; //There is no password
        } else {
            return passwordHash.equals(UtilMethods.hashStringSHA256(password));
        }
    }

    public boolean doesPasswordExist() {
        String password = sharedPref.getString(PASSWORD_HASH_PREF, "");
        if (password.isEmpty()) {
            return false;
        } else {
            return true;
        }
    }

    public void removePassword() {
        sharedPref.edit().remove(PASSWORD_HASH_PREF).apply();
    }

    public void newPassword(String newPassword) {
        sharedPref.edit().putString(PASSWORD_HASH_PREF, UtilMethods.hashStringSHA256(newPassword)).apply();
    }

    public String getPasswordHash() {
        return sharedPref.getString(PASSWORD_HASH_PREF, "");
    }
}
