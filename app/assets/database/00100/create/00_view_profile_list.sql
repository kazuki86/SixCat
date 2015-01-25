create view view_profile_list as select 
  hd.*,
  dt_name.value as name,
  dt_kana.value as kana,
  dt_nickname.value as nickname,
  dt_birthday.value as birthday
from 
  profile_hd hd
  left join profile_detail dt_name 
    on  hd._id = dt_name.profile_id 
    and dt_name.key_id = 1
  left join profile_detail dt_kana 
    on  hd._id = dt_kana.profile_id 
    and dt_kana.key_id = 2
  left join profile_detail dt_nickname 
    on  hd._id = dt_nickname.profile_id 
    and dt_nickname.key_id = 3
  left join profile_detail dt_birthday 
    on  hd._id = dt_birthday.profile_id 
    and dt_birthday.key_id = 4
;