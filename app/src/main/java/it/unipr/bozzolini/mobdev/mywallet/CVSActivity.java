package it.unipr.bozzolini.mobdev.mywallet;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CVSActivity extends AppCompatActivity {
    private final String TAG_CSV = "CSV management";

    private final String CSVFilename = "savedCSV.csv";

    private DbReaderDbHelper dbHelper;

    private ArrayList<Expense> expenses = new ArrayList<Expense>();
    private ArrayList<Category> categories = new ArrayList<Category>();

    private void getCategories( ArrayList<Category> categories) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String SELECT_CAT_QUERY = "SELECT " + DbConfig.Category.COLUMN_NAME_ID + ", " +
                DbConfig.Category.COLUMN_NAME_NAME + " " +
                "FROM " + DbConfig.Category.TABLE_NAME +";";

        Log.d(TAG_CSV,"Query CAtegorie: " + SELECT_CAT_QUERY);

        Cursor cursorCat = db.rawQuery(SELECT_CAT_QUERY,null);

        while (cursorCat.moveToNext()) {

            Integer catId = cursorCat.getInt(cursorCat.getColumnIndexOrThrow(DbConfig.Category.COLUMN_NAME_ID));
            String catName = cursorCat.getString(cursorCat.getColumnIndexOrThrow(DbConfig.Category.COLUMN_NAME_NAME));

            categories.add(new Category(catId,catName));
        }
    }

    private void getExpenses(ArrayList<Expense> expenses)   {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String SELECT_EXPENSES_QUERY = "SELECT " + DbConfig.Expenses.COLUMN_NAME_ID + ", " +
                DbConfig.Expenses.COLUMN_NAME_DATE + ", " +
                DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT + ", " +
                DbConfig.Expenses.COLUMN_NAME_NOTES + ", " +
                DbConfig.Expenses.COLUMN_NAME_CATEGORY + " " +
                "FROM " + DbConfig.Expenses.TABLE_NAME +";";

        Cursor cursor = db.rawQuery(SELECT_EXPENSES_QUERY,null);

        while (cursor.moveToNext()) {

            Integer expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_ID));
            String expenseDate = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_DATE));
            String expenseNotes = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_NOTES));
            Integer tmp = cursor.getInt(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_CATEGORY));
            String expenseCategory = tmp.toString();
            Double expenseAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT));

            expenses.add(new Expense(expenseId,expenseDate, expenseCategory, expenseNotes,expenseAmount));
        }
    }


    private void toastMessenger(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    private void writeCSV() {

        String toWrite = "";
        toWrite += "CATEGORIES\nCATEGORY_ID,CATEGORY_NAME\n";

        for (int i = 0; i < categories.size(); i++) {

            Category tmp = categories.get(i);
            toWrite += Integer.toString(tmp.getId()) + ";" + tmp.getName()+ "\n";

        }

        toWrite += "EXPENSES\nEXPENSE_ID,EXPENSE_DATE,EXPENSE_NOTES,EXPENSE_CENTS_AMOUNT,EXPENSE_CATEGORY\n";

        for(int i = 0; i < expenses.size(); i++) {
            Expense tmp = expenses.get(i);
            toWrite += Integer.toString(tmp.getId()) + ";" + tmp.getDate() + ";" + tmp.getNotes() + ";" +
                        Double.toString(tmp.getAmountValue()) + ";" + tmp.getCategory() + "\n";
        }

        Log.d(TAG_CSV,"toWrite in CSV: " + toWrite);

        try {
            FileOutputStream fos = openFileOutput(CSVFilename, Context.MODE_PRIVATE);
            fos.write(toWrite.getBytes());
            Log.d(TAG_CSV,"Scrittura riuscita del CVS.");
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG_CSV,"Eccezione apertura file " + CSVFilename);
            e.printStackTrace();
        } catch (IOException e) {
            Log.d(TAG_CSV,"Eccezione scrittura di: " + toWrite);
            e.printStackTrace();
        }
    }

    private View.OnClickListener ExportPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            getCategories(categories);
            getExpenses(expenses);

            writeCSV();

        }
    };

    private static final int READ_REQUEST_CODE = 42;

    private View.OnClickListener ImportPressed = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/csv");
            startActivityForResult(Intent.createChooser(intent, "Open CSV"), READ_REQUEST_CODE);
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == READ_REQUEST_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                Log.d(TAG_CSV, "entrato nel result del ACTION_GET_CONTENT");
                //File file = new File(data.getData().getPath());
                Uri filepath = data.getData();
                proImportCSV(filepath);
            }
        }
    }

    private void proImportCSV(Uri from){

        boolean readExpense = false;
        try {
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            InputStream is = null;

            is = getContentResolver().openInputStream(from);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while((line = br.readLine()) != null) {
                Log.d(TAG_CSV,"Linea letta: " + line);
                String[] row = line.split(";");

                if(row[0].equals("CATEGORIES") || row[0].equals("CATEGORY_ID"))
                    continue;

                if(row[0].equals("EXPENSES") || row[0].equals("EXPENSE_ID")) {
                    readExpense = true;
                    continue;
                }
                Log.d(TAG_CSV,"Sto leggendo expense? " + readExpense + " e Row[0] vale: " + row[0]);
                if(!readExpense) {
                    ContentValues values = new ContentValues();
                    values.put(DbConfig.Category.COLUMN_NAME_NAME, row[1]);

                    long newRowId = db.insert(DbConfig.Category.TABLE_NAME, null, values);
                    if(newRowId == -1)
                        Log.d(TAG_CSV, "Categoria già esistente.");

                }
                else {
                    ContentValues values = new ContentValues();
                    //2;19/07/2018;World of Warcraft adventure game;1939;5

                    String correctDate = row[1].replaceAll("/","-");
                    values.put(DbConfig.Expenses.COLUMN_NAME_DATE, correctDate);
                    /*if(row[2].equals(""))
                        values.put(DbConfig.Expenses.COLUMN_NAME_NOTES,"");
                    else*/
                    values.put(DbConfig.Expenses.COLUMN_NAME_NOTES,row[2]);
                    Double correctAmount = Double.parseDouble(row[3]);
                    values.put(DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT,correctAmount);
                    Integer correctCategory = Integer.parseInt(row[4]);
                    values.put(DbConfig.Expenses.COLUMN_NAME_CATEGORY,correctCategory);

                    long newRowId = db.insert(DbConfig.Expenses.TABLE_NAME, null, values);

                    if(newRowId == -1)
                        Log.d(TAG_CSV, "Vincolo violato nell'inserimento");
                }
            }

        } catch (Exception e) { Log.e("TAG",e.toString());

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cvs);

        dbHelper = new DbReaderDbHelper(getApplicationContext());

        Button btnExport = findViewById(R.id.btn_Export);
        btnExport.setOnClickListener(ExportPressed);

        Button btnImport = findViewById(R.id.btn_Import);
        btnImport.setOnClickListener(ImportPressed);

    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
