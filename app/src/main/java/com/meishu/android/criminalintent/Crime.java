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
    private String suspected;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        this.id = id;
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

    public String getSuspected() {
        return suspected;
    }

    public void setSuspected(String suspected) {
        this.suspected = suspected;
    }
}
