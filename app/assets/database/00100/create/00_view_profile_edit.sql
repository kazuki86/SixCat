create view view_profile_edit as select 
  ms.*, 
  hd._id as profile_hd_id, 
  dt.* 
from
  profile_key_master ms,
  profile_hd hd 
  left join profile_detail dt 
    on ms._id = dt.key_id 
    and hd._id = dt.profile_id 
;