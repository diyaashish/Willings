select
  * 
from
  t_plan_history tph 
where
  tph.plan_status IN /* statusCdList */('1', '2') 
  and tph.history_created_at between /* betweenStart */'2020-01-01 00:00:00' and /* betweenEnd */'2020-01-31 23:59:59'
order by
  tph.plan_history_seq;