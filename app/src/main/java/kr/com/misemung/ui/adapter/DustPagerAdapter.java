package kr.com.misemung.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import kr.com.misemung.ui.DustFragment;

public class DustPagerAdapter extends FragmentStatePagerAdapter {

    private List<Pair<Fragment, String>> fragmentList;

    public DustPagerAdapter(FragmentManager fm, ArrayList<Pair<Fragment, String>> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int i) {

        return fragmentList.get(i).first;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    // Delete a page at a `position`
    public void deletePage(int position)
    {
        // Remove the corresponding item in the data set
        fragmentList.remove(position);
        // Notify the adapter that the data set is changed
        notifyDataSetChanged();
    }
}
