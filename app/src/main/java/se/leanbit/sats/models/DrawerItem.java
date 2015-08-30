package se.leanbit.sats.models;

import android.graphics.drawable.Drawable;

public class DrawerItem
{
    public final Drawable icon;
    public final String title,
                        description;
    public DrawerItem(final Drawable icon,
               final String title,
               final String description)
    {
        this.icon = icon;
        this.title = title;
        this.description = description;
    }
}
