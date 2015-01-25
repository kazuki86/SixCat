create table profile_detail( 
  _id integer primary key autoincrement, 
  profile_id integer not null, 
  key_id integer not null, 
  sequence integer not null default 1, 
  value text 
);