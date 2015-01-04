package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.gr.java_conf.kazuki.sixcat.data.SixCatSQLiteOpenHelper;


public class ProfileEditActivity extends ActionBarActivity {

    private SQLiteDatabase db;

    private long profile_id ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        if (savedInstanceState == null) {

            String str_id =  getIntent().getStringExtra(PlaceholderFragment.ARG_ITEM_ID);
            Log.d("DebugXX",str_id);
            profile_id =  Long.valueOf(str_id);

            Bundle arguments = new Bundle();
            arguments.putString(PlaceholderFragment.ARG_ITEM_ID,str_id);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile_edit_save) {

            PlaceholderFragment fragment = (PlaceholderFragment)getSupportFragmentManager().findFragmentById(R.id.container);
            EditText edit_name= (EditText)fragment.getView().findViewById(R.id.et_profile_edit_name);
            showDialog("update result", "name:" + edit_name.getText().toString());
            boolean result = save();
            if (result ) {
                Intent detailIntent = new Intent(this, ProfileDetailActivity.class);
                detailIntent.putExtra(ProfileDetailFragment.ARG_ITEM_ID, Long.toString(profile_id));
                startActivity(detailIntent);
            } else {
                showDialog("Error", "保存に失敗しました。");
            }
            return true;
        }else if (id == R.id.action_profile_edit_cancel) {
            //戻る
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean save(){
        try {
            db.beginTransaction();

            //TODO ここらへんは、動的ビューにしたら変更する。
            List<ProfileDetail> values = new ArrayList<ProfileDetail>();
            values.add(new ProfileDetail(1,1,((EditText)getView(R.id.et_profile_edit_name)).getText().toString()));
            values.add(new ProfileDetail(2,1,((EditText)getView(R.id.et_profile_edit_kana)).getText().toString()));
            values.add(new ProfileDetail(3,1,((EditText)getView(R.id.et_profile_edit_nickname)).getText().toString()));
            values.add(new ProfileDetail(4,1,((EditText)getView(R.id.et_profile_edit_birthday)).getText().toString()));


            db.delete("profile_detail","profile_id = ?",new String[]{Long.toString(profile_id)});
            for(ProfileDetail value : values) {
                ContentValues profileDetail = new ContentValues();
                profileDetail.put("profile_id", profile_id);
                profileDetail.put("key_id", value.key_id);
                profileDetail.put("sequence", value.sequence);
                profileDetail.put("value", value.value);
                db.insert("profile_detail",null, profileDetail);
            }

            db.setTransactionSuccessful();
            db.endTransaction();

        } catch(Exception e) {
            Log.e("ERROR", e.toString());
            return false;
        }
        return true;
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

        private SQLiteDatabase db;
        private Cursor cursor;


        private final int IDX_ID = 0;
        private final int IDX_STATUS = 1;
        private final int IDX_NAME = 2;
        private final int IDX_KANA = 3;
        private final int IDX_NICKNAME = 4;
        private final int IDX_BIRTHDAY = 5;

        public PlaceholderFragment() {
            super();
        }

        protected void initializeView(View rootView){
            String profile_id =  getActivity().getIntent().getStringExtra(ARG_ITEM_ID);

            SixCatSQLiteOpenHelper helper = new SixCatSQLiteOpenHelper(getActivity());

            db = helper.getReadableDatabase();
            //sample
            //TODO ここは本来は、view_profile_listから取得せずに直接profile_detailから取得するように変更する。
            cursor = db.query("view_profile_list",
                    new String[]{"_id","status","name","kana","nickname","birthday"},
                    "_id = ?",new String[]{profile_id}
                    ,null,null,null);
            cursor.moveToFirst();

            ((EditText) rootView.findViewById(R.id.et_profile_edit_name)).setText(cursor.getString(IDX_NAME));
            ((EditText) rootView.findViewById(R.id.et_profile_edit_kana)).setText(cursor.getString(IDX_KANA));
            ((EditText) rootView.findViewById(R.id.et_profile_edit_nickname)).setText(cursor.getString(IDX_NICKNAME));
            ((EditText) rootView.findViewById(R.id.et_profile_edit_birthday)).setText(cursor.getString(IDX_BIRTHDAY));
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
