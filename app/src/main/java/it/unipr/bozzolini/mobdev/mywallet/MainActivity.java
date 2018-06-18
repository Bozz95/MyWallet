package it.unipr.bozzolini.mobdev.mywallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG_MAIN = "MAINACTIVITY";

    public void startAddActivity(View view){
        Intent intent = new Intent(this, AddActivity.class);
        Log.d(TAG_MAIN, "About to start new activity");
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG_MAIN,"Dentro main activity");
        //ImageButton imgBtnAdd = findViewById(R.id.imageButtonAdd);


    }
}
