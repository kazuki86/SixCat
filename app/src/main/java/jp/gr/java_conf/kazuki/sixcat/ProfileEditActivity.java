package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.kazuki.sixcat.data.SixCatSQLiteOpenHelper;


public class ProfileEditActivity extends ActionBarActivity {

    private SQLiteDatabase db;

//    public static final String KEY_EDIT_DATA = "key_edit_data";

    public String profile_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        if (savedInstanceState == null) {

            profile_id = getIntent().getStringExtra(PlaceholderFragment.ARG_ITEM_ID);

            Bundle arguments = new Bundle();
            arguments.putString(PlaceholderFragment.ARG_ITEM_ID, profile_id);
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

//        PlaceholderFragment fragment = (PlaceholderFragment) getSupportFragmentManager().findFragmentById(R.id.container);
//        EditText edit_name= (EditText)fragment.getView().findViewById(R.id.et_profile_edit_name);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile_edit_save) {

            boolean result = save(profile_id);
            if (result) {
                Intent detailIntent = new Intent(this, ProfileDetailActivity.class);
                detailIntent.putExtra(ProfileDetailFragment.ARG_ITEM_ID, profile_id);
                startActivity(detailIntent);
            } else {
                showDialog("Error", "保存に失敗しました。");
            }
            return true;
        } else if (id == R.id.action_profile_edit_delete) {

            ConfirmDeletingDialog confirmDialog = new ConfirmDeletingDialog();
            confirmDialog.show(getSupportFragmentManager(),"dialog");

            return true;
        } else if (id == R.id.action_profile_edit_cancel) {
            //戻る
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class ConfirmDeletingDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final ProfileEditActivity activity = (ProfileEditActivity)getActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Confirmation");
            builder.setMessage("本当に削除しますか？");
            builder.setPositiveButton("はい", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    boolean result = activity.delete(activity.profile_id);
                    if (result) {
                        Intent listIntent = new Intent(activity, ProfileListActivity.class);
                        startActivity(listIntent);
                    } else {
                        activity.showDialog("Error", "削除に失敗しました。");
                    }
                }
            });
            builder.setNegativeButton("いいえ", null);
            return builder.create();
        }
    }



        @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PlaceholderFragment.ARG_ITEM_ID, profile_id);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle inState) {
        super.onRestoreInstanceState(inState);
        profile_id = inState.getString(PlaceholderFragment.ARG_ITEM_ID);
    }

    private boolean delete(String profile_id) {
        try {
            db.beginTransaction();

            db.delete("profile_hd", "_id = ?", new String[]{profile_id});
            db.delete("profile_detail", "profile_id = ?", new String[]{profile_id});

            db.setTransactionSuccessful();
            db.endTransaction();
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return true;

    }

    private boolean save(String profile_id) {
        try {
            db.beginTransaction();

            List<ProfileDetail> values = new ArrayList<>();
            LinearLayout containerView = (LinearLayout) getView(R.id.container_profile_edit);
            for (int i = 0; i < containerView.getChildCount(); i++) {
                View child = containerView.getChildAt(i);
                String key_id = (String) child.getTag(R.string.tag_key_id);
                Integer sequence = (Integer) child.getTag(R.string.tag_key_sequence);
                Integer value_type = (Integer) child.getTag(R.string.tag_key_type);
                if (key_id == null || sequence == null) continue;
                String value = getInputValue(child, value_type);
                if (value == null || value.isEmpty()) continue;
                values.add(new ProfileDetail(Long.valueOf(key_id), sequence, value));
            }

            db.delete("profile_detail", "profile_id = ?", new String[]{profile_id});
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
                Spinner spinnerEdit = (Spinner) child.findViewById(R.id.spn_profile_edit_element);
                KeyValueItem item = (KeyValueItem)spinnerEdit.getSelectedItem();
                if (item.key == R.integer.option_no_select_id) {
                    value = "";
                } else {
                    value = "" + item.key;
                }
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
        if (fragment == null) {
            return null;
        }
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
                        List<KeyValueItem> options = new ArrayList<>();
                        options.add(new KeyValueItem(R.integer.option_no_select_id, getString(R.string.option_default_string)));
                        for(int i=getResources().getInteger(R.integer.option_index_from); i<=getResources().getInteger(R.integer.option_index_to); i++) {
                            String column_name = String.format("option%02d",i);
                            String option = cursor.getString(cursor.getColumnIndex(column_name));
                            if (option != null) {
//                                options.set(i,option);
                                options.add(new KeyValueItem(i,option));
                            }
                        }

                        ProfileParcelable profile = new ProfileParcelable(key_id, sequence, value, value_type, label_str,options);
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

        private Cursor getProfileKeyMasterWithExistValueCursor(String profile_id) {
            return db.query("view_profile_edit",
                    null,
                    "profile_hd_id = ?  and use_flg = ?",
                    new String[]{profile_id, "1"},
                    null,
                    null,
                    "sort_order"
            );
        }
    }

    static class ProfileDetail {
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

