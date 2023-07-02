SELECT
  tny.nyushukkin_yotei_seq
  , tny.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , tny.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tac.anken_status
  , tny.nyushukkin_type
  , tny.nyushukkin_komoku_id
  , tny.nyushukkin_yotei_date
  , tny.nyushukkin_yotei_gaku
  , tny.nyushukkin_date
  , tny.nyushukkin_gaku
  , tny.tekiyo
  , tsk.seisan_id 
  , tsk.seisan_seq
FROM
  t_nyushukkin_yotei tny 
  LEFT OUTER JOIN t_anken ta 
    ON tny.anken_id = ta.anken_id 
  LEFT OUTER JOIN t_person tc 
    ON tny.customer_id = tc.customer_id 
  LEFT JOIN t_anken_customer tac
    ON tny.anken_id = tac.anken_id
    AND tny.customer_id = tac.customer_id
  LEFT JOIN t_seisan_kiroku tsk 
    ON tny.seisan_seq = tsk.seisan_seq
WHERE
  1=1
  /*%if searchCondition.isAnkenView */
  AND tny.anken_id = /* searchCondition.transitionAnkenId */null 
  /*%end*/
  /*%if searchCondition.isCustomerView */
  AND tny.customer_id = /* searchCondition.transitionCustomerId */null 
  /*%end*/
  
  /*%if !searchCondition.isCompleted */
    /*%if searchCondition.isNotEmpty(searchCondition.completedExcludeAnkenStatus) */
    AND tac.anken_status NOT IN /* searchCondition.completedExcludeAnkenStatus */(NULL) 
    /*%end */
  /*%end*/
ORDER BY
  tny.nyushukkin_yotei_date ASC
  , tny.created_at ASC
;