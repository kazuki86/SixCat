package jp.gr.java_conf.kazuki.sixcat.data;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SixCatSQLiteOpenHelper
 *
 * Created by kazuki on 2015/01/02.
 */
public class SixCatSQLiteOpenHelper  extends SQLiteOpenHelper {

    static final int VERSION        = 100000009;
    static final int SUB_VERSION_MAX = 1000000;

    static String assetsFileEncoding = "UTF-8";
    static String database_name = "six_cat";
    private Context mContext;

    public SixCatSQLiteOpenHelper(Context context) {
        super(context,database_name, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            execute(db);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String oldAppVersion = getApplicationVersion(oldVersion);
        String newAppVersion = getApplicationVersion(newVersion);

        try {
            execute(db, oldAppVersion, newAppVersion);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getApplicationVersion(int dbVersion) {
        int version = dbVersion / SUB_VERSION_MAX;
        return String.format("%05d",version);
    }


    private void execute(SQLiteDatabase db) throws IOException {
        this.execute(db, "00000","99999");
    }

    private void execute(SQLiteDatabase db, String fromVersion, String toVersion) throws IOException {
        AssetManager as = mContext.getResources().getAssets();
        String baseDir = "database";

        String versions[] = as.list(baseDir);
        Arrays.sort(versions);
        for (String version : versions) {
            if( (version.compareTo(fromVersion) > 0 && version.compareTo(toVersion) <= 0)
                    || version.equals(fromVersion) && version.equals(toVersion) ) {
                String targetDirectory = baseDir + "/" + version;
                executeEachSql(db, targetDirectory);
            }
        }
    }

    private void executeEachSql(SQLiteDatabase db, String targetDirectory) throws IOException {
        AssetManager as = mContext.getResources().getAssets();

        String dropDirectory = targetDirectory + "/drop";
        String dropSqlFiles[] = as.list(dropDirectory);
        Arrays.sort(dropSqlFiles);
        for (String filename : dropSqlFiles) {
            String dropSql = readAll(as.open(dropDirectory + "/" + filename));
            Log.d("INITIALIZE_DATABASE", dropSql);
            db.execSQL(dropSql);
        }

        String createDirectory = targetDirectory + "/create";
        String createSqlFiles[] = as.list(createDirectory);
        Arrays.sort(createSqlFiles);
        for (String filename : createSqlFiles) {
            String createSql = readAll(as.open(createDirectory + "/" + filename));
            Log.d("INITIALIZE_DATABASE", createSql);
            db.execSQL(createSql);
        }

        String insertDirectory = targetDirectory + "/insert";
        String insertSqlFiles[] = as.list(insertDirectory);
        Arrays.sort(insertSqlFiles);
        for (String filename : insertSqlFiles) {
            List<String> insertSqlList = readLines(as.open(insertDirectory + "/" + filename));
            for(String insertSql : insertSqlList){
                if (insertSql.isEmpty()) continue;
                Log.d("INITIALIZE_DATABASE", insertSql);
                db.execSQL(insertSql);
            }
        }

    }

    private String readAll(InputStream is) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is,assetsFileEncoding));

            StringBuilder sb = new StringBuilder();
            String str;
            while((str = br.readLine()) != null){
                sb.append(str);
                sb.append("\n");
            }
            return sb.toString();
        } finally {
            if (br != null) br.close();
        }
    }

    private List<String> readLines(InputStream is) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is,assetsFileEncoding));

            List<String> list = new ArrayList<>();
            String str;
            while((str = br.readLine()) != null){
                list.add(str);
            }
            return list;
        } finally {
            if (br != null) br.close();
        }
    }


}
