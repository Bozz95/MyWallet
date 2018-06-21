package it.unipr.bozzolini.mobdev.mywallet;

public class Expense {

    private String date, category, notes;
    private int id;
    private double amount;

    public Expense() {
    }

    public Expense(int id, String date, String category, String notes, double amount) {
        this.date = date;
        this.category = category;
        this.notes = notes;

        this.id = id;
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        String app = Double.toString(amount/100) + " €";
        return app;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCategory() {

        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
