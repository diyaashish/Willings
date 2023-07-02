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
  -- タスク作成者を取得
  , (
      SELECT
        worker_account_seq
      FROM
        t_task_worker
      WHERE
        task_seq = tt.task_seq
        AND creater_flg = 1
  ) AS assigned_account_seq
FROM
  t_task tt 
  LEFT JOIN t_anken ta
    USING (anken_id)
  LEFT JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1'
  AND ttw.creater_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
  AND ttw.entrust_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
  AND tt.task_status != /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
ORDER BY
  tt.task_seq DESC
;