SELECT
  tt.task_seq
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