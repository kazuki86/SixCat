package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
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

            //TODO ここらへんは、動的ビューにしたら変更する。
            List<ProfileDetail> values = new ArrayList<ProfileDetail>();
            values.add(new ProfileDetail(1,1,((EditText)getView(R.id.et_profile_edit_name)).getText().toString()));
            values.add(new ProfileDetail(2,1,((EditText)getView(R.id.et_profile_edit_kana)).getText().toString()));
            values.add(new ProfileDetail(3,1,((EditText)getView(R.id.et_profile_edit_nickname)).getText().toString()));
            values.add(new ProfileDetail(4,1,((EditText)getView(R.id.et_profile_edit_birthday)).getText().toString()));

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

        } catch(Exception e) {
            Log.e("ERROR", e.toString());
            return -1;
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

        protected void initializeView(View rootView){

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
