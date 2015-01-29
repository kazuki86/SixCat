package jp.gr.java_conf.kazuki.sixcat;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class MainActivity extends FragmentActivity {

    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //スプラッシュ画像を2000ミリ秒表示する。
        handler = new Handler();
        handler.postDelayed(new splashHandler(), 2000);
    }

    class splashHandler implements Runnable{
        public void run(){
            //インテントを生成し、遷移先のアクティビティクラスを指定する。
            Intent intent = new Intent(MainActivity.this,ProfileListActivity.class);
            //次のアクティビティの起動
            startActivity(intent);
            //スプラッシュの終了。
            MainActivity.this.finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
