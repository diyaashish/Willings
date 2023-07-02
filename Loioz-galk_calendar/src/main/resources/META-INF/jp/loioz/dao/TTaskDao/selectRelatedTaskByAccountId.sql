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
  , ttw.task_worker_seq
  , ttw.creater_flg
  , ttw.entrust_flg
  , ttw.new_task_kakunin_flg
  , ttw.new_history_kakunin_flg
  , ttw.disp_order
  , ttw.today_task_disp_order
  , ttw.today_task_date 
  -- order by で使用。「すべてのタスク」と「割り当てたタスク」を区別するため作業者レコード数を取得。
  , (SELECT COUNT(*) FROM t_task_worker WHERE task_seq = tt.task_seq GROUP BY task_seq) worker_count
FROM
  t_task tt 
  LEFT JOIN t_anken ta 
    USING (anken_id) 
  INNER JOIN ( 
    SELECT
      * 
    FROM
      t_task_worker ttw 
    WHERE
      ttw.worker_account_seq = /* accountId */'1'
  ) ttw 
    ON tt.task_seq = ttw.task_seq 
WHERE
  tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9' 
  /*%if assignFlg==false */
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
    -- 割り当てられたタスク（別アカウントが作成したタスクに作業者として割り当てられている）
    OR EXISTS (
      SELECT
        *
      FROM
        t_task_worker ttw
      WHERE
        ttw.task_seq = tt.task_seq
        AND ttw.worker_account_seq = /* accountId */'1'
        AND ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
        AND ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    )
  )
  /*%end */
  /*%if taskSeq != null */
  AND tt.task_seq = /* taskSeq */'1'
  /*%end */
ORDER BY
  1 = 1
  /*%if @jp.loioz.common.constant.CommonConstant$AllTaskListSortKey@DEFAULT.equalsByCode(sortKeyCd) */
  -- すべてのタスクが先頭（作成したタスクの作業者、作成したタスクに作業者がいない）
  , (
      (ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' and ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0')
      or
      (ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' and ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' and worker_count = 1)
    ) DESC
  -- 「すべてのタスクを先頭」、「割り当てられたタスク」を最後にすることで「割り当てたタスク」が中間にくるようにしています
  -- 作成者でないものが最後（割り当てられたタスク）
  , ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' ASC
  -- すべてのタスクの表示順
  , ttw.disp_order DESC
  , tt.task_seq DESC
  /*%end */
  /*%if @jp.loioz.common.constant.CommonConstant$AllTaskListSortKey@LIMIT_DATE_ASC.equalsByCode(sortKeyCd) */
  , tt.limit_dt_to IS NULL ASC, tt.limit_dt_to
  /*%end */
  /*%if @jp.loioz.common.constant.CommonConstant$AllTaskListSortKey@LIMIT_DATE_DESC.equalsByCode(sortKeyCd) */
  , tt.limit_dt_to DESC
  /*%end */
  , tt.updated_at DESC
  , tt.task_seq DESC
;