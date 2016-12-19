package com.s16.dhammadroid.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.AlertDialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.s16.dhammadroid.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by SMM on 10/26/2016.
 */
public class NineNawinSetDateFragment extends AlertDialogFragment {

    private static int BASE_ID = 0x1000;

    public interface OnSetDateListener {
        public void onSetDate(Calendar value);
        public void onClearDate();
    }

    private View.OnClickListener mButtonClearClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            performClear();
        }
    };

    private View.OnClickListener mButtonPreviousClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showPrevious();
        }
    };

    private View.OnClickListener mButtonNextClick = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            showNext();
        }
    };

    private TextView mMonthTitle;
    private RadioGroup mHolder;

    private OnSetDateListener mOnSetDateListener;
    private Calendar mCurrent;
    private List<Calendar> mArrDate;

    private static Calendar dateClone(Calendar value) {
        if(value == null) return null;

        Calendar refDate = Calendar.getInstance();
        refDate.set(value.get(Calendar.YEAR), value.get(Calendar.MONTH), value.get(Calendar.DATE));

        return refDate;
    }

    private static String getDateString(Calendar value) {
        if(value == null) return "";
        int day = value.get(Calendar.DAY_OF_MONTH);
        int month = value.get(Calendar.MONTH) + 1;
        return ((day < 10) ? "0" + day : day) + "/" + ((month < 10) ? "0" + month : month) + "/" + value.get(Calendar.YEAR);
    }

    private static int dateCompare(Calendar value1, Calendar value2) {
        if((value1 == null) && (value2 == null)) return 0;
        if(value2 == null) return -1;
        if(value1 == null) return 1;

        int day1 = value1.get(Calendar.DAY_OF_MONTH);
        int month1 = value1.get(Calendar.MONTH);
        int year1 = value1.get(Calendar.YEAR);

        int day2 = value2.get(Calendar.DAY_OF_MONTH);
        int month2 = value2.get(Calendar.MONTH);
        int year2 = value2.get(Calendar.YEAR);

        if(year1 > year2) return -1;
        if(year1 < year2) return 1;

        if(year1 == year2) {
            if(month1 > month2) return -1;
            if(month1 < month2) return 1;

            if(month1 == month2) {
                if(day1 > day2) return -1;
                if(day1 < day2) return 1;

                if(day1 == day2) return 0;
            }
        }

        return 0;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.action_set_date);

        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                performAccept();
                dialog.dismiss();
            }
        });
        setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_nn_dateselect, container, false);

        View buttonClear = rootView.findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(mButtonClearClick);

        View btnPrevious = rootView.findViewById(R.id.buttonPrevious);
        btnPrevious.setOnClickListener(mButtonPreviousClick);

        View btnNext = rootView.findViewById(R.id.buttonNext);
        btnNext.setOnClickListener(mButtonNextClick);

        mMonthTitle = (TextView)rootView.findViewById(R.id.textMonth);
        mHolder = (RadioGroup)rootView.findViewById(R.id.groupSetDate);

        createList();

        return rootView;
    }

    public void setOnSetDateListener(OnSetDateListener listener) {
        mOnSetDateListener = listener;
    }

    protected int getArgsSelectDay() {
        Bundle extras = getArguments();
        if (extras != null) {
            return extras.getInt("selectDay");
        } else {
            return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        }
    }

    protected Calendar getArgsSelectedDate() {
        Bundle extras = getArguments();
        if (extras != null) {
            long milliseconds = extras.getLong("selectedDate");
            Calendar value = Calendar.getInstance();
            value.setTimeInMillis(milliseconds);
            return value;
        } else {
            return Calendar.getInstance();
        }
    }

    private void createList() {
        if (mCurrent == null) {
            Calendar rightNow = Calendar.getInstance();
            int month = rightNow.get(Calendar.MONTH);
            mCurrent = Calendar.getInstance();
            mCurrent.set(rightNow.get(Calendar.YEAR), month, 1);
        }

        Calendar first = dateClone(mCurrent);
        int monthNow = first.get(Calendar.MONTH);
        mMonthTitle.setText(first.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault()));

        int selectday = getArgsSelectDay();
        int dayOfWeek = first.get(Calendar.DAY_OF_WEEK);
        while(dayOfWeek != selectday) {
            first.roll(Calendar.DAY_OF_YEAR, true);
            dayOfWeek = first.get(Calendar.DAY_OF_WEEK);
        }

        mArrDate = new ArrayList<Calendar>();
        int month = first.get(Calendar.MONTH);
        while(month == monthNow) {
            mArrDate.add(dateClone(first));
            first.roll(Calendar.DAY_OF_YEAR, 7);
            month = first.get(Calendar.MONTH);
        }

        Calendar selectedDate = getArgsSelectedDate();
        int checkedId = -1;
        int minHeight = getContext().getResources().getDimensionPixelSize(R.dimen.list_item_min_height);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mHolder.clearCheck();
        mHolder.removeAllViews();
        for(int i = 0; i < mArrDate.size(); i++) {
            int childId = BASE_ID + i;

            RadioButton button = new RadioButton(getContext());
            button.setId(childId);
            button.setLayoutParams(layoutParams);
            button.setText(getDateString(mArrDate.get(i)));
            button.setMinHeight(minHeight);
            if (dateCompare(selectedDate, mArrDate.get(i)) == 0) {
                checkedId = childId;
            }

            mHolder.addView(button);
        }
        mHolder.measure(mHolder.getLayoutParams().width, mHolder.getLayoutParams().height);
        if (checkedId > 0) {
            mHolder.check(checkedId);
        }
    }

    private void showPrevious() {
        int monthNow = mCurrent.get(Calendar.MONTH);

        if(monthNow == 0) {
            mCurrent.roll(Calendar.YEAR, false);
            mCurrent.roll(Calendar.MONTH, 11);
        } else {
            mCurrent.roll(Calendar.MONTH, false);
        }
        createList();
    }

    private void showNext() {
        int monthNow = mCurrent.get(Calendar.MONTH);

        if(monthNow == 11) {
            mCurrent.roll(Calendar.YEAR, true);
            mCurrent.roll(Calendar.MONTH, -11);
        } else {
            mCurrent.roll(Calendar.MONTH, true);
        }

        createList();
    }

    private void performAccept() {
        if (mHolder == null) return;
        int selectedIndex = mHolder.getCheckedRadioButtonId() - BASE_ID;
        if (selectedIndex < 0) return;
        Calendar selected = mArrDate.get(selectedIndex);
        if (mOnSetDateListener != null) {
            mOnSetDateListener.onSetDate(selected);
        }
    }

    private void performClear() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.action_clear);
        builder.setMessage(R.string.message_date_clear);
        builder.setNegativeButton(android.R.string.no, null);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHolder.clearCheck();
                if (mOnSetDateListener != null) {
                    mOnSetDateListener.onClearDate();
                }
            }
        });
        builder.create().show();
    }
}
