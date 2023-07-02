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
  AND ttw.creater_flg = '1'
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  -- タスク作成者以外で担当者がいるか判定（別ユーザにタスクを割り当てているか）
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_task_worker ttw2 
    WHERE
      ttw2.creater_flg = '0' 
      AND tt.task_seq = ttw2.task_seq
  )
;