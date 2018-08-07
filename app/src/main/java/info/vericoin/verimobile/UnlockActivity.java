package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import static info.vericoin.verimobile.BitcoinApplication.PASSWORD_HASH_PREF;
import static info.vericoin.verimobile.BitcoinApplication.PREFERENCE_FILE_KEY;

public class UnlockActivity extends AppCompatActivity {

    private TextInputLayout passwordLayout;

    private Button unlockButton;

    private SharedPreferences sharedPref;

    public static Intent createIntent(Context context){
        return new Intent(context, UnlockActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unlock_wallet);

        sharedPref = getSharedPreferences(PREFERENCE_FILE_KEY, Context.MODE_PRIVATE);

        unlockButton = findViewById(R.id.unlockButton);

        passwordLayout = findViewById(R.id.passwordLayout);

        unlockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPasswordCorrect()){
                    startActivity(MainActivity.createIntent(UnlockActivity.this));
                    finish(); //Prevent app from going back to this activity after its finished.
                }else{
                    passwordLayout.setError("Password is incorrect");
                }
            }
        });
    }

    public String getPassword(){
        return passwordLayout.getEditText().getText().toString();
    }

    public boolean isPasswordCorrect(){
        String passwordHash = sharedPref.getString(PASSWORD_HASH_PREF,"");
        if(passwordHash.isEmpty()){
            return true; //There is no password
        }else {
            return passwordHash.equals(Util.hashStringSHA256(getPassword()));
        }
    }

}
