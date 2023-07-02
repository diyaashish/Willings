SELECT
  COUNT(task_worker_seq)
FROM
  t_task_worker
WHERE
  worker_account_seq = /* accountSeq */NULL
  AND (new_task_kakunin_flg = 0
  OR new_history_kakunin_flg = 0);