SELECT
  tt.task_seq
FROM
  t_task tt 
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
  AND tt.limit_dt_to < /* date */null
ORDER BY
  tt.limit_dt_to ASC,
  tt.task_seq DESC
;