SELECT
  COALESCE(MIN(today_task_disp_order), 0) AS disp_min
FROM
  t_task_worker 
WHERE
  worker_account_seq = /*accountSeq*/'1'
  AND task_worker_seq IN /* taskWorkerSeqList */(1);