package com.example.encryptaapplication.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

class SectionsPagerAdapter extends FragmentPagerAdapter {


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FriendRequestsFragment requestsFragment = new FriendRequestsFragment();
                return requestsFragment;
            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return  contactsFragment;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Friend Requests";
            case 1:
                return  "Contacts";
            default:
                return null;
        }

    }
}