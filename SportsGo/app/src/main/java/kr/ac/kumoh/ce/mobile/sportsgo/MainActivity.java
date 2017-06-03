package kr.ac.kumoh.ce.mobile.sportsgo;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//       setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) { super(fm); }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new MainPage();
                case 1:
                    return new MyPage();
            }
            return null;
        }

        @Override
        public int getCount() { return 2; }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "MainPage 1";
                case 1:
                    return "MyPage 2";
            }
            return null;
        }
    }
    public static class ListItem {

        private String[] mData;

        public ListItem(String[] data ){
            mData = data;
        }

        public ListItem( String txt1, String txt2,String txt3){

            mData = new String[3];
            mData[0] = txt1;
            mData[1] = txt2;
            mData[2] = txt3;

        }
        public ListItem( String txt1, String txt2,String txt3,String txt4,String txt5,String txt6,String txt7,String txt8 ){

            mData = new String[8];
            mData[0] = txt1;
            mData[1] = txt2;
            mData[2] = txt3;
            mData[3] = txt4;
            mData[4] = txt5;
            mData[5] = txt6;
            mData[6] = txt7;
            mData[7] = txt8;

        }
        public ListItem( String txt1, String txt2, String txt3, String txt4,String txt5){

            mData = new String[5];
            mData[0] = txt1;
            mData[1] = txt2;
            mData[2] = txt3;
            mData[3] = txt4;
            mData[4] = txt5;

        }
        public String[] getData(){
            return mData;
        }
        public String getData(int index){
            return mData[index];
        }
        public void setData(String[] data){
            mData = data;
        }
    }//end listitem

}
