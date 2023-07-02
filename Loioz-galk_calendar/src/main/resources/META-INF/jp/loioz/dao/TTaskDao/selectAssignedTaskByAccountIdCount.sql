SELECT
  COUNT(*)
FROM
  t_task tt 
  LEFT JOIN t_anken ta
    USING (anken_id)
  LEFT JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.creater_flg = '0'
  AND ttw.entrust_flg = '0'
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
;