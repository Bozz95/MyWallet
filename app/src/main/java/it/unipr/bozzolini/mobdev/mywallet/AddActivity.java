package it.unipr.bozzolini.mobdev.mywallet;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddActivity extends AppCompatActivity {
    private  static final String TAG_ADD = "TAG ADD ACTIVITY";

    private DbReaderDbHelper dbHelper;

    private class MySpinnerAdapter extends ArrayAdapter<String> {
        public MySpinnerAdapter(Context context, int resource, List<String> objects) {
            super(context, resource, objects);
        }

        @Override
        public String getItem(int position) {
            return super.getItem(position);
        }

        @Override
        public long getItemId(int position) {
            return super.getItemId(position);
        }

        @Override
        public boolean isEnabled(int position){
            if(position == 0) return false;
            return true;
        }

        @Override
        public View getDropDownView(int position, View convertView,
                                    ViewGroup parent) {
            View view = super.getDropDownView(position, convertView, parent);
            TextView tv = (TextView) view;
            if(position == 0){
                // Set the hint text color gray
                tv.setTextColor(Color.GRAY);
            }
            else {
                tv.setTextColor(Color.BLACK);
            }
            //tv.setText(super.getItem(position).getItemString());
            return view;
        }
    }

    private void setupSpinner(Spinner spinner){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DbConfig.Category.COLUMN_NAME_NAME
        };
        String sortOrder = DbConfig.Category.COLUMN_NAME_NAME + " ASC";

        Cursor cursor = db.query(DbConfig.Category.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder);

        ArrayList<String> items = new ArrayList<String>();
        items.add(new String(getString(R.string.TextViewCategory)));

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Category.COLUMN_NAME_NAME));
            items.add(name);
        }

        cursor.close();

        MySpinnerAdapter adapter = new MySpinnerAdapter(this, android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private View.OnClickListener startAddCategory = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG_ADD, "provo a creare l'intent");
            Intent intent = new Intent(AddActivity.this, AddCategory.class);
            Log.d(TAG_ADD, "About to start new activity add category");
            startActivityForResult(intent, 1);
            //startActivity(intent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DbReaderDbHelper(getApplicationContext());

        setContentView(R.layout.activity_add);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.myToolbar);
        setSupportActionBar(myToolbar);

        Spinner spinner = findViewById(R.id.spinner);
        setupSpinner(spinner);

        Button btnStartAddCategory = findViewById(R.id.button_add_category);
        btnStartAddCategory.setOnClickListener(startAddCategory);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG_ADD, "entro nella on activity result ");
        if (requestCode == 1) {
            Log.d(TAG_ADD, "entro nel requestcode 1 ");
            if(resultCode == RESULT_OK){
                Log.d(TAG_ADD, "entro nel result OK ");
                Bundle bundle = data.getExtras();
                Boolean refresh = bundle.getBoolean("refresh");
                Log.d(TAG_ADD, "valore del risultato: " + refresh.toString());
                if(refresh){
                    Spinner spinner = findViewById(R.id.spinner);
                    setupSpinner(spinner);
                }
            }

        }
    }
}
