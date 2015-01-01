package jp.gr.java_conf.kazuki.sixcat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
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


public class ProfileRegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_register);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
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

            return true;
        }else if (id == R.id.action_profile_register_cancel) {
            //戻る
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
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
    public static class PlaceholderFragment extends Fragment {

        final int REQUEST_ACTION_PICK = 1;
        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_profile_edit, container, false);


            //img_profile_edit_portrait
            final ImageView portrait = (ImageView)rootView.findViewById(R.id.img_profile_edit_portrait);
            if (portrait != null) {
                portrait.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        //実行フロー
//                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        //これだとギャラリー専門が開きます。
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        //createChooserを使うと選択ダイアログのタイトルを変更する事ができます。
                        startActivityForResult(Intent.createChooser(intent, "select"), REQUEST_ACTION_PICK);
                        //デフォルトで「アプリ選択」と出ます。
                        //startActivityForResult(intent, REQUEST_ACTION_PICK);
                    }
                });
            }

            return rootView;
        }

        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if(resultCode == RESULT_OK){
                if(requestCode == REQUEST_ACTION_PICK){
                    try {
                        InputStream iStream = getActivity().getApplicationContext().getContentResolver().openInputStream(data.getData());
                        Bitmap bm = BitmapFactory.decodeStream(iStream);
                        iStream.close();
                        //Bitmapで普通に利用ができます。
                        ((ImageView) getActivity().findViewById(R.id.img_profile_edit_portrait)).setImageBitmap(bm);
                    }catch (IOException e) {}
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
