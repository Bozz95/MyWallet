package it.unipr.bozzolini.mobdev.mywallet;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowExpenses extends AppCompatActivity {
    private final String TAG_SHOWEXPENSE = "Show Expenses";
    private RecyclerView myRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private DbReaderDbHelper dbHelper;
    private RecycleviewAdapter recycleAdapter;

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            // Row is swiped from recycler view
            // remove it from adapter
            int position = viewHolder.getAdapterPosition();

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String DELETE_CONDITION = DbConfig.Expenses.COLUMN_NAME_ID + "= ?";
            String[] CONDITION_VALUE = {Integer.toString(dataset.get(position).getId())};
            int rows = db.delete(DbConfig.Expenses.TABLE_NAME,DELETE_CONDITION,CONDITION_VALUE);
            if(rows != 0)
                toastMessenger("Spesa Eliminata.");
            dataset.remove(position);
            recycleAdapter.notifyItemRemoved(position);
            recycleAdapter.notifyItemRangeChanged(position, dataset.size());
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            // view the background view
        }
    };

    private ArrayList<Expense> dataset;

    private ArrayList<Expense> getData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String SELECT_QUERY = "SELECT " +   DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_ID + ", " +
                                            DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_DATE + ", " +
                                            DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT + ", " +
                                            DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_NOTES + ", " +
                                            DbConfig.Category.TABLE_NAME + "." + DbConfig.Category.COLUMN_NAME_NAME + " " +
                "FROM " + DbConfig.Expenses.TABLE_NAME + ", " + DbConfig.Category.TABLE_NAME +
                " WHERE " +  DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_CATEGORY + " = " +
                            DbConfig.Category.TABLE_NAME + "." + DbConfig.Category.COLUMN_NAME_ID +
                " ORDER BY " + DbConfig.Expenses.TABLE_NAME + "." + DbConfig.Expenses.COLUMN_NAME_DATE + " DESC;";


        Cursor cursor = db.rawQuery(SELECT_QUERY,null);
        ArrayList<Expense> items = new ArrayList<Expense>();

        while (cursor.moveToNext()) {

            Integer expenseId = cursor.getInt(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_ID));
            String expenseDate = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_DATE));
            String expenseNotes = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_NOTES));
            String expenseCategory = cursor.getString(cursor.getColumnIndexOrThrow(DbConfig.Category.COLUMN_NAME_NAME));
            Double expenseAmount = cursor.getDouble(cursor.getColumnIndexOrThrow(DbConfig.Expenses.COLUMN_NAME_CENTS_AMOUNT));
            //int id, String date, String category, String notes, double amount
            items.add(new Expense(expenseId,expenseDate, expenseCategory, expenseNotes,expenseAmount));
        }

        return  items;
    }

    private void toastMessenger(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_SHORT).show();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_expenses);

        dbHelper = new DbReaderDbHelper(getApplicationContext());
        dataset = getData();

        if(dataset.size() == 0)
            toastMessenger("Non hai ancora inserito Spese.");

        myRecyclerView = findViewById(R.id.myRecycleView);

        myRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(myRecyclerView.getContext(), mLayoutManager.getOrientation());
        dividerItemDecoration.setDrawable(
                ContextCompat.getDrawable(
                        ShowExpenses.this,
                        R.drawable.recycler_separator
                )
        );

        //dividerItemDecoration.setDrawable(getContext().getResources().getDrawable(R.drawable.sk_line_divider));
        myRecyclerView.addItemDecoration(dividerItemDecoration);
        myRecyclerView.setItemAnimator(new DefaultItemAnimator());
        recycleAdapter = new RecycleviewAdapter(dataset);
        myRecyclerView.setAdapter(recycleAdapter);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(myRecyclerView);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}
