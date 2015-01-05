package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
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

            PlaceholderFragment fragment = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            EditText edit_name= (EditText)fragment.getView().findViewById(R.id.et_profile_edit_name);
            showDialog("save result", "name:" + edit_name.getText().toString());
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
        long id = -1;
        try {
            db.beginTransaction();
            ContentValues profileHd = new ContentValues();
            profileHd.put("status", 0);
            id = db.insert("profile_hd", null, profileHd);

            List<ProfileDetail> values = new ArrayList<ProfileDetail>();

            LinearLayout containerView = (LinearLayout)getView(R.id.container_profile_edit);
            for(int i=0; i<containerView.getChildCount(); i++) {
                View child = containerView.getChildAt(i);
                String key_id = (String)child.getTag(R.string.tag_key_id);
                Integer sequence = (Integer)child.getTag(R.string.tag_key_sequence);
                if (key_id == null || sequence == null) continue;
                //TODO keyごとに探すIDを変える
                EditText editText = (EditText) child.findViewById(R.id.txt_profile_edit_element);
                String value = editText.getText().toString();
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
            throw e;
//            return -1;
        }
        return id;
    }

    private View getView(int id) {
        PlaceholderFragment fragment = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container);
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

        protected void initializeView(LayoutInflater inflater, View rootView){
            LinearLayout containerView = (LinearLayout)rootView.findViewById(R.id.container_profile_edit);
            Cursor cursor = getProfileKeyMasterCursor();

            while(cursor.moveToNext()){

                View row = inflater.inflate(R.layout.partial_profile_edit_element_text, null);
                String key_id = cursor.getString(cursor.getColumnIndex("_id"));
                int sequence = 1;

                row.setTag(R.string.tag_key_id, key_id);
                row.setTag(R.string.tag_key_sequence, sequence);

                TextView label = (TextView) row.findViewById(R.id.lbl_profile_edit_element);
                label.setText(cursor.getString(cursor.getColumnIndex("name")));

                EditText editText = (EditText) row.findViewById(R.id.txt_profile_edit_element);
                editText.setTag(R.string.tag_key_id, key_id);
                editText.setTag(R.string.tag_key_sequence, sequence);

                containerView.addView(row);
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
