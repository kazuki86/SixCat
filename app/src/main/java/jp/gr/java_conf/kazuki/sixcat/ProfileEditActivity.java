package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import jp.gr.java_conf.kazuki.sixcat.data.SixCatSQLiteOpenHelper;


public class ProfileEditActivity extends ActionBarActivity {

    private SQLiteDatabase db;

    public static final String KEY_EDIT_DATA = "key_edit_data";

    private long profile_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        if (savedInstanceState == null) {

            String str_id = getIntent().getStringExtra(PlaceholderFragment.ARG_ITEM_ID);
            profile_id = Long.valueOf(str_id);

            Bundle arguments = new Bundle();
            arguments.putString(PlaceholderFragment.ARG_ITEM_ID, str_id);
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }

        if (db == null) {
            SixCatSQLiteOpenHelper helper = new SixCatSQLiteOpenHelper(this);
            db = helper.getWritableDatabase();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile_edit, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.container);
//        EditText edit_name= (EditText)fragment.getView().findViewById(R.id.et_profile_edit_name);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile_edit_save) {

            boolean result = save();
            if (result) {
                Intent detailIntent = new Intent(this, ProfileDetailActivity.class);
                detailIntent.putExtra(ProfileDetailFragment.ARG_ITEM_ID, Long.toString(profile_id));
                startActivity(detailIntent);
            } else {
                showDialog("Error", "保存に失敗しました。");
            }
            return true;
        } else if (id == R.id.action_profile_edit_delete) {

            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Confirm");
            dialog.setMessage("本当に削除しますか？");
            dialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    ProfileEditActivity.this.setResult(Activity.RESULT_OK);
                    boolean result = delete();
                    if (result) {
                        Intent listIntent = new Intent(ProfileEditActivity.this, ProfileListActivity.class);
                        startActivity(listIntent);
                    } else {
                        showDialog("Error", "削除に失敗しました。");
                    }
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
//                    ProfileEditActivity.this.setResult(Activity.RESULT_OK);
                }
            });
            dialog.create();
            dialog.show();
            return true;
        } else if (id == R.id.action_profile_edit_cancel) {
            //戻る
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//
//        PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.container);
//        if (fragment == null) return;
//
//        ArrayList<String> state = fragment.getInstanceState();
//
//        outState.putStringArrayList(KEY_EDIT_DATA, state);
//    }
//
//    @Override
//    protected void onRestoreInstanceState(Bundle inState) {
//        super.onRestoreInstanceState(inState);
//        PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.container);
//        if (fragment == null) return;
//        ArrayList<String> state = inState.getStringArrayList(KEY_EDIT_DATA);
//        fragment.loadInstanceState(state);
//    }

    private boolean delete() {
        try {
            db.beginTransaction();

            db.delete("profile_hd", "_id = ?", new String[]{Long.toString(profile_id)});
            db.delete("profile_detail", "profile_id = ?", new String[]{Long.toString(profile_id)});

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return true;

    }

    private boolean save() {
        try {
            db.beginTransaction();

            List<ProfileDetail> values = new ArrayList<ProfileDetail>();
            LinearLayout containerView = (LinearLayout) getView(R.id.container_profile_edit);
            for (int i = 0; i < containerView.getChildCount(); i++) {
                View child = containerView.getChildAt(i);
                String key_id = (String) child.getTag(R.string.tag_key_id);
                Integer sequence = (Integer) child.getTag(R.string.tag_key_sequence);
                Integer value_type = (Integer) child.getTag(R.string.tag_key_type);
                if (key_id == null || sequence == null) continue;
                String value = getInputValue(child, value_type);
                if (value == null) continue;
                values.add(new ProfileDetail(Long.valueOf(key_id), sequence, value));
            }

            db.delete("profile_detail", "profile_id = ?", new String[]{Long.toString(profile_id)});
            for (ProfileDetail value : values) {
                ContentValues profileDetail = new ContentValues();
                profileDetail.put("profile_id", profile_id);
                profileDetail.put("key_id", value.key_id);
                profileDetail.put("sequence", value.sequence);
                profileDetail.put("value", value.value);
                db.insert("profile_detail", null, profileDetail);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return true;
    }

    private String getInputValue(View child, Integer value_type) {
        String value = null;
        switch (value_type) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 6:
                EditText editText = (EditText) child.findViewById(R.id.txt_profile_edit_element);
                value = editText.getText().toString();
                break;
            case 5:
                value = null;
                break;
            case 7:
                ImageView imageView = (ImageView) child.findViewById(R.id.img_profile_edit_element);
                value = (String) imageView.getTag(R.string.tag_image_file_path);
                break;
        }
        return value;
    }

    private View getView(int id) {
        PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        return fragment.getView().findViewById(id);
    }

    private void showDialog(String title, String text) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(text);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileEditActivity.this.setResult(Activity.RESULT_OK);
            }
        });
        dialog.create();
        dialog.show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends AbstractProfileEditFragment {

        public PlaceholderFragment() {
            super();
        }

        protected void initializeView(LayoutInflater inflater, View rootView, Bundle savedInstanceState) {
            String profile_id = getActivity().getIntent().getStringExtra(ARG_ITEM_ID);

            LinearLayout containerView = (LinearLayout) rootView.findViewById(R.id.container_profile_edit);

            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_EDIT_DATA)) {

                ArrayList<ProfileParcelable> contents = savedInstanceState.getParcelableArrayList(KEY_EDIT_DATA);
                for (ProfileParcelable profile : contents) {
                    View row = createRowView(inflater, profile);
                    containerView.addView(row);
                }
            } else {

                Cursor cursor = null;
                try {
                    cursor = getProfileKeyMasterWithExistValueCursor(profile_id);

                    while (cursor.moveToNext()) {

                        String key_id = cursor.getString(cursor.getColumnIndex("_id"));
                        String value = cursor.getString(cursor.getColumnIndex("value"));
                        String sequence_str = cursor.getString(cursor.getColumnIndex("sequence"));
                        int value_type = cursor.getInt(cursor.getColumnIndex("value_type_id"));
                        int sequence = (sequence_str == null) ? 1 : Integer.valueOf(sequence_str);
                        String label_str = cursor.getString(cursor.getColumnIndex("name")) + ((sequence > 1 ? sequence : ""));

                        ProfileParcelable profile = new ProfileParcelable(key_id, sequence, value, value_type, label_str);
                        View row = createRowView(inflater, profile);
                        containerView.addView(row);
                    }
                } catch (SQLiteException e) {
                    Log.e("", e.toString());
                } finally {
                    try {
                        if (cursor != null) {
                            cursor.close();
                        }
                    } catch (SQLiteException e) {
                        Log.e("", e.toString());
                    }
                }
            }

        }

        private View createRowView(LayoutInflater inflater, ProfileParcelable profile) {

//            Log.d("construct data", "       " + key_id + "," + value);
            View row = null;
            int content_view_id = R.id.txt_profile_edit_element;
            // 1:数値、2:単一行テキスト、3:複数行テキスト、4:英数字、5:選択、6:日付、7:画像
            switch (profile.value_type) {
                case 1:
                case 2:
                case 3:
                case 4:
                    //
                    row = getTextView(inflater, profile.value);
                    break;
                case 5:
                    //
                    break;
                case 6:
                    //
                    row = getDateView(inflater, profile.value);
                    break;
                case 7:
                    row = getImageView(inflater, profile.value);
                    content_view_id = R.id.img_profile_edit_element;
                    break;
            }
            row.setTag(R.string.tag_key_id, profile.key_id);
            row.setTag(R.string.tag_key_sequence, profile.sequence);
            row.setTag(R.string.tag_key_type, profile.value_type);

            TextView label = (TextView) row.findViewById(R.id.lbl_profile_edit_element);
            label.setText(profile.label_str);

            View contentView = row.findViewById(content_view_id);

            contentView.setTag(R.string.tag_key_id, profile.key_id);
            contentView.setTag(R.string.tag_key_sequence, profile.sequence);
            contentView.setTag(R.string.tag_key_type, profile.value_type);

            return row;
        }
        private View getTextView(LayoutInflater inflater, String value) {
            View row;
            row = inflater.inflate(R.layout.partial_profile_edit_element_text, null);

            EditText editView = (EditText) row.findViewById(R.id.txt_profile_edit_element);
            if (value != null) {
                editView.setText(value);
            }
            editView.setSaveEnabled(false);

            return row;
        }

        private View getImageView(LayoutInflater inflater, String value) {
            View row;
            row = inflater.inflate(R.layout.partial_profile_edit_element_image, null);
            ImageView imageView = (ImageView) row.findViewById(R.id.img_profile_edit_element);
            imageView.setOnClickListener(new ImageClickListener(R.id.img_profile_edit_element));

            if (value != null) {
                imageView.setTag(R.string.tag_image_file_path, value);
                try {
                    File srcFile = new File(value);
                    FileInputStream fis = new FileInputStream(srcFile);
                    Bitmap bm = BitmapFactory.decodeStream(fis);
                    imageView.setImageBitmap(bm);
                } catch (FileNotFoundException e) {
                    Log.d("IMAGE ERROR", e.toString());
                    e.printStackTrace();
                }
            }
            return row;
        }

        private View getDateView(LayoutInflater inflater, String value) {
            View row;
            row = inflater.inflate(R.layout.partial_profile_edit_element_date, null);
            final EditText editText = (EditText) row.findViewById(R.id.txt_profile_edit_element);
            if (value != null) {
                editText.setText(value);
            }
            editText.setSaveEnabled(false);
            editText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatePickerDialog.OnDateSetListener DateSetListener = new DatePickerDialog.OnDateSetListener() {
                        public void onDateSet(android.widget.DatePicker datePicker, int year,
                                              int monthOfYear, int dayOfMonth) {
                            editText.setText("" + year + "/" + (monthOfYear + 1) + "/" + dayOfMonth);
                        }
                    };

                    // 日付情報の初期設定
                    Calendar calendar = Calendar.getInstance();
                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                        dateFormat.setLenient(false);
                        java.util.Date date = dateFormat.parse(editText.getText().toString());
                        calendar.setTime(date);
                    } catch (ParseException e) {
                    }

                    DatePickerDialog dialog = new DatePickerDialog(
                            getActivity(),
                            android.R.style.Theme_Light,
                            DateSetListener,
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                    );
                    dialog.show();
                }
            });
            return row;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);


            LinearLayout containerView = (LinearLayout) getActivity().findViewById(R.id.container_profile_edit);

            ArrayList<ProfileParcelable> contents = new ArrayList<ProfileParcelable>();

            int childCount = containerView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View row = containerView.getChildAt(i);

                if (row.getTag(R.string.tag_key_id) == null) {
                    continue;
                }

                String key_id = (String) row.getTag(R.string.tag_key_id);
                int sequence = (Integer) row.getTag(R.string.tag_key_sequence);
                int value_type = (Integer) row.getTag(R.string.tag_key_type);

                TextView label = (TextView) row.findViewById(R.id.lbl_profile_edit_element);
                String label_str = label.getText().toString();

                String value = getValue(row, value_type);

                ProfileParcelable content = new ProfileParcelable(key_id, sequence, value, value_type, label_str);
                contents.add(content);

                Log.d("save", content.toString());
            }
            outState.putParcelableArrayList(KEY_EDIT_DATA, contents );

        }

        private String getValue(View container, int value_type) {
            String value = null;
            switch (value_type) {
                case 1:
                case 2:
                case 3:
                case 4:
                    //
                    EditText textEdit = (EditText) container.findViewById(R.id.txt_profile_edit_element);
                    value = textEdit.getText().toString();
                    break;
                case 5:
                    //
                    break;
                case 6:
                    //
                    EditText dateEdit = (EditText) container.findViewById(R.id.txt_profile_edit_element);
                    value = dateEdit.getText().toString();
                    break;
                case 7:

                    ImageView imageView = (ImageView) container.findViewById(R.id.img_profile_edit_element);
                    value = (String) imageView.getTag(R.string.tag_image_file_path);
                    break;
            }
            return value;
        }

        private Cursor getProfileKeyMasterWithExistValueCursor(String profile_id) {
            return db.query("view_profile_detail",
                    null,
                    //本来profile_idはview定義中のleft join のon句に指定するべきだが仕方なく。
                    "(profile_id = ? or profile_id is NULL) and use_flg = ?",
                    new String[]{profile_id, "1"},
                    null,
                    null,
                    "sort_order desc"
            );
        }
    }

    class ProfileDetail {
        public long key_id;
        public int sequence;
        public String value;

        public ProfileDetail(long key_id, int sequence, String value) {
            this.key_id = key_id;
            this.sequence = sequence;
            this.value = value;
        }
    }


}


class ProfileParcelable implements Parcelable {

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