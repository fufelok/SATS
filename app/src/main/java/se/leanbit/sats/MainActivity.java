package se.leanbit.sats;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.*;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import se.leanbit.sats.adapters.CustomFragmentPagerAdapter;
import se.leanbit.sats.adapters.DrawerListAdapter;
import se.leanbit.sats.adapters.interfaces.PagerScrollListener;

import se.leanbit.sats.fragments.ListFragment;
import se.leanbit.sats.models.DrawerItem;
import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.models.SatsSimpleCenter;
import se.leanbit.sats.repositories.services.SatsActivitiesService;
import se.leanbit.sats.repositories.services.SatsTimeFormatService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;


public class MainActivity extends ActionBarActivity
{
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private RelativeLayout mDrawerPane;
    private ArrayList<Integer> listOfWeeks;
    private LinkedHashMap<Integer, Integer> weekMap;
    private SatsTimeFormatService satsTimeFormatService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //Load state and set content view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_container);

        //Initialize backend services and retrieve data
        final SatsActivitiesService satsActivitiesService = new SatsActivitiesService();
        final SatsActivity[] activities = satsActivitiesService.getActivitiesBetween("2015-03-01", "2015-06-30");
        satsTimeFormatService = new SatsTimeFormatService();
        final ArrayList<SatsActivity> listOfActivities = new ArrayList<>();

        //Populate and format services data
        satsActivitiesService.setFullCenterMap();
        makeListOfActivities(listOfActivities, activities);
        populateListOfWeeks(satsActivitiesService,activities);

        //Create the action bar (toolbar)
        toolbar = (Toolbar) findViewById(R.id.toolbar1);
        final ImageView toolbarSettingsIcon = (ImageView) findViewById(R.id.action_bar_logo_settings);
        final ImageView toolbarSatsIcon = (ImageView) findViewById(R.id.action_bar_logo_sats);
        final ImageView toolbarRefreshIcon = (ImageView) findViewById(R.id.action_bar_logo_refresh);
        setToolBar(toolbarSettingsIcon,toolbarSatsIcon,toolbarRefreshIcon);
        toolbar.inflateMenu(R.menu.menu_main);

        //Create the menu (drawer)
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        final ListView drawerList = (ListView) findViewById(R.id.navList);
        setDrawer(drawerList);

        //Create the View pager and StickyList fragments
        final Fragment mListFragment = new ListFragment();
        final CustomFragmentPagerAdapter adapter = new CustomFragmentPagerAdapter(getSupportFragmentManager(), this, listOfActivities, satsActivitiesService,satsTimeFormatService, activities, listOfWeeks, weekMap);
        final ImageView leftShadow = (ImageView) findViewById(R.id.shadow_left);
        final ImageView rightShadow = (ImageView) findViewById(R.id.shadow_right);
        final ImageView markerLeft = (ImageView) findViewById(R.id.marker_left);
        final ImageView markerRight = (ImageView) findViewById(R.id.marker_right);
        final ViewPager mViewPager = (ViewPager) findViewById(R.id.horizontal_view_pager);

        markerLeft.setOnClickListener(markersActionListener);
        markerRight.setOnClickListener(markersActionListener);

