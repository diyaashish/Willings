SELECT
  * 
FROM
  t_task_worker 
WHERE
  task_seq = /* taskSeq */null
  AND worker_account_seq IN /* workerSeqList*/(null)
