package jp.gr.java_conf.kazuki.sixcat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kazuki on 2015/01/02.
 */
public class SixCatSQLiteOpenHelper  extends SQLiteOpenHelper {

    static final int VERSION = 1;

    public SixCatSQLiteOpenHelper(Context context) {
        super(context, "six_cat", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String create_sql_value_type_master
                = "create table value_type_master( "
                + " _id integer primary key autoincrement, "
                + " name text not null ); ";
        String create_sql_profile_key_master
                = "create table profile_key_master( "
                + " _id integer primary key autoincrement, "
                + " name text not null, "
                + " description text, "
                + " sort_order integer not null default 0, "
                + " plural_flg integer not null default 0, "
                + " value_type_id integer not null, " // 1:数値、2:単一行テキスト、3:複数行テキスト、3:英数字、4:選択、5:日付、
                + " initial_value text, "
                + " system_flg integer not null default 0, "
                + " use_flg integer not null default 1, "
                + " option0 text, "
                + " option1 text, "
                + " option2 text, "
                + " option3 text, "
                + " option4 text, "
                + " option5 text, "
                + " option6 text, "
                + " option7 text, "
                + " option8 text, "
                + " option9 text ); ";
        String create_sql_profile_hd
                = "create table profile_hd( "
                + " _id integer primary key autoincrement, "
                + " status integer not null default 0 )";
        String create_sql_profile_detail
                = "create table profile_detail( "
                + " _id integer primary key autoincrement, "
                + " profile_id integer not null, "
                + " key_id integer not null, "
                + " sequence integer not nll default 1, "
                + " value text )";

        String[] insert_sql_value_type_master_list = new String[]{
                "insert into value_type_master( 1, '数値'); ",
                "insert into value_type_master( 2, '単一行テキスト'); ",
                "insert into value_type_master( 3, '複数行テキスト'); ",
                "insert into value_type_master( 4, '英数字'); ",
                "insert into value_type_master( 5, '選択'); ",
                "insert into value_type_master( 6, '日付'); ",
        };
        String[] insert_sql_profile_key_master_list = new String[]{
                "insert into profile_key_master ( 1,'名前'           ,'',1,0,2,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master ( 2,'よみがな'       ,'',2,0,2,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master ( 3,'ニックネーム'   ,'',3,0,2,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master ( 4,'誕生日'         ,'',4,0,6,'',0,1,null,null,null,null,null,null,null,null,null,null);",
        };
        String[] insert_sql_profile_list = new String[]{
                "insert into profile_hd ( 1, 0);",
                "insert into profile_detail ( 1, 1, 1, 1, '六猫　晶子');",
                "insert into profile_detail ( 2, 1, 2, 1, 'ろくねこ　しょうこ');",
                "insert into profile_detail ( 3, 1, 3, 1, 'しょうこ');",
                "insert into profile_detail ( 4, 1, 4, 1, '1980/02/03');",
        };

        db.execSQL(create_sql_value_type_master);
        db.execSQL(create_sql_profile_key_master);
        db.execSQL(create_sql_profile_hd);
        db.execSQL(create_sql_profile_detail);

        for(String sql : insert_sql_value_type_master_list) {
            db.execSQL(sql);
        }
        for(String sql : insert_sql_profile_key_master_list) {
            db.execSQL(sql);
        }
        for(String sql : insert_sql_profile_list) {
            db.execSQL(sql);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String drop_sql_value_type_master   = "drop table value_type_master;";
        String drop_sql_profile_key_master  = "drop table profile_key_master;";
        String drop_sql_profile_hd          = "drop table profile_hd;";
        String drop_sql_profile_detail      = "drop table profile_detail;";

        db.execSQL(drop_sql_value_type_master);
        db.execSQL(drop_sql_profile_key_master);
        db.execSQL(drop_sql_profile_hd);
        db.execSQL(drop_sql_profile_detail);

    }
}
