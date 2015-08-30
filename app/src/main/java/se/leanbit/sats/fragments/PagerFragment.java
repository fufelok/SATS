package se.leanbit.sats.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.leanbit.sats.R;

public class PagerFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();
        int position = args.getInt("page_position");
        CustomCircleDraw fragmentLayout = (CustomCircleDraw)inflater.inflate(R.layout.pager_draw_circle, container, false);
        setFillOnCircle(position,fragmentLayout);
        fragmentLayout.setWeekDates(args.getString("week_dates"));
        fragmentLayout.setIsCurrentWeek(args.getBoolean("is_current_week"));
        fragmentLayout.setMaxAntalPass(args.getInt("max_antal_pass"));
        fragmentLayout.setAntalPass(args.getInt("pass_per_vecka"));
        fragmentLayout.setPassNextWeek(args.getInt("pass_next_week"));
        fragmentLayout.setPassLastWeek(args.getInt("pass_last_week"));
        fragmentLayout.isPastWeek(args.getBoolean("is_past_Week"));
        fragmentLayout.isLastBeforeWeek(args.getBoolean("is_last_before_week"));

        changeColor(position, fragmentLayout);

        return fragmentLayout;
    }

    private void setFillOnCircle(int position,CustomCircleDraw circleView)
    {
        if(position%2==0)
        {
            circleView.drawCircleFill(true);
        }
        else
        {
            circleView.drawCircleFill(false);
        }
    }

    private View changeColor(int position, View view)
    {
        if(position%2==0)
        {
            view.setBackgroundColor(getResources().getColor(R.color.scroll_view_lightgrey));
        }
        else
        {
            view.setBackgroundColor(getResources().getColor(R.color.scroll_view_darkgrey));
        }

        return view;
    }
}
