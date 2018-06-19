package it.unipr.bozzolini.mobdev.mywallet;

import android.content.ContentValues;
import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatatypeMismatchException;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddCategory extends AppCompatActivity {

    private static final String TAG_ADD_CATEGORY = "Add Category Activity";

    private DbReaderDbHelper dbHelper;

    /*
    private SQLiteDatabase db = dbHelper.getWritableDatabase();
    */
    private void toastMessenger(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private View.OnClickListener addCategory = new View.OnClickListener() {

        @Override
        public void onClick(View view) {
            TextInputLayout textCategory = findViewById(R.id.TextInputLayoutCategory);
            String categoryName = textCategory.getEditText().getText().toString();

            if (categoryName.matches("")) {
               textCategory.setError("Serve un NOME per la categoria.");
               return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(DbConfig.Category.COLUMN_NAME_NAME, categoryName);

            long newRowId = db.insert(DbConfig.Category.TABLE_NAME, null, values);
            if(newRowId != -1) {
                toastMessenger("Nuova categoria inserita.");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("refresh", true);
                setResult(RESULT_OK,resultIntent);
                } else {
                textCategory.setError("Categoria già esistente.");
            }
            finish();
        }
    };

    private View.OnFocusChangeListener editTextFocusEvent = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            TextInputLayout textCategory = findViewById(R.id.TextInputLayoutCategory);
            textCategory.setError(null);
        }
    };

    private View.OnClickListener editTextClick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            TextInputLayout textCategory = findViewById(R.id.TextInputLayoutCategory);
            textCategory.setError(null);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHelper = new DbReaderDbHelper(getApplicationContext());

        setContentView(R.layout.activity_add_category);

        Button buttonAdd = findViewById(R.id.buttonAddCategory);
        buttonAdd.setOnClickListener(addCategory);

        TextInputLayout layoutEditText = findViewById(R.id.TextInputLayoutCategory);
        EditText editText = layoutEditText.getEditText();
        editText.setOnClickListener(editTextClick);
        //editText.setOnFocusChangeListener(editTextFocusEvent);

    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
