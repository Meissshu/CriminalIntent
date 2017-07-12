package com.meishu.android.criminalintent;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.meishu.android.criminalintent.database.CrimeBaseHelper;
import com.meishu.android.criminalintent.database.CrimeDbScheme.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Meishu on 17.03.2017.
 */

public class CrimeLab {

    private static CrimeLab crimeLab;
    private Context context;
    private SQLiteDatabase database;

    private CrimeLab(Context context) {
        this.context = context.getApplicationContext();
        database = new CrimeBaseHelper(this.context).getWritableDatabase();
//        for (int i = 0; i < 100; ++i) {
//            Crime crime = new Crime();
//            crime.setTitle("Crime №" + i);
//            crime.setSolved(i % 2 == 0);
//            crimes.add(crime);
//        }
    }

    public void addCrime(Crime c) {
        ContentValues values = getContentValues(c);
        database.insert(CrimeTable.NAME, null, values);
    }

    public static CrimeLab get(Context context) {
        if (crimeLab == null) {
            crimeLab = new CrimeLab(context);
        }
        return crimeLab;
    }


    public List<Crime> getCrimes() {
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id) {

        return null;
    }

    private static ContentValues getContentValues(Crime crime) {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);

        return values;
    }

    private Cursor query(String where, String whereArgs[]) {
        return database.query(CrimeTable.NAME, null, where, whereArgs, null, null, null);
    }


    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        database.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[]{uuidString});
    }


}
