package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SettingsActivity extends AppCompatActivity {

    private ConstraintLayout changePasswordRow;

    public static Intent createIntent(Context context){
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        changePasswordRow = findViewById(R.id.changePasswordRow);
        changePasswordRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(ChangePasswordActivity.createIntent(SettingsActivity.this));
            }
        });
    }
}
