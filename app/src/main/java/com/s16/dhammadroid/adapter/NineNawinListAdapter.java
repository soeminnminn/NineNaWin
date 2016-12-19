package com.s16.dhammadroid.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.s16.dhammadroid.Common;
import com.s16.dhammadroid.R;
import com.s16.dhammadroid.Utility;
import com.s16.dhammadroid.data.NineNawinResData;

/**
 * Created by SMM on 10/24/2016.
 */
public class NineNawinListAdapter extends ArrayAdapter<NineNawinResData.ItemDetail> {

    private final Typeface mTypeface;

    private Drawable mBkgGreen;
    private Drawable mBkgGreenSelected;
    private Drawable mBkgPink;
    private Drawable mBkgYellow;

    static class ViewHolder {
        TextView textViewDay;
        TextView textViewDate;
        TextView textViewValue;
        TextView textViewRound;
        TextView textViewNote;
        LinearLayout linearLayoutItem;
    }

    public NineNawinListAdapter(Context context, NineNawinResData.ItemDetail[] objects) {
        super(context, 0, objects);

        mTypeface = Common.getZawgyiTypeface(context);

        mBkgGreen = ContextCompat.getDrawable(context, R.drawable.ninenawin_bkg_green);
        mBkgGreenSelected = ContextCompat.getDrawable(context, R.drawable.ninenawin_bkg_green_selected);
        mBkgPink = ContextCompat.getDrawable(context, R.drawable.ninenawin_bkg_pink);
        mBkgYellow = ContextCompat.getDrawable(context, R.drawable.ninenawin_bkg_yellow);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_details_view, null);
            holder = new ViewHolder();

            holder.textViewDay = (TextView)convertView.findViewById(R.id.textViewDay);
            holder.textViewDate = (TextView)convertView.findViewById(R.id.textViewDate);
            holder.textViewValue = (TextView)convertView.findViewById(R.id.textViewValue);
            holder.textViewRound = (TextView)convertView.findViewById(R.id.textViewRound);
            holder.textViewNote = (TextView)convertView.findViewById(R.id.textViewNote);
            holder.linearLayoutItem = (LinearLayout)convertView.findViewById(R.id.linearLayoutItem);

            holder.textViewDay.setTypeface(mTypeface);
            holder.textViewDate.setTypeface(mTypeface);
            holder.textViewValue.setTypeface(mTypeface);
            holder.textViewRound.setTypeface(mTypeface);
            holder.textViewNote.setTypeface(mTypeface);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        final NineNawinResData.ItemDetail item = getItem(position);
        holder.textViewDay.setText(Utility.ZawGyiDrawFix(item.getDay()));
        holder.textViewDate.setText(item.getDateString());
        holder.textViewValue.setText(Utility.ZawGyiDrawFix(item.getValue()));
        holder.textViewRound.setText(Utility.ZawGyiDrawFix(item.getRound()));

        if(NineNawinResData.getIsToday(item)) {
            if(item.getIsVege()) {
                holder.textViewNote.setText("(" + Utility.ZawGyiDrawFix(getContext().getText(R.string.nn_vege_date)) + ")");
                holder.linearLayoutItem.setBackgroundDrawable(mBkgGreenSelected);
            } else {
                holder.textViewNote.setText(null);
                holder.linearLayoutItem.setBackgroundDrawable(mBkgGreen);
            }
        } else {
            if(item.getIsVege()) {
                holder.textViewNote.setText("(" + Utility.ZawGyiDrawFix(getContext().getText(R.string.nn_vege_date)) + ")");
                holder.linearLayoutItem.setBackgroundDrawable(mBkgPink);
            } else {
                holder.textViewNote.setText(null);
                holder.linearLayoutItem.setBackgroundDrawable(mBkgYellow);
            }
        }

        return convertView;
    }
}
