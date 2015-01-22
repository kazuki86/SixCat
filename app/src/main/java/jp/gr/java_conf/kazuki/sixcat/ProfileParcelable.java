package jp.gr.java_conf.kazuki.sixcat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kazuki on 2015/01/22.
 */
public class ProfileParcelable implements Parcelable {

    public String key_id;
    public int sequence;
    public String value;
    public int value_type;
    public String label_str;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key_id);
        dest.writeInt(sequence);
        dest.writeString(value);
        dest.writeInt(value_type);
        dest.writeString(label_str);
    }

    public static final Parcelable.Creator<ProfileParcelable> CREATOR
            = new Parcelable.Creator<ProfileParcelable>() {
        public ProfileParcelable createFromParcel(Parcel in) {
            return new ProfileParcelable(in);
        }

        public ProfileParcelable[] newArray(int size) {
            return new ProfileParcelable[size];
        }
    };
    private ProfileParcelable(Parcel in) {
        key_id = in.readString();
        sequence = in.readInt();
        value = in.readString();
        value_type =in.readInt();
        label_str = in.readString();
    }

    public ProfileParcelable(
            String key_id,
            int sequence,
            String value,
            int value_type,
            String label_str
    ){
        this.key_id = key_id;
        this.sequence = sequence;
        this.value = value;
        this.value_type = value_type;
        this. label_str = label_str;
    }

    @Override
    public String toString() {
        return "ProfileParcelable[" + key_id +  "," + sequence +  "," + value +  "," + value_type +  "," + label_str +  "]";
    }
}