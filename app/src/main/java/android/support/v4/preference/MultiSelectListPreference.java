package android.support.v4.preference;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.util.AttributeSet;

import com.s16.dhammadroid.R;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by SMM on 11/3/2016.
 */

public class MultiSelectListPreference extends DialogPreference {

    private static int[] ATTRS = new int[] {
            android.R.attr.entries,
            android.R.attr.entryValues
    };

    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private Set<String> mValues = new HashSet<String>();
    private Set<String> mNewValues = new HashSet<String>();
    private boolean mPreferenceChanged;

    public MultiSelectListPreference(Context context) {
        super(context);
        initialize(context, null, R.attr.dialogPreferenceStyle, 0);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, R.attr.dialogPreferenceStyle, 0);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr, 0);
    }

    public MultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr, defStyleRes);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        mEntries = a.getTextArray(0);
        mEntryValues = a.getTextArray(1);
        a.recycle();
    }

    /**
     * Sets the human-readable entries to be shown in the list. This will be
     * shown in subsequent dialogs.
     * <p>
     * Each entry must have a corresponding index in
     * {@link #setEntryValues(CharSequence[])}.
     *
     * @param entries The entries.
     * @see #setEntryValues(CharSequence[])
     */
    public void setEntries(CharSequence[] entries) {
        mEntries = entries;
    }

    /**
     * @see #setEntries(CharSequence[])
     * @param entriesResId The entries array as a resource.
     */
    public void setEntries(int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }

    /**
     * The list of entries to be shown in the list in subsequent dialogs.
     *
     * @return The list as an array.
     */
    public CharSequence[] getEntries() {
        return mEntries;
    }

    /**
     * The array to find the value to save for a preference when an entry from
     * entries is selected. If a user clicks on the second item in entries, the
     * second item in this array will be saved to the preference.
     *
     * @param entryValues The array to be used as values to save for the preference.
     */
    public void setEntryValues(CharSequence[] entryValues) {
        mEntryValues = entryValues;
    }

    /**
     * @see #setEntryValues(CharSequence[])
     * @param entryValuesResId The entry values array as a resource.
     */
    public void setEntryValues(int entryValuesResId) {
        setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
    }

    /**
     * Returns the array of values to be saved for the preference.
     *
     * @return The array of values.
     */
    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    /**
     * Sets the value of the key. This should contain entries in
     * {@link #getEntryValues()}.
     *
     * @param values The values to set for the key.
     */
    public void setValues(Set<String> values) {
        mValues.clear();
        mValues.addAll(values);

        internalPersistStringSet(values);
    }

    /**
     * Retrieves the current value of the key.
     */
    public Set<String> getValues() {
        return mValues;
    }

    /**
     * Returns the index of the given value (in the entry values array).
     *
     * @param value The value whose index should be returned.
     * @return The index of the value, or -1 if not found.
     */
    public int findIndexOfValue(String value) {
        if (value != null && mEntryValues != null) {
            for (int i = mEntryValues.length - 1; i >= 0; i--) {
                if (mEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "MultiSelectListPreference requires an entries array and " +
                            "an entryValues array.");
        }

        boolean[] checkedItems = getSelectedItems();
        builder.setMultiChoiceItems(mEntries, checkedItems,
                new DialogInterface.OnMultiChoiceClickListener() {
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            mPreferenceChanged |= mNewValues.add(mEntryValues[which].toString());
                        } else {
                            mPreferenceChanged |= mNewValues.remove(mEntryValues[which].toString());
                        }
                    }
                });
        mNewValues.clear();
        mNewValues.addAll(mValues);
    }

    private boolean[] getSelectedItems() {
        final CharSequence[] entries = mEntryValues;
        final int entryCount = entries.length;
        final Set<String> values = mValues;
        boolean[] result = new boolean[entryCount];

        for (int i = 0; i < entryCount; i++) {
            result[i] = values.contains(entries[i].toString());
        }

        return result;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult && mPreferenceChanged) {
            final Set<String> values = mNewValues;
            if (callChangeListener(values)) {
                setValues(values);
            }
        }
        mPreferenceChanged = false;
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        final CharSequence[] defaultValues = a.getTextArray(index);
        final int valueCount = defaultValues.length;
        final Set<String> result = new HashSet<String>();

        for (int i = 0; i < valueCount; i++) {
            result.add(defaultValues[i].toString());
        }

        return result;
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValues(restoreValue ? getInternalPersistedStringSet(mValues) : (Set<String>) defaultValue);
    }

    private boolean internalPersistStringSet(Set<String> values) {
        if (shouldPersist()) {
            // Shouldn't store null
            if (values.equals(getInternalPersistedStringSet(null))) {
                // It's already there, so the same as persisting
                return true;
            }

            SharedPreferences.Editor editor = getPreferenceManager().getSharedPreferences().edit();
            editor.putStringSet(getKey(), values);
            editor.commit();
            return true;
        }
        return false;
    }

    private Set<String> getInternalPersistedStringSet(Set<String> defaultReturnValue) {
        if (!shouldPersist()) {
            return defaultReturnValue;
        }

        return getPreferenceManager().getSharedPreferences().getStringSet(getKey(), defaultReturnValue);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state
            return superState;
        }

        final SavedState myState = new SavedState(superState);
        myState.values = getValues();
        return myState;
    }

    private static class SavedState extends BaseSavedState {
        Set<String> values;

        public SavedState(Parcel source) {
            super(source);
            values = new HashSet<String>();
            String[] strings = source.createStringArray();

            final int stringCount = strings.length;
            for (int i = 0; i < stringCount; i++) {
                values.add(strings[i]);
            }
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeStringArray(values.toArray(new String[0]));
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }

}
