SELECT
  tt.task_seq
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.today_task_date = /* date */null
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
ORDER BY
  ttw.today_task_disp_order DESC
;