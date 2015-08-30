package se.leanbit.sats.adapters;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;

import org.w3c.dom.Text;
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.leanbit.sats.R;
import se.leanbit.sats.models.SatsActivity;
import se.leanbit.sats.repositories.services.SatsActivitiesService;
import se.leanbit.sats.repositories.services.SatsTimeFormatService;
import se.leanbit.sats.BookedActivity;

public class StickyListAdapter extends BaseAdapter implements StickyListHeadersAdapter
{
    private static final int PAST_ACTIVITY = 0;
    private static final int CUSTOM_ACTIVITY = 1;
    private static final int SATS_ACTIVITY = 2;
    private LayoutInflater inflater;
    private SatsActivitiesService satsActivitiesService;
    private SatsTimeFormatService satsTimeFormatService;
    private ArrayList<SatsActivity> mActivityList;


    public StickyListAdapter(Context context, ArrayList<SatsActivity> list)
    {
        inflater = LayoutInflater.from(context);
        this.mActivityList = list;
        satsActivitiesService = new SatsActivitiesService();
        satsTimeFormatService = new SatsTimeFormatService();
    }

    @Override
    public long getHeaderId(int i)
    {
        if (satsActivitiesService.isPast(mActivityList.get(i)))
        {
            return satsTimeFormatService.getWeekNum(mActivityList.get(i));
        }
        return i;
    }

