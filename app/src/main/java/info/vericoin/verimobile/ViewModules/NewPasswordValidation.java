package info.vericoin.verimobile.ViewModules;

import android.support.design.widget.TextInputLayout;

public class NewPasswordValidation {

    private TextInputLayout passwordLayout;

    private TextInputLayout rePasswordLayout;

    public NewPasswordValidation(TextInputLayout password, TextInputLayout rePassword) {
        this.passwordLayout = password;
        this.rePasswordLayout = rePassword;
    }

    public boolean checkValidity() {
        String password = passwordLayout.getEditText().getText().toString();
        String rePassword = rePasswordLayout.getEditText().getText().toString();
        if (!password.equals(rePassword)) {
            rePasswordLayout.setError("Passwords do not match");
            return false;
        }
        if (password.isEmpty()) {
            passwordLayout.setError("Password can not be empty");
            return false;
        }
        resetErrors();
        return true;
    }

    public void resetErrors() {
        passwordLayout.setErrorEnabled(false);
        rePasswordLayout.setErrorEnabled(false);
    }

    public String getPassword() {
        return passwordLayout.getEditText().getText().toString();
    }
}
