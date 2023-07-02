-- 無料ステータス以外の履歴を全て取得する
select
  * 
from
  t_plan_history tph 
where
  tph.plan_status != /* @jp.loioz.common.constant.plan.PlanConstant$PlanStatus@FREE */'0';