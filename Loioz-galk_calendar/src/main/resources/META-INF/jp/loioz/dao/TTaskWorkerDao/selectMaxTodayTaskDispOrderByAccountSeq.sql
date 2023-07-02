SELECT
  COALESCE(MAX(today_task_disp_order), 0) AS disp_max
FROM
  t_task_worker 
WHERE
  worker_account_seq = /*accountSeq*/'1'
