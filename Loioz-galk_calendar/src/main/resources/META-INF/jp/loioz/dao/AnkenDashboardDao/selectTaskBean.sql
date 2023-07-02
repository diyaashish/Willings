SELECT
    tt.task_seq
    , tt.anken_id
    , tt.title
    , tt.content
    , tt.all_day_flg
    , tt.limit_dt_to
    , tt.task_status
    , ttw.worker_account_seq AS account_seq
    , ttw.entrust_flg
  -- タスクSEQ紐づくコメント件数を取得
    , ( 
        SELECT
            COUNT(*) 
        FROM
            t_task_history 
        WHERE
            task_seq = ttw.task_seq 
            AND task_history_type = 1
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
    LEFT JOIN t_task_worker ttw 
        USING (task_seq) 
WHERE
    tt.anken_id = /* ankenId */null
    /*%if !isAllDispTaskList */
    AND (
        tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@PREPARING */'1'
        OR tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@WORKING */'2'
    )
    /*%end */
ORDER BY
    CASE 
        WHEN tt.limit_dt_to IS NOT NULL 
            THEN 1 
        ELSE 0 
        END DESC
    , tt.limit_dt_to ASC
    , tt.task_seq ASC
    , ttw.worker_account_seq ASC
;