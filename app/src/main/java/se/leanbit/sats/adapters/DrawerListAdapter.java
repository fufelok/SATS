package se.leanbit.sats.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import se.leanbit.sats.R;
import se.leanbit.sats.models.DrawerItem;

import java.util.ArrayList;

public class DrawerListAdapter extends BaseAdapter
{
    private Context mContext;
    private ArrayList<DrawerItem> mItems;

    public DrawerListAdapter(Context context, ArrayList<DrawerItem> items)
    {
        mContext = context;
        mItems = items;
    }

    @Override
    public int getCount()
    {
        return mItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.drawer_item, null);
        }

        ImageView icon = (ImageView) convertView.findViewById(R.id.drawer_item_image);
        TextView title = (TextView) convertView.findViewById(R.id.drawer_item_title);
        TextView description = (TextView) convertView.findViewById(R.id.drawer_item_description);

        icon.setImageDrawable(mItems.get(position).icon);
        title.setText(mItems.get(position).title);
        description.setText(mItems.get(position).description);

        return convertView;
    }
}
