package it.unipr.bozzolini.mobdev.mywallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private View.OnClickListener myAddListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton imgBtnAdd = findViewById(R.id.imageButtonAdd);
        imgBtnAdd.setOnClickListener(myAddListener);

    }
}
