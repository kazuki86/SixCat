package jp.gr.java_conf.kazuki.sixcat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * keyとvalueを持つだけ
 * Created by kazuki on 2015/01/29.
 */
public class KeyValueItem  implements Parcelable {
    public int key;
    public String value;
    public KeyValueItem(int key, String value) {
        this.key = key;
        this.value = value;
    }

    private KeyValueItem(Parcel in) {
        key = in.readInt();
        value = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(key);
        dest.writeString(value);
    }


    public static final Parcelable.Creator<KeyValueItem> CREATOR
            = new Parcelable.Creator<KeyValueItem>() {
        public KeyValueItem createFromParcel(Parcel in) {
            return new KeyValueItem(in);
        }

        public KeyValueItem[] newArray(int size) {
            return new KeyValueItem[size];
        }
    };

    @Override
    public String toString() {
        return value;
    }
}
