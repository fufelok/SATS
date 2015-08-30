package se.leanbit.sats.adapters;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import se.leanbit.sats.R;
import se.leanbit.sats.fragments.PagerFragment;
import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.repositories.services.SatsActivitiesService;
import se.leanbit.sats.repositories.services.SatsTimeFormatService;

public class CustomFragmentPagerAdapter extends FragmentStatePagerAdapter
{
    final ArrayList<SatsActivity> mListOfActivities;
    protected Context mContext;
    final SatsActivitiesService mSatsActivitiesService;
    final SatsTimeFormatService mSatsTimeFormatService;
    final SatsActivity[] mActivities;
    final LinkedHashMap<Integer, Integer> mWeekMap;
    final ArrayList<Integer> mListOfWeeks;

    public CustomFragmentPagerAdapter(FragmentManager fm, Context context
            ,ArrayList<SatsActivity> listOfActivities
            ,SatsActivitiesService satsActivitiesService
            ,SatsTimeFormatService satsTimeFormatService
            ,SatsActivity[] activities
            ,ArrayList<Integer> listOfWeeks
            ,LinkedHashMap<Integer, Integer> weekMap)    // Byt till ArrayList!!!
    {
        super(fm);
        mContext = context;
        this.mListOfActivities = listOfActivities;
        this.mSatsActivitiesService = satsActivitiesService;
        this.mSatsTimeFormatService = satsTimeFormatService;
        this.mActivities = activities;
        this.mListOfWeeks = listOfWeeks;
        this.mWeekMap = weekMap;
    }

    @Override
    public Fragment getItem(int position)
    {
        Fragment fragment = new PagerFragment();
        Bundle args = new Bundle();
        args.putInt("page_position", position + 1);
        args.putString("week_dates", mSatsTimeFormatService.getStartEndOFWeekByWeekNum(mListOfWeeks.get(position)));
        args.putBoolean("is_current_week", mSatsTimeFormatService.isCurrentWeek(mListOfWeeks.get(position)));
        args.putInt("max_antal_pass", mSatsActivitiesService.getMaxTraning(mActivities)); //Byt till ArrayList!!!
        args.putInt("pass_per_vecka", mWeekMap.get(mListOfWeeks.get(position)));
        args.putInt("week_num", mListOfWeeks.get(position));
        args.putBoolean("is_past_Week", mSatsTimeFormatService.weekIsPast(mListOfWeeks.get(position)));

        if((position +1)< mListOfWeeks.size())
        {
            args.putBoolean("is_last_before_week",mSatsTimeFormatService.isCurrentWeek(mListOfWeeks.get(position+1)));
        }
        if(position < mWeekMap.size()-1)
        {
            args.putInt("pass_next_week", mWeekMap.get(mListOfWeeks.get(position+1)));
        }
        else
        {
            args.putInt("pass_next_week", -1);
        }

        if(position ==0)
        {
            args.putInt("pass_last_week", -1);
        }
        else
        {
            args.putInt("pass_last_week", mWeekMap.get(mListOfWeeks.get(position-1)));
        }
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public int getCount()
    {
        return mListOfWeeks.size();
    }

    @Override
    public float getPageWidth(int position){
        return 0.20f;
    }

}
