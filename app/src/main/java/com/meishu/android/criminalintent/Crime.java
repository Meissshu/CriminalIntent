package com.meishu.android.criminalintent;

import java.util.Date;
import java.util.UUID;

/**
 * Created by Meishu on 16.03.2017.
 */

public class Crime {

    private UUID id;
    private String title;
    private boolean solved;
    private Date date;

    public Crime() {
        id = UUID.randomUUID();
        date = new Date();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UUID getId() {

        return id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}