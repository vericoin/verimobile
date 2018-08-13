package info.vericoin.verimobile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class VericoinGettingStartedActivity extends AppCompatActivity {

    private Button nextButton;

    public static Intent createIntent(Context context) {
        return new Intent(context, VericoinGettingStartedActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vericoin_getting_started);

        nextButton = findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(VeriumGettingStartedActivity.createIntent(VericoinGettingStartedActivity.this));
            }
        });

    }
}
