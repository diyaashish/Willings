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
  ) ttw ON tt.task_seq = ttw.task_seq
WHERE
  tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
ORDER BY
  /*%if @jp.loioz.common.constant.CommonConstant$AllTaskListSortKey@LIMIT_DATE_ASC.equalsByCode(sortKeyCd) */
  tt.limit_dt_to IS NULL ASC, tt.limit_dt_to,
  /*%end */
  /*%if @jp.loioz.common.constant.CommonConstant$AllTaskListSortKey@LIMIT_DATE_DESC.equalsByCode(sortKeyCd) */
  tt.limit_dt_to DESC,
  /*%end */
  tt.updated_at DESC,
  tt.task_seq DESC
;