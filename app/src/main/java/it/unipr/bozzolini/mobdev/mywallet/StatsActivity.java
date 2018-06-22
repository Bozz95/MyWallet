package it.unipr.bozzolini.mobdev.mywallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class StatsActivity extends AppCompatActivity {
    private Float total = 0.0f;
    private List<PieEntry> pieData = new ArrayList<>();
    private DbReaderDbHelper dbHelper;

    private void getData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String SELECT_TOTAL = "SELECT SUM("+ DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT + ") AS totale " +
                "FROM " + DbConfig.Expenses.TABLE_NAME +";";

        Cursor cursor = db.rawQuery(SELECT_TOTAL,null);

        if(cursor.moveToNext()) {
            total = cursor.getFloat(cursor.getColumnIndexOrThrow("totale"));
        }
        cursor.close();
        String SELECT_TOTAL_BY_CATEGORY = "SELECT SUM("+ DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT + ") AS percentuale, "  +
                DbConfig.Category.TABLE_NAME + "." + DbConfig.Category.COLUMN_NAME_NAME + " " +
                "FROM " + DbConfig.Expenses.TABLE_NAME + ", " + DbConfig.Category.TABLE_NAME +
                " WHERE " +  DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_CATEGORY + " = " +
                DbConfig.Category.TABLE_NAME + "." + DbConfig.Category.COLUMN_NAME_ID +
                " GROUP BY " + DbConfig.Category.TABLE_NAME + "." + DbConfig.Category.COLUMN_NAME_NAME + ";";
        cursor = db.rawQuery(SELECT_TOTAL_BY_CATEGORY, null);
        //sum(expense_cents_amount)
        while(cursor.moveToNext()) {
            pieData.add(new PieEntry((cursor.getFloat(cursor.getColumnIndexOrThrow("percentuale"))/total)*100, cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Category.COLUMN_NAME_NAME))));
        }

    }

    private void toastMessenger(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        dbHelper = new DbReaderDbHelper(getApplicationContext());

        getData();
        TextView myTextView = findViewById(R.id.textView7);
        myTextView.setText("Totale Spese: " + Float.toString(total/100) + " €");
        PieDataSet pieDataSet = new PieDataSet(pieData, "Percentuali");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        PieData labelledData = new PieData(pieDataSet);

        PieChart chart = findViewById(R.id.pieChart);
        chart.setData(labelledData);
        chart.animateY(1000);
        chart.invalidate();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