    @Override
    public int getCount()
    {
        return mActivityList.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mActivityList.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {

        ViewHolderPast pastHolder;
        ViewHolderFuture futureHolder;
        ViewHolderCustom customHolder;


        if (getItemViewType(position) == PAST_ACTIVITY)
        {

            if (convertView == null)
            {
                pastHolder = new ViewHolderPast();
                convertView = inflater.inflate(R.layout.past_activity, parent, false);
                pastHolder.textName = (TextView) convertView.findViewById(R.id.past_activity_name);
                pastHolder.textDate = (TextView) convertView.findViewById(R.id.past_activity_date);
                pastHolder.textComment = (TextView) convertView.findViewById(R.id.past_activity_comment_text);
                pastHolder.textCompleted = (TextView) convertView.findViewById(R.id.past_activity_checkmark_text);
                pastHolder.imageCheck = (ImageView) convertView.findViewById(R.id.past_activity_checkmark_image);
                pastHolder.imageMan = (ImageView) convertView.findViewById(R.id.past_activity_image);

                convertView.setOnClickListener(pastActivityCheckboxAction);

                pastHolder = setHolderText(pastHolder, position);
                convertView.setTag(pastHolder);
            }
            else
            {

                pastHolder = (ViewHolderPast) convertView.getTag();
                pastHolder = setHolderText(pastHolder, position);
                convertView.setTag(pastHolder);
            }
            return convertView;

        }
        else
        {

            if (getItemViewType(position) == CUSTOM_ACTIVITY)
            {
                if (convertView == null)
                {
                    customHolder = new ViewHolderCustom();
                    convertView = inflater.inflate(R.layout.custom_activity, parent, false);
                    customHolder.textName = (TextView) convertView.findViewById(R.id.custom_activity_name);
                    customHolder.textDuration = (TextView) convertView.findViewById(R.id.custom_activity_duration_text);
                    customHolder.textComment = (TextView) convertView.findViewById(R.id.custom_activity_comment_text);
                    customHolder.buttonDetails = (Button) convertView.findViewById(R.id.custom_activity_button_details);
                    customHolder.textCalendar = (TextView) convertView.findViewById(R.id.custom_activity_calendar_text);
                    customHolder.textTrainingProgram = (TextView) convertView.findViewById((R.id.custom_activity_workout_text));
                    convertView.setTag(customHolder);
                    customHolder = setCustomHolderText(customHolder, position);

                }
                else
                {
                    customHolder = (ViewHolderCustom) convertView.getTag();
                    customHolder = setCustomHolderText(customHolder, position);
                }
                return convertView;
            }
            else
            {
                if (convertView == null)
                {
                    futureHolder = new ViewHolderFuture();
                    convertView = inflater.inflate(R.layout.future_activity, parent, false);
                    futureHolder.textName = (TextView) convertView.findViewById(R.id.future_activity_name);
                    futureHolder.textInstructor = (TextView) convertView.findViewById(R.id.future_activity_instructor);
                    futureHolder.textRegion = (TextView) convertView.findViewById(R.id.future_activity_region);
                    futureHolder.textDuration = (TextView) convertView.findViewById(R.id.future_activity_duration_text);
                    futureHolder.textQue = (TextView) convertView.findViewById(R.id.future_activity_que_text);
                    futureHolder.textCalendar = (TextView) convertView.findViewById(R.id.future_activity_calendar_text);
                    futureHolder.textPass = (TextView) convertView.findViewById(R.id.future_activity_workout_text);
                    futureHolder.buttonCancel = (Button) convertView.findViewById(R.id.future_activity_button_cancel);
                    futureHolder.textHour = (TextView) convertView.findViewById(R.id.future_activity_hour_text);
                    futureHolder.textMinutes = (TextView) convertView.findViewById(R.id.future_activity_minute_text);
                    futureHolder.imageQue = (ImageView) convertView.findViewById(R.id.future_activity_que_image);
                    futureHolder.workOut = (ImageView) convertView.findViewById(R.id.future_activity_workout_image);
                    futureHolder.arrayIndex = (TextView) convertView.findViewById(R.id.future_activity_array_index);

                    futureHolder.workOut.setOnClickListener(startBookedActivityAction);

                    convertView.setTag(futureHolder);
                    futureHolder = setFutureViewHolder(futureHolder, position);

                }
                else
                {
                    futureHolder = (ViewHolderFuture) convertView.getTag();
                    futureHolder = setFutureViewHolder(futureHolder, position);
                }
                return convertView;
            }
        }
    }

    private ViewHolderFuture setFutureViewHolder(ViewHolderFuture futureHolder, int position)
    {
        futureHolder.textName.setText(satsActivitiesService.getActivityName(mActivityList.get(position)));
        futureHolder.textInstructor.setText(satsActivitiesService.instructor(mActivityList.get(position)));
        futureHolder.textRegion.setText(satsActivitiesService.getRegion(mActivityList.get(position)));
        futureHolder.textDuration.setText("" + satsActivitiesService.duration(mActivityList.get(position)) + " min");
        int queCounter = satsActivitiesService.que(mActivityList.get(position));

        if(queCounter == 0)
        {
            futureHolder.textQue.setText("");
            futureHolder.imageQue.setImageResource(R.drawable.done_2_icon);
        }
        else
        {
            futureHolder.textQue.setText(""+ queCounter);
            futureHolder.imageQue.setImageResource(R.drawable.icon_queue);
        }

        futureHolder.textCalendar.setText("Lägg till i kalender");
        futureHolder.textPass.setText("Mer om passet");
        futureHolder.buttonCancel.setText("Avboka");
        futureHolder.textHour.setText(satsTimeFormatService.getHoursMinutes(mActivityList.get(position))[0]);
        futureHolder.textMinutes.setText(satsTimeFormatService.getHoursMinutes(mActivityList.get(position))[1]);
        futureHolder.arrayIndex.setText(""+position);
        return futureHolder;
    }

    private ViewHolderCustom setCustomHolderText(ViewHolderCustom customHolder, int position)
    {
        customHolder.textName.setText(satsActivitiesService.getActivityName(mActivityList.get(position)));
        customHolder.textDuration.setText(satsActivitiesService.duration(mActivityList.get(position)) + " min");
        customHolder.buttonDetails.setText("Detaljer");
        customHolder.textCalendar.setText("Lägg till i kalender");
        customHolder.textTrainingProgram.setText("Träningsprogram");

        if (satsActivitiesService.comments(mActivityList.get(position)))
        {
            customHolder.textComment.setText("1 kommentar");
        }
        else
        {
            customHolder.textComment.setText("Lägg till kommentar");
        }
        return customHolder;
    }

    private ViewHolderPast setHolderText(ViewHolderPast pastHolder, int position)
    {
        String activityName = satsActivitiesService.getActivityName(mActivityList.get(position));
        String activityTypeGroup = satsActivitiesService.getGroupType(mActivityList.get(position));

        pastHolder.textName.setText(activityName);
        pastHolder.textDate.setText(satsTimeFormatService.getDayName(mActivityList.get(position)) + " " + satsTimeFormatService.getDate(mActivityList.get(position)));

        switch (activityTypeGroup)
        {
            case "GROUP":
                pastHolder = setPictureOfGroup(activityName, pastHolder);
                break;
            case "GYM":
                pastHolder = setPictureOfGym(activityName, pastHolder);
                break;
            case "OTHER":
                pastHolder = setPictureOfOther(activityName, pastHolder);
                break;
            default:
                pastHolder.imageMan.setImageResource(R.drawable.all_training_icons);
        }

        if (satsActivitiesService.comments(mActivityList.get(position)))
        {
            pastHolder.textComment.setText("1 kommentar");
        }
        else
        {
            pastHolder.textComment.setText("Lägg till kommentar");
        }

        if (satsActivitiesService.isCompleted(mActivityList.get(position)))
        {
            pastHolder.textCompleted.setText("Avklarat!");
            pastHolder.imageCheck.setImageResource(R.drawable.done_icon);
            pastHolder.imageCheck.setTag(1);
        }
        else
        {
            pastHolder.textCompleted.setText("Avklarat?");
            pastHolder.imageCheck.setImageResource(R.drawable.checkmark_button_normal);
            pastHolder.imageCheck.setTag(0);

        }
        return pastHolder;
    }
    private ViewHolderPast setPictureOfOther(String activityName, ViewHolderPast pastHolder)
    {
        if(activityName.equals("walking"))
        {
            pastHolder.imageMan.setImageResource(R.drawable.running_icon);
        }
        else if (activityName.equals("football"))
        {
            pastHolder.imageMan.setImageResource(R.drawable.all_training_icons);
        }
        else if(activityName.equals("cycle"))
        {
            pastHolder.imageMan.setImageResource(R.drawable.cykling_icon);
        }
        else
        {
            pastHolder.imageMan.setImageResource(R.drawable.all_training_icons);
        }
        return pastHolder;
    }

    private ViewHolderPast setPictureOfGym(String activityName, ViewHolderPast pastHolder)
    {
        pastHolder.imageMan.setImageResource(R.drawable.all_training_icons);
        return pastHolder;
    }

    private ViewHolderPast setPictureOfGroup(String activityName , ViewHolderPast pastHolder)
    {
        if(activityName.equals("SatsCycling")|| activityName.equals("Easy Cycling")|| activityName.equals("Cycling Pulse" ))
        {
            pastHolder.imageMan.setImageResource(R.drawable.cykling_icon);
        }
        else if(activityName.equals("shape") )
        {
            pastHolder.imageMan.setImageResource(R.drawable.strength_trainging_icon);
        }
        else if (activityName.equals("GROUP"))
        {
            pastHolder.imageMan.setImageResource(R.drawable.group_training_icon);
        }
        else
        {
            pastHolder.imageMan.setImageResource(R.drawable.all_training_icons);
        }
        return pastHolder;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent)
    {
        HeaderViewHolder holder;
        String headerText;

        if (convertView == null)
        {
            holder = new HeaderViewHolder();
            convertView = inflater.inflate(R.layout.header_view, parent, false);
            holder.text = (TextView) convertView.findViewById(R.id.text_header);
            convertView.setTag(holder);
        }
        else
        {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        if(satsActivitiesService.isPast(mActivityList.get(position)))
        {
            headerText = "Vecka " + satsTimeFormatService.getWeekNum(mActivityList.get(position)) + " ( " + satsTimeFormatService.getWeekDates(mActivityList.get(position)) + " ) ";
        }
        else
        {
            if(satsTimeFormatService.isToday(mActivityList.get(position)))
            {
                headerText = "Idag, " + satsTimeFormatService.getDayName(mActivityList.get(position)) + "  " + satsTimeFormatService.getDate(mActivityList.get(position));
            }
            else
            {
                headerText = satsTimeFormatService.getDayName(mActivityList.get(position)) + "  " + satsTimeFormatService.getDate(mActivityList.get(position));
            }
        }
        holder.text.setText(headerText);
        return convertView;
    }

    public int getItemViewType(int position)
    {
        if (satsActivitiesService.isPast(mActivityList.get(position)))
        {
            return PAST_ACTIVITY;
        }
        else
        {
            if ((satsActivitiesService.isCustom(mActivityList.get(position))))
            {
                return CUSTOM_ACTIVITY;
            }
            else
            {
                return SATS_ACTIVITY;
            }

        }
    }


    public int getViewTypeCount()
    {
        return 3;
    }


    private View.OnClickListener pastActivityCheckboxAction = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            ImageView image = (ImageView) view.findViewById(R.id.past_activity_checkmark_image);
            TextView text = (TextView) view.findViewById(R.id.past_activity_checkmark_text);
            if ((int) image.getTag() == 0)
            {
                image.setImageResource(R.drawable.done_icon);
                image.setTag(1);
                text.setText("Avklarat!");
            } else
            {
                image.setImageResource(R.drawable.checkmark_button_normal);
                image.setTag(0);
                text.setText("Avklarat?");
            }
        }
    };

    private View.OnClickListener startBookedActivityAction = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            Intent intent = new Intent(view.getContext(), BookedActivity.class);

            TextView tv = (TextView) view.getRootView().findViewById(R.id.future_activity_array_index);
            int index = Integer.parseInt((String) tv.getText());

            SatsActivity activity = mActivityList.get(index);
            intent.putExtra("Activity", activity);
            view.getContext().startActivity(intent);
        }
    };

    private class ViewHolderFuture
    {
        TextView textName;
        TextView textInstructor;
        TextView textRegion;
        TextView textDuration;
        TextView textQue;
        ImageView imageQue;
        TextView textCalendar;
        TextView textPass;
        Button buttonCancel;
        TextView textHour;
        TextView textMinutes;
        TextView arrayIndex;
        ImageView workOut;
    }

    private class ViewHolderCustom
    {
        TextView textName;
        TextView textDuration;
        Button buttonDetails;
        TextView textComment;
        TextView textCalendar;
        TextView textTrainingProgram;
    }

    private class HeaderViewHolder
    {
        TextView text;
    }

    private class ViewHolderPast
    {
        TextView textName;
        TextView textDate;
        TextView textComment;
        TextView textCompleted;
        ImageView imageMan;
        ImageView imageCheck;
    }


}