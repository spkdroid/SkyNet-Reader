package com.news.skynet.viewpager;

/**
 * Created by Ramkumar Velmurugan on 2016-07-16.
 *
 *  This the pageAdapter class that will provide the fragment as a simple
 *
 *  tab view to the user.
 *
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.news.skynet.fragment.NewsFeed;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        //   Creating tabviews based on the selected screen

            if (position == 0) {
                NewsFeed tab1 = new NewsFeed("http://www.spkdroid.com/News/canada.php?type=1");
                return tab1;
            } else if (position == 1) {
                NewsFeed tab2 = new NewsFeed("http://www.spkdroid.com/News/canada.php?type=2");
                return tab2;
            } else if (position == 2) {
                NewsFeed tab3 = new NewsFeed("http://www.spkdroid.com/News/canada.php?type=3");
                return tab3;
            } else if (position == 3) {
                NewsFeed tab4 = new NewsFeed("http://www.spkdroid.com/News/canada.php?type=4");
                return tab4;
            } else if (position == 4) {
                NewsFeed tab5 = new NewsFeed("http://www.spkdroid.com/News/canada.php?type=5");
                return tab5;
            }
            else
            {
                NewsFeed tab6 = new NewsFeed("http://www.spkdroid.com/News/canada.php?type=5");
                return tab6;
            }

    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}