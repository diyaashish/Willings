-- 今日のタスク件数
SELECT
  COUNT(tt.task_seq) count
  , 1 sort_order
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.today_task_date = /* date */null
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
UNION ALL
-- 今日のタスクの未読件数
SELECT
  COUNT(tt.task_seq) count
  , 2 sort_order
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.today_task_date = /* date */null
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
UNION ALL 
-- 期限付きタスク件数
SELECT
  COUNT(tt.task_seq) count
  , 3 sort_order
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
  AND tt.limit_dt_to > /* yesterday */null
UNION ALL
-- 期限付きタスクの未読件数
SELECT
  COUNT(tt.task_seq) count
  , 4 sort_order
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
  AND tt.limit_dt_to > /* yesterday */null
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
UNION ALL
-- すべてのタスク件数取得
SELECT
  COUNT(tt.task_seq) count
  , 5 sort_order
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
  AND ( 
    -- タスク作成者かつ作業者
    EXISTS ( 
      SELECT
        * 
      FROM
        t_task_worker ttw 
      WHERE
        ttw.task_seq = tt.task_seq 
        AND ttw.worker_account_seq = /* accountId */'1' 
        AND ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
        AND ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    ) 
    -- 作成したタスクに別アカウントが作業者として割り当てられていない
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_task_worker ttw 
      WHERE
        ttw.task_seq = tt.task_seq 
        AND ttw.worker_account_seq = /* accountId */'1' 
        AND ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
        AND ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
        AND (SELECT COUNT(*) FROM t_task_worker WHERE task_seq = tt.task_seq GROUP BY task_seq) = 1
    )
  )
UNION ALL
-- すべてのタスクの未読件数取得
SELECT
  COUNT(tt.task_seq) count
  , 6 sort_order
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
  AND ( 
    -- タスク作成者かつ作業者
    EXISTS ( 
      SELECT
        * 
      FROM
        t_task_worker ttw 
      WHERE
        ttw.task_seq = tt.task_seq 
        AND ttw.worker_account_seq = /* accountId */'1' 
        AND ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
        AND ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    ) 
    -- 作成したタスクに別アカウントが作業者として割り当てられていない
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_task_worker ttw 
      WHERE
        ttw.task_seq = tt.task_seq 
        AND ttw.worker_account_seq = /* accountId */'1' 
        AND ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
        AND ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
        AND (SELECT COUNT(*) FROM t_task_worker WHERE task_seq = tt.task_seq GROUP BY task_seq) = 1
    )
  )
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
UNION ALL
-- 期限を過ぎたタスク件数を取得
SELECT
  COUNT(tt.task_seq) count
  , 7 sort_order
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
UNION ALL
-- 期限を過ぎたタスクの未読件数を取得
SELECT
  COUNT(tt.task_seq) count
  , 8 sort_order
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
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
UNION ALL
-- 割り当てられたタスク件数を取得
SELECT
  COUNT(tt.task_seq) count
  , 9 sort_order
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.creater_flg = '0'
  AND ttw.entrust_flg = '0'
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
UNION ALL
-- 割り当てられたタスクの未読件数を取得
SELECT
  COUNT(tt.task_seq) count
  , 10 sort_order
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.creater_flg = '0'
  AND ttw.entrust_flg = '0'
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
UNION ALL
-- 割り当てたタスク件数を取得
SELECT
  COUNT(tt.task_seq) count
  , 11 sort_order
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
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
UNION ALL
-- 割り当てたタスクの未読件数を取得
SELECT
  COUNT(tt.task_seq) count
  , 12 sort_order
FROM
  t_task tt 
  INNER JOIN t_task_worker ttw 
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
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
UNION ALL
-- 完了タスクの未読件数取得
SELECT
  COUNT(tt.task_seq) count
  , 13 sort_order
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
  tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  -- 未読のタスクを対象とする
  AND ( 
    ttw.new_task_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
    OR ttw.new_history_kakunin_flg = /* @jp.loioz.common.constant.CommonConstant$MidokuKidokuCd@MIDOKU */'0'
  )
ORDER BY sort_order
;