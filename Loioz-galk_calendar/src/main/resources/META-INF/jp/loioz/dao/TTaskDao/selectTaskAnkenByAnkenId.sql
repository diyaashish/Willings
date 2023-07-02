SELECT
/*%if countFlg==true */
  COUNT(*) count
/*%end */
/*%if countFlg==false */
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
/*%end */
FROM
  t_task tt 
  LEFT JOIN t_anken ta 
    USING (anken_id) 
WHERE
  tt.anken_id = /* ankenId */NULL 
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListDispKey@INCOMPLETE.equalsByCode(dispKeyCd) */
  AND ( 
    tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@PREPARING */'1' 
    OR tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@WORKING */'2'
  ) 
/*%end */
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListDispKey@COMPLETED.equalsByCode(dispKeyCd) */
  AND ( 
    tt.task_status = /* @jp.loioz.common.constant.CommonConstant$TaskStatus@COMPLETED */'9'
  ) 
/*%end */
ORDER BY
  1 = 1
  -- countを取得するときは、ORDER BY を設定しない
/*%if countFlg==false */
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListSortKey@DEFAULT.equalsByCode(sortKeyCd) */
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListDispKey@INCOMPLETE.equalsByCode(dispKeyCd) */
  , tt.task_seq DESC
/*%end */
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListDispKey@COMPLETED.equalsByCode(dispKeyCd) */
  , tt.updated_at DESC
/*%end */
/*%end */
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListSortKey@LIMIT_DATE_ASC.equalsByCode(sortKeyCd) */
  , tt.limit_dt_to IS NULL ASC
  , tt.limit_dt_to
  , tt.updated_at DESC
/*%end */
/*%if @jp.loioz.common.constant.CommonConstant$TaskAnkenListSortKey@LIMIT_DATE_DESC.equalsByCode(sortKeyCd) */
  , tt.limit_dt_to DESC
  , tt.updated_at DESC
/*%end */
/*%end */
;
