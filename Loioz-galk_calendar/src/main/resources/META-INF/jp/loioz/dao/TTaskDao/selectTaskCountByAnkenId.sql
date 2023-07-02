SELECT
  tt.anken_id
  , COUNT(*) number_of_task
FROM
  t_task tt 
WHERE
  tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  AND tt.anken_id IN /* ankenIdList */(1)
GROUP BY
  tt.anken_id
;