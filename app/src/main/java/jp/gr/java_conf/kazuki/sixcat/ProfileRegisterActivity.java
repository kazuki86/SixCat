package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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


public class ProfileRegisterActivity extends ActionBarActivity {

    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_register);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
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
        getMenuInflater().inflate(R.menu.menu_profile_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile_register_save) {

//            PlaceholderFragment fragment = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container);
//            EditText edit_name= (EditText)fragment.getView().findViewById(R.id.et_profile_edit_name);
//            showDialog("save result", "name:" + edit_name.getText().toString());
            long profile_id = save();
            if ( profile_id != -1 ) {
                Intent detailIntent = new Intent(this, ProfileDetailActivity.class);
                detailIntent.putExtra(ProfileDetailFragment.ARG_ITEM_ID, Long.toString(profile_id));
                startActivity(detailIntent);
            } else {
                showDialog("Error", "保存に失敗しました。");
            }
            return true;
        }else if (id == R.id.action_profile_register_cancel) {
            //戻る
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private long save(){
        long id;
        try {
            db.beginTransaction();
            ContentValues profileHd = new ContentValues();
            profileHd.put("status", 0);
            id = db.insert("profile_hd", null, profileHd);

            List<ProfileDetail> values = new ArrayList<>();

            LinearLayout containerView = (LinearLayout)getView(R.id.container_profile_edit);
            for(int i=0; i<containerView.getChildCount(); i++) {
                View child = containerView.getChildAt(i);
                String key_id = (String)child.getTag(R.string.tag_key_id);
                Integer sequence = (Integer)child.getTag(R.string.tag_key_sequence);
                Integer value_type = (Integer)child.getTag(R.string.tag_key_type);
                if (key_id == null || sequence == null) continue;
                String value = getInputValue(child, value_type);
                if (value == null || value.isEmpty()) continue;
                values.add(new ProfileDetail(Long.valueOf(key_id), sequence, value));
            }

            for(ProfileDetail value : values) {
                ContentValues profileDetail = new ContentValues();
                profileDetail.put("profile_id", id);
                profileDetail.put("key_id", value.key_id);
                profileDetail.put("sequence", value.sequence);
                profileDetail.put("value", value.value);
                db.insert("profile_detail",null, profileDetail);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch(RuntimeException e) {
            return -1;
        }
        return id;
    }

    private String getInputValue(View child, Integer value_type) {
        String value = null;
        switch(value_type){
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
                value = (String)imageView.getTag(R.string.tag_image_file_path);
                break;
        }
        return value;
    }

    private View getView(int id) {
        PlaceholderFragment fragment = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container);
        if (fragment == null || fragment.getView() == null) {
            return null;
        }
        return fragment.getView().findViewById(id);
    }

    private void showDialog(String title, String text){

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(text);
        dialog.setPositiveButton("OK",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProfileRegisterActivity.this.setResult(Activity.RESULT_OK);
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

        protected void initializeView(LayoutInflater inflater, View rootView, Bundle savedInstanceState){
            LinearLayout containerView = (LinearLayout)rootView.findViewById(R.id.container_profile_edit);



            if (savedInstanceState != null && savedInstanceState.containsKey(KEY_EDIT_DATA)) {

                ArrayList<ProfileParcelable> contents = savedInstanceState.getParcelableArrayList(KEY_EDIT_DATA);
                for (ProfileParcelable profile : contents) {
                    View row = createRowView(inflater, profile);
                    containerView.addView(row);
                }
            } else {
                Cursor cursor = getProfileKeyMasterCursor();

                while (cursor.moveToNext()) {
                    String key_id = cursor.getString(cursor.getColumnIndex("_id"));
                    int value_type = cursor.getInt(cursor.getColumnIndex("value_type_id"));
                    String label_str = cursor.getString(cursor.getColumnIndex("name"));
                    int sequence = 1;
                    String value = ""; //default value ?
                    List<KeyValueItem> options = new ArrayList<>();
                    options.add(new KeyValueItem(R.integer.option_no_select_id, getString(R.string.option_default_string)));
                    for(int i=getResources().getInteger(R.integer.option_index_from); i<=getResources().getInteger(R.integer.option_index_to); i++) {
                        String column_name = String.format("option%02d",i);
                        String option = cursor.getString(cursor.getColumnIndex(column_name));
                        if (option != null) {
                            options.add(new KeyValueItem(i,option));
                        }
                    }

                    ProfileParcelable profile = new ProfileParcelable(key_id, sequence, value, value_type, label_str,options);
                    View row = createRowView(inflater, profile);
                    containerView.addView(row);
                }
            }
        }

        private Cursor getProfileKeyMasterCursor() {
            return db.query("profile_key_master",
                    null,
                    "use_flg = ?",
                    new String[]{"1"},
                    null,
                    null,
                    "sort_order"
            );
        }

    }

    class ProfileDetail{
        public long key_id;
        public int sequence;
        public String value;
        public ProfileDetail(long key_id, int sequence, String value){
            this.key_id = key_id;
            this.sequence = sequence;
            this.value = value;
        }
    }
}
