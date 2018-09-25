package cis.co.kr.ciscultureinseoul.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import cis.co.kr.ciscultureinseoul.fragment.ViewP1Fragment;
import cis.co.kr.ciscultureinseoul.fragment.ViewP2Fragment;


public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ViewP1Fragment.getInstance();
        } else {
            return ViewP2Fragment.getInstance();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
