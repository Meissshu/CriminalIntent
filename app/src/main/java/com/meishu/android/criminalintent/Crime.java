package com.meishu.android.criminalintent;

import java.util.UUID;

/**
 * Created by Meishu on 16.03.2017.
 */

public class Crime {

    private UUID id;
    private String title;

    public Crime() {
        id = UUID.randomUUID();
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
}
