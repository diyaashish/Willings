SELECT
  COUNT(*)
FROM
  t_task tt 
  LEFT JOIN t_anken ta
    USING (anken_id)
  INNER JOIN ( 
    SELECT
      * 
    FROM
      t_task_worker ttw 
    WHERE
      ttw.worker_account_seq = /* accountId */'1'
  ) ttw ON tt.task_seq = ttw.task_seq
WHERE
  tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  AND tt.limit_dt_to > /* yesterday */null
;