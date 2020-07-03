package com.example.interceptevents_tvn;



public class ItemData {

    private String title;
    private boolean checked;

    public ItemData(String title, boolean checked) {
        this.title = title;
        this.checked = checked;
    }

    public String getTitle() {
        return title;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
