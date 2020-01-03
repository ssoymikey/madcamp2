package com.example.project2.Adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.project2.Fragment.AddressFragment;
import com.example.project2.Fragment.ImageFragment;
import com.example.project2.Fragment.MemoFragment;

public class ContentsPagerAdapter extends FragmentStatePagerAdapter {
    private int tabCount;

    public ContentsPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AddressFragment addressFragment = new AddressFragment();
                return addressFragment;

            case 1:
                ImageFragment imageFragment = new ImageFragment();
                return imageFragment;

            case 2:
                MemoFragment memoFragment = new MemoFragment();
                return memoFragment;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }
}
