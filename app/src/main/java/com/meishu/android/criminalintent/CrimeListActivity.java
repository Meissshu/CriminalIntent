package com.meishu.android.criminalintent;

import android.content.Intent;
import android.support.v4.app.Fragment;

/**
 * Created by Meishu on 17.03.2017.
 */

public class CrimeListActivity extends SingleFragmentActivity implements CrimeListFragment.Callbacks {
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_masterdetail;
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if (findViewById(R.id.detail_fragmentContainer) == null) {
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragmentContainer, newDetail)
                    .commit();
        }
    }
}