        drawShadows(leftShadow, rightShadow);
        makeViewPager(mViewPager, adapter,mListFragment,markerLeft,markerRight);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        fragmentTransaction.add(R.id.listfragment_container, mListFragment, "listFrag")
                .commit();
    }

    private void populateListOfWeeks(SatsActivitiesService satsActivitiesService, SatsActivity[] activities)
    {
        listOfWeeks = new ArrayList<>();
        weekMap = satsActivitiesService.getTraningMap(activities);
        for (Integer item : weekMap.keySet())
        {
            listOfWeeks.add(item);
        }
    }

    private void makeListOfActivities(ArrayList<SatsActivity> listOfActivities, SatsActivity[] activities)
    {
        for (int i = 0; i < activities.length; i++)
        {
            listOfActivities.add(activities[i]);
        }
    }

    private void setToolBar(ImageView toolbarSettingsIcon, ImageView toolbarSatsIcon, ImageView toolbarRefreshIcon)
    {
        setSupportActionBar(toolbar);
        toolbarRefreshIcon.setOnClickListener(actionBarRefreshListener);
        toolbarSettingsIcon.setOnClickListener(actionBarSettingsListener);
        toolbarSatsIcon.setOnClickListener(actionBarSettingsListener);
    }

    private void setDrawer(ListView drawerList)
    {
        mDrawerLayout.setScrimColor(Color.argb(100, 51, 51, 51));
        //mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerPane = (RelativeLayout) findViewById(R.id.drawerPane);

        ArrayList<DrawerItem> items = new ArrayList<>();
        items.add(new DrawerItem(getResources().getDrawable(R.drawable.maps_icon),"Karta","Hitta ditt närmaste\nSATS center!"));

        drawerList.setAdapter(new DrawerListAdapter(this, items));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {
               mDrawerLayout.closeDrawer(mDrawerPane);

               Intent myIntent = new Intent(MainActivity.this, MapViewActivity.class);
               MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void makeViewPager(ViewPager mViewPager, CustomFragmentPagerAdapter adapter,
                               final Fragment mListFragment,
                               final ImageView markerLeft,
                               final ImageView markerRight
    )
    {
        mViewPager.setAdapter(adapter);
        mViewPager.setCurrentItem(listOfWeeks.indexOf(satsTimeFormatService.getCurrentWeekNum()) - 2);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                PagerScrollListener listener = (PagerScrollListener) mListFragment;
                position += 2;
                int currentWeekPosition = listOfWeeks.indexOf(satsTimeFormatService.getCurrentWeekNum());
                setMarkerPosition(position, positionOffset, currentWeekPosition, markerLeft, markerRight);
                int cursorPosition = syncPosition(position);
                listener.onPagePositionChanged(cursorPosition);
                //Log.d("onPageScrolled", " " + position + " position" + cursorPosition + " cursorPosition " + positionOffset + " position offset " + " positionOffsetPixels" + positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position)
            {
            }

            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
    }

    private void drawShadows(ImageView leftShadow,ImageView rightShadow)
    {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        float screenWidth = displaymetrics.widthPixels;
        float dimenPix = getResources().getDimension(R.dimen.shadow_size);

        RelativeLayout.LayoutParams lpRight = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        RelativeLayout.LayoutParams lpLeft = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lpRight.width = (int) dimenPix;
        lpLeft.width = (int) dimenPix;
        lpRight.setMargins((int) (screenWidth / 5 * 2) - (int) dimenPix, (int) getResources().getDimension(R.dimen.height_of_top_rectangle), 0, (int) getResources().getDimension(R.dimen.height_of_bottom_rectangle));
        lpLeft.setMargins((int) (screenWidth / 5 * 3), (int) getResources().getDimension(R.dimen.height_of_top_rectangle), 0, (int) getResources().getDimension(R.dimen.height_of_bottom_rectangle));
        leftShadow.setLayoutParams(lpLeft);
        rightShadow.setLayoutParams(lpRight);
    }

    private int syncPosition(int position)
    {
        int cursorPosition = 0;
        int maxcursorPosition = 0;
        for (Integer key : weekMap.keySet())
        {
            maxcursorPosition += weekMap.get(key);
        }
        for (int i = 0; i < position; i++)
        {
            int weekNum = listOfWeeks.get(i);
            int passThisWeek = weekMap.get(weekNum);
            cursorPosition = cursorPosition + passThisWeek;
        }
        if (cursorPosition > maxcursorPosition - 1)
        {
            cursorPosition = maxcursorPosition - 1;
        }
        return cursorPosition;
    }


    private void setMarkerPosition(int position, float positionOffset, int currentWeekPosition, ImageView markerLeft, ImageView markerRight)
    {
        if (position < currentWeekPosition + 3)
        {
            if (positionOffset < 0.4)
            {
                markerLeft.setImageDrawable(null);
            }
        }
        if (position > currentWeekPosition + 1)
        {
            if (positionOffset > 0.3)
            {
                markerLeft.setImageResource(R.drawable.back_to_now_left);
            }
        }

        if (position > currentWeekPosition - 4)
        {
            if (positionOffset > 0.7)
            {
                markerRight.setImageDrawable(null);
            }
        }
        if (position < currentWeekPosition - 2)
        {
            if (positionOffset < 0.7)
            {
                markerRight.setImageResource(R.drawable.forward_to_now);
            }
        }
    }

    private View.OnClickListener actionBarRefreshListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate);

            ImageView toolbarRefreshIcon = (ImageView) v.findViewById(R.id.action_bar_logo_refresh);
            toolbarRefreshIcon.startAnimation(rotation);
        }
    };

    private View.OnClickListener actionBarSettingsListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            mDrawerLayout.openDrawer(mDrawerPane);
        }
    };

    private View.OnClickListener markersActionListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            final ViewPager mViewPager = (ViewPager) findViewById(R.id.horizontal_view_pager);
            mViewPager.setCurrentItem(listOfWeeks.indexOf(satsTimeFormatService.getCurrentWeekNum()) - 2);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent keyEvent)
    {
        if(keyEvent.getAction() == KeyEvent.ACTION_DOWN
            && keyEvent.getKeyCode() == KeyEvent.KEYCODE_MENU)
        {
            if (!mDrawerLayout.isDrawerOpen(mDrawerPane))
            {
                mDrawerLayout.openDrawer(mDrawerPane);
            }
            else
            {
                mDrawerLayout.closeDrawer(mDrawerPane);
            }
            return true;
        }
        return false;
    }
}
