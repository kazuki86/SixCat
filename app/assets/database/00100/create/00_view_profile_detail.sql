create view view_profile_detail as select 
  *
from
  profile_key_master ms
  left join profile_detail dt
    on ms._id = dt.key_id
;