package edu.cmu.chimps.messageontap_api;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;


public class ExtensionData implements Parcelable {
    /**
     * We'll see what will be included in this class. Currently, it only contains a string
     * which contains serialized data for criteria to trigger this extension.
     */

    private static final String KEY_TRIGGER = "trigger";

    private String mTrigger = null;

    public ExtensionData() {
    }

    /**
     * Returns the trigger criteria string
     * Default null.
     */
    public String trigger() {
        return mTrigger;
    }

    /**
     * Sets the trigger criteria string. Default null.
     */
    public ExtensionData trigger(String trigger) {
        mTrigger = trigger;
        return this;
    }

    /**
     * Serializes the contents of this object to JSON.
     */
    public JSONObject serialize() throws JSONException {
        JSONObject data = new JSONObject();
        data.put(KEY_TRIGGER, mTrigger);
        return data;
    }

    /**
     * Deserializes the given JSON representation of extension data, populating this
     * object.
     */
    public void deserialize(JSONObject data) throws JSONException {
        this.mTrigger = data.optString(KEY_TRIGGER);
    }

    /**
     * Serializes the contents of this object to a {@link Bundle}.
     */
    public Bundle toBundle() {
        Bundle data = new Bundle();
        data.putString(KEY_TRIGGER, mTrigger);
        return data;
    }

    /**
     * Deserializes the given {@link Bundle} representation of extension data, populating this
     * object.
     */
    public void fromBundle(Bundle src) {
        this.mTrigger = src.getString(KEY_TRIGGER);
    }

    /**
     * @see Parcelable
     */
    public static final Creator<ExtensionData> CREATOR
            = new Creator<ExtensionData>() {
        public ExtensionData createFromParcel(Parcel in) {
            return new ExtensionData(in);
        }

        public ExtensionData[] newArray(int size) {
            return new ExtensionData[size];
        }
    };

    private ExtensionData(Parcel in) {
        int parcelableSize = in.readInt();
        int startPosition = in.dataPosition();
        this.mTrigger = in.readString();

        if (TextUtils.isEmpty(this.mTrigger)) {
            this.mTrigger = null;
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        // Inject a placeholder that will store the parcel size from this point on
        // (not including the size itself).
        int sizePosition = parcel.dataPosition();
        parcel.writeInt(0);
        int startPosition = parcel.dataPosition();
        parcel.writeString(TextUtils.isEmpty(mTrigger) ? "" : mTrigger);
        // Go back and write the size
        int parcelableSize = parcel.dataPosition() - startPosition;
        parcel.setDataPosition(sizePosition);
        parcel.writeInt(parcelableSize);
        parcel.setDataPosition(startPosition + parcelableSize);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        try {
            ExtensionData other = (ExtensionData) o;
            return TextUtils.equals(other.mTrigger, mTrigger);

        } catch (ClassCastException e) {
            return false;
        }
    }

    private static boolean objectEquals(Object x, Object y) {
        if (x == null || y == null) {
            return x == y;
        } else {
            return x.equals(y);
        }
    }

    /**
     * Returns true if the two provided data objects are equal (or both null).
     */
    public static boolean equals(ExtensionData x, ExtensionData y) {
        if (x == null || y == null) {
            return x == y;
        } else {
            return x.equals(y);
        }
    }

    @Override
    public String toString() {
        try {
            return serialize().toString();
        } catch (JSONException e) {
            return super.toString();
        }
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    public void clean() {
        //TODO: length limit
    }
}
