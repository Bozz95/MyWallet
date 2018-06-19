package it.unipr.bozzolini.mobdev.mywallet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class AddActivity extends AppCompatActivity {
    private  static final String TAG_ADD = "TAG ADD ACTIVITY";

    private DbReaderDbHelper dbHelper;

    private Calendar myCalendar = Calendar.getInstance();

    private void updateLabel() {
        EditText myDateText = findViewById(R.id.editTextDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.ITALY);
        myDateText.setText(sdf.format(myCalendar.getTime()));

    }

    /**
     * Get decimal formated string to include comma seperator to decimal number
     *
     * @param value
     * @return
     */
    public  String getDecimalFormattedString(String value) {
        if (value != null && !value.equalsIgnoreCase("")) {
            StringTokenizer lst = new StringTokenizer(value, ".");
            String str1 = value;
            String str2 = "";
            if (lst.countTokens() > 1) {
                str1 = lst.nextToken();
                str2 = lst.nextToken();
            }
            String str3 = "";
            int i = 0;
            int j = -1 + str1.length();
            if (str1.charAt(-1 + str1.length()) == '.') {
                j--;
                str3 = ".";
            }
            for (int k = j; ; k--) {
                if (k < 0) {
                    if (str2.length() > 0)
                        str3 = str3 + "." + str2;
                    return str3;
                }
                if (i == 3) {
                    str3 = "," + str3;
                    i = 0;
                }
                str3 = str1.charAt(k) + str3;
                i++;
            }
        }
        return "";
    }

    // SPINNER ADAPTER
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

    /**
     * Restrict digits after decimal point value as per currency
     */
    class MoneyValueFilter extends DigitsKeyListener {
        private int digits;

        public MoneyValueFilter(int i) {
            super(false, true);
            digits = i;
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            CharSequence out = super.filter(source, start, end, dest, dstart, dend);

            // if changed, replace the source
            if (out != null) {
                source = out;
                start = 0;
                end = out.length();
            }

            int len = end - start;

            // if deleting, source is empty
            // and deleting can't break anything
            if (len == 0) {
                return source;
            }

            int dlen = dest.length();

            // Find the position of the decimal .
            for (int i = 0; i < dstart; i++) {
                if (dest.charAt(i) == '.') {
                    // being here means, that a number has
                    // been inserted after the dot
                    // check if the amount of digits is right
                    return getDecimalFormattedString((dlen - (i + 1) + len > digits) ? "" : String.valueOf(new SpannableStringBuilder(source, start, end)));
                }
            }

            for (int i = start; i < end; ++i) {
                if (source.charAt(i) == '.') {
                    // being here means, dot has been inserted
                    // check if the amount of digits is right
                    if ((dlen - dend) + (end - (i + 1)) > digits)
                        return "";
                    else
                        break; // return new SpannableStringBuilder(source,
                    // start, end);
                }
            }

            // if the dot is after the inserted part,
            // nothing can break
            return getDecimalFormattedString(String.valueOf(new SpannableStringBuilder(source, start, end)));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DbReaderDbHelper(getApplicationContext());

        setContentView(R.layout.activity_add);

        EditText date = findViewById(R.id.editTextDate);
        date.setOnClickListener(popupCalendar);

        EditText etAmount = findViewById(R.id.editTextAmount);
        etAmount.addTextChangedListener(amountTextWatcher);

        Spinner spinner = findViewById(R.id.spinner);
        setupSpinner(spinner);

        Button btnStartAddCategory = findViewById(R.id.button_add_category);
        btnStartAddCategory.setOnClickListener(startAddCategory);
    }

    /// EVENT LISTENERS
    private View.OnClickListener startAddCategory = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.d(TAG_ADD, "provo a creare l'intent");
            Intent intent = new Intent(AddActivity.this, AddCategory.class);
            Log.d(TAG_ADD, "About to start new activity add category");
            startActivityForResult(intent, 1);
        }
    };

    private View.OnClickListener popupCalendar = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            new DatePickerDialog(AddActivity.this, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();
        }
    };

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            // TODO Auto-generated method stub
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        }

    };

    TextWatcher amountTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            EditText etAmount = findViewById(R.id.editTextAmount);
            int cursorPosition = etAmount.getSelectionEnd();
            String originalStr = etAmount.getText().toString();

            //To restrict only two digits after decimal place
            etAmount.setFilters(new InputFilter[]{new MoneyValueFilter(2)});

            try {
                etAmount.removeTextChangedListener(this);
                String value = etAmount.getText().toString();

                if (value != null && !value.equals("")) {
                    if (value.startsWith(".")) {
                        etAmount.setText("0.");
                    }
                    if (value.startsWith("0") && !value.startsWith("0.")) {
                        etAmount.setText("");
                    }
                    String str = etAmount.getText().toString().replaceAll(",", "");
                    if (!value.equals(""))
                        etAmount.setText(getDecimalFormattedString(str));

                    int diff = etAmount.getText().toString().length() - originalStr.length();
                    etAmount.setSelection(cursorPosition + diff);
                }
                etAmount.addTextChangedListener(this);
            } catch (Exception ex) {
                ex.printStackTrace();
                etAmount.addTextChangedListener(this);
            }
        }
    };



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_add:
                //TODO
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
