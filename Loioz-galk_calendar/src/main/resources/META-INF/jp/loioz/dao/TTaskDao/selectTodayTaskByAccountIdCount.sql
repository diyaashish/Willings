SELECT
  COUNT(*)
FROM
  t_task tt 
  LEFT JOIN t_anken ta
    USING (anken_id)
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.today_task_date = /* date */null
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
;