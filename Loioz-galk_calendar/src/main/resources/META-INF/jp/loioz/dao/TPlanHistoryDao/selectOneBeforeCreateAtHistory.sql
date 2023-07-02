select
  * 
from
  t_plan_history tph 
where
  tph.history_created_at < /* targetDateTime */'2020-03-19 00:00:00'
order by
  tph.plan_history_seq desc
limit 1;