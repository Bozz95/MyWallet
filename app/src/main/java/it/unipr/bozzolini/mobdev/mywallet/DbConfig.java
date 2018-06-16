package it.unipr.bozzolini.mobdev.mywallet;

import android.provider.BaseColumns;

public final class DbConfig {

    private DbConfig() {}


    public final class Category implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_NAME = "category_name";
        public static final String COLUMN_NAME_ID = "category_id";
    }

    public final class Expenses implements BaseColumns {
        public static final String TABLE_NAME = "expenses";
        public static final String COLUMN_NAME_DATE = "expense_date";
        public static final String COLUMN_NAME_ID = "expense_id";
        public static final String COLUMN_NAME_NOTES = "expense_notes";
        public static final String COLUMN_NAME_CENTS_AMOUNT = "expense_cents_amount";
        public static final String COLUMN_NAME_CATEGORY = "expense_category";
    }
}
