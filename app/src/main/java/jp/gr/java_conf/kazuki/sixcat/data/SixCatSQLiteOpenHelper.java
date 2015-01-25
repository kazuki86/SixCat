package jp.gr.java_conf.kazuki.sixcat.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * SixCatSQLiteOpenHelper
 *
 * Created by kazuki on 2015/01/02.
 */
public class SixCatSQLiteOpenHelper  extends SQLiteOpenHelper {

    static final int VERSION = 23;

    public SixCatSQLiteOpenHelper(Context context) {
        super(context, "six_cat", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        initializeDatabase(db);
    }

    private void initializeDatabase(SQLiteDatabase db){
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
                + " value_type_id integer not null, " // 1:数値、2:単一行テキスト、3:複数行テキスト、4:英数字、5:選択、6:日付、7:画像
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
                + " sequence integer not null default 1, "
                + " value text )";

        String create_sql_view_profile_list
                =  " create view view_profile_list as select "
                + "   hd.*,"
                + "   dt_name.value as name,"
                + "   dt_kana.value as kana,"
                + "   dt_nickname.value as nickname,"
                + "   dt_birthday.value as birthday"
                + " from "
                + "   profile_hd hd"
                + "   left join profile_detail dt_name "
                + "     on  hd._id = dt_name.profile_id "
                + "     and dt_name.key_id = 1"
                + "   left join profile_detail dt_kana "
                + "     on  hd._id = dt_kana.profile_id "
                + "     and dt_kana.key_id = 2"
                + "   left join profile_detail dt_nickname "
                + "     on  hd._id = dt_nickname.profile_id "
                + "     and dt_nickname.key_id = 3"
                + "   left join profile_detail dt_birthday "
                + "     on  hd._id = dt_birthday.profile_id "
                + "     and dt_birthday.key_id = 4;";

        String create_sql_view_profile_detail
                = " create view view_profile_detail as select "
                + "   *"
                + " from"
                + "   profile_key_master ms"
                + "   left join profile_detail dt on ms._id = dt.key_id;";

        String create_sql_view_profile_edit
                = " create view view_profile_edit as select "
                + "   ms.*, "
                + "   hd._id as profile_hd_id, "
                + "   dt.* "
                + " from"
                + "   profile_key_master ms,"
                + "   profile_hd hd "
                + "   left join profile_detail dt "
                + "     on ms._id = dt.key_id "
                + "     and hd._id = dt.profile_id ;";

        Log.d("SQL", create_sql_view_profile_edit);
        String[] insert_sql_value_type_master_list = new String[]{
                "insert into value_type_master values( 1, '数値'); ",
                "insert into value_type_master values( 2, '単一行テキスト'); ",
                "insert into value_type_master values( 3, '複数行テキスト'); ",
                "insert into value_type_master values( 4, '英数字'); ",
                "insert into value_type_master values( 5, '選択'); ",
                "insert into value_type_master values( 6, '日付'); ",
                "insert into value_type_master values( 7, '画像'); ",
        };
        String[] insert_sql_profile_key_master_list = new String[]{
                "insert into profile_key_master values ( 1,'名前'           ,'',2,0,2,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master values ( 2,'よみがな'       ,'',3,0,2,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master values ( 3,'ニックネーム'   ,'',4,0,2,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master values ( 4,'誕生日'         ,'',5,0,6,'',0,1,null,null,null,null,null,null,null,null,null,null);",
                "insert into profile_key_master values ( 5,'写真'           ,'',1,0,7,'',0,1,null,null,null,null,null,null,null,null,null,null);",
        };
        String[] insert_sql_profile_list = new String[]{
                "insert into profile_hd values ( 1, 0);",
                "insert into profile_detail values ( 1, 1, 1, 1, '六猫　晶子');",
                "insert into profile_detail values ( 2, 1, 2, 1, 'ろくねこ　しょうこ');",
                "insert into profile_detail values ( 3, 1, 3, 1, 'しょうこ');",
                "insert into profile_detail values ( 4, 1, 4, 1, '1980/02/03');",

                "insert into profile_hd values ( 2, 0);",
                "insert into profile_detail values ( 5, 2, 1, 1, '六猫　多喜子');",
                "insert into profile_detail values ( 6, 2, 2, 1, 'ろくねこ　たきこ');",
                "insert into profile_detail values ( 7, 2, 3, 1, 'たっきー');",
                "insert into profile_detail values ( 8, 2, 4, 1, '1984/09/28');",

        };


        db.execSQL(create_sql_value_type_master);
        db.execSQL(create_sql_profile_key_master);
        db.execSQL(create_sql_profile_hd);
        db.execSQL(create_sql_profile_detail);
        db.execSQL(create_sql_view_profile_list);
        db.execSQL(create_sql_view_profile_detail);
        db.execSQL(create_sql_view_profile_edit);

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
        String drop_sql_view_profile_list   = "drop view view_profile_list;";
        String drop_sql_view_profile_detail   = "drop view view_profile_detail;";
        String drop_sql_view_profile_edit   = "drop view view_profile_edit;";

        try {
            db.execSQL(drop_sql_value_type_master);
            db.execSQL(drop_sql_profile_key_master);
            db.execSQL(drop_sql_profile_hd);
            db.execSQL(drop_sql_profile_detail);
            db.execSQL(drop_sql_view_profile_list);
            db.execSQL(drop_sql_view_profile_detail);
            db.execSQL(drop_sql_view_profile_edit);
        } catch(Exception e){

            Log.d("DB",e.toString());
        }


        initializeDatabase(db);

    }
}
