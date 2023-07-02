-- 無料ステータスの履歴は1件しか存在せず、複数件取得されることはない。（必ずt_plan_historyの1レコード目にのみ存在している）
select
  * 
from
  t_plan_history tph 
where
  tph.plan_status = /* @jp.loioz.common.constant.plan.PlanConstant$PlanStatus@FREE */'0';