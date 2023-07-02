select
  * 
from
  t_plan_history tph 
where
  tph.plan_status = /* statusCd */'1' 
order by
  tph.plan_history_seq desc 
limit
  1;