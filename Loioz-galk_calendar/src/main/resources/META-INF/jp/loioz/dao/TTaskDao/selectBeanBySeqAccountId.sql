SELECT
  tt.task_seq
  , tt.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tt.title
  , tt.content
  , tt.limit_dt_to
  , tt.all_day_flg
  , tt.task_status
  , tt.created_by
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
  , ttw.creater_flg
  , ttw.entrust_flg
  , ttw.new_task_kakunin_flg
  , ttw.new_history_kakunin_flg
  , ttw.disp_order
  -- タスクSEQ紐づくコメントの件数を取得
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
  LEFT JOIN t_anken ta
    USING (anken_id)
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */null
  AND tt.task_seq = /* taskSeq */null