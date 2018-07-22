package it.unipr.bozzolini.mobdev.mywallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_MAIN = "MAINACTIVITY";

    private View.OnClickListener startAddActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), AddActivity.class);
            Log.d(TAG_MAIN, "About to start new activity");
            startActivity(intent);
        }
    };

    private View.OnClickListener startShowExpenseActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), ShowExpenses.class);
            Log.d(TAG_MAIN, "About to start new activity");
            startActivity(intent);
        }
    };

    private View.OnClickListener startStatsActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), StatsActivity.class);
            Log.d(TAG_MAIN, "About to start new activity");
            startActivity(intent);
        }
    };

    private View.OnClickListener startCVSActivity = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), CVSActivity.class);
            Log.d(TAG_MAIN, "About to start new activity");
            startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG_MAIN,"Dentro main activity");

        ImageButton imgBtnAdd = findViewById(R.id.imageButtonAdd);
        imgBtnAdd.setOnClickListener(startAddActivity);

        ImageButton imgBtnMovements = findViewById(R.id.imageButtonMovements);
        imgBtnMovements.setOnClickListener(startShowExpenseActivity);

        ImageButton imgBtnStats = findViewById(R.id.imageButtonStats);
        imgBtnStats.setOnClickListener(startStatsActivity);

        ImageButton imgBtnCVS = findViewById(R.id.imageButtonConvert);
        imgBtnCVS.setOnClickListener(startCVSActivity);
    }
}
