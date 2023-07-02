SELECT
  tt.task_seq
  -- タスクの担当者をカンマ区切りの文字列で取得
  , ( 
    SELECT
      group_concat(worker_account_seq SEPARATOR ',') 
    FROM
      t_task_worker 
    WHERE
      task_seq = tt.task_seq
      AND entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
  ) AS worker_account_seq
  -- タスクのコメント数を取得
  , ( 
      SELECT
          COUNT(*) 
      FROM
          t_task_history tth 
      WHERE
        tth.task_seq = tt.task_seq 
        AND tth.task_history_type = /* @jp.loioz.common.constant.CommonConstant$TaskHistoryType@COMMENT */'1'
  ) AS comment_count 
  -- タスクSEQ紐づくチェックアイテムの件数を取得
  , ( 
      SELECT
          COUNT(*) 
      FROM
          t_task_check_item ttc 
      WHERE
        ttc.task_seq = tt.task_seq 
  ) AS check_item_count 
  -- タスクSEQ紐づく終了したチェックアイテムの件数を取得
  , ( 
      SELECT
          COUNT(*) 
      FROM
          t_task_check_item ttc 
      WHERE
        ttc.task_seq = tt.task_seq 
        AND ttc.complete_flg = /* @jp.loioz.common.constant.CommonConstant$CheckItemStatus@COMPLETED */'1'
  ) AS complete_check_item_count 
FROM
  t_task tt 
WHERE
  tt.task_seq IN /*taskSeqList*/(1) 
;