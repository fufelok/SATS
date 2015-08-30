package se.leanbit.sats.fragments;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import java.util.ArrayList;

import se.emilsjolander.stickylistheaders.StickyListHeadersListView;

import se.leanbit.sats.R;
import se.leanbit.sats.adapters.StickyListAdapter;
import se.leanbit.sats.adapters.interfaces.PagerScrollListener;
import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.repositories.services.SatsActivitiesService;

public class ListFragment extends Fragment implements PagerScrollListener
{
    StickyListHeadersListView mList;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
       final SatsActivitiesService satsActivitiesService = new SatsActivitiesService();
        SatsActivity[] activities = satsActivitiesService.getActivitiesBetween("2015-03-01","2015-06-30");

        final ArrayList<SatsActivity> listOfActivities = new ArrayList<>();
        for(int i = 0; i < activities.length; i++)
        {
            listOfActivities.add(activities[i]);
        }

        View view = inflater.inflate(R.layout.stickylist_headersview, container, false);

        StickyListHeadersListView stickyList = (StickyListHeadersListView) view;
        StickyListAdapter activityListAdapter = new StickyListAdapter(getActivity(),listOfActivities);
        stickyList.setAdapter(activityListAdapter);
        stickyList.setOnScrollListener(new AbsListView.OnScrollListener()
        {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount)
            {
               // Log.d("onScroll ", firstVisibleItem + "-" + visibleItemCount + "-" + totalItemCount + (satsActivitiesService.isPast(listOfActivities.get(firstVisibleItem))));
                if (satsActivitiesService.isPast(listOfActivities.get(firstVisibleItem)))
                {
                    TextView tv = (TextView) view.getRootView().findViewById(R.id.header_text);
                    tv.setText("TIDIGARE TRÄNING");
                }
                else
                {
                    TextView tv = (TextView) view.getRootView().findViewById(R.id.header_text);
                    tv.setText("KOMMANDE TRÄNING");
                }
            }
        });
        int mPosition = 0;
        mList = (StickyListHeadersListView)view;
        mList.setSelection(mPosition);

        return view;
    }

    public void onPagePositionChanged(int position){
        mList.setSelection(position);
    }
}
