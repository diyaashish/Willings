SELECT
  tkk.kaikei_kiroku_seq
  , tkk.hassei_date
  , tkk.nyushukkin_komoku_id
  , tkk.nyushukkin_type
  , tkk.nyukin_gaku
  , tkk.shukkin_gaku
  , tkk.tax_gaku
  , tkk.tax_rate
  , tkk.tekiyo
  , tkk.created_by
  , tkk.seisan_date
  , tkk.pool_type
  , tsk.seisan_id
  , mnk.komoku_name
  , CASE 
    WHEN tkk.nyushukkin_komoku_id IS NOT NULL 
      THEN mnk.tax_flg 
    ELSE tkk.tax_flg 
    END AS tax_flg
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei, ''), ' ', COALESCE(tc.customer_name_mei, ''))) AS customer_name
  , CONCAT(COALESCE(ma.account_name_sei, ''), ' ', COALESCE(ma.account_name_mei, '')) AS account_name 
  , tac.anken_status
FROM
  t_kaikei_kiroku tkk 
  LEFT OUTER JOIN t_anken ta 
    ON tkk.anken_id = ta.anken_id 
  LEFT OUTER JOIN t_person tc 
    ON tkk.customer_id = tc.customer_id 
  LEFT OUTER JOIN t_anken_customer tac 
    ON tkk.anken_id = tac.anken_id 
    AND tkk.customer_id = tac.customer_id 
  LEFT OUTER JOIN m_nyushukkin_komoku mnk 
    ON tkk.nyushukkin_komoku_id = mnk.nyushukkin_komoku_id 
  LEFT OUTER JOIN m_account ma 
    ON tkk.created_by = ma.account_seq 
  LEFT OUTER JOIN t_seisan_kiroku tsk 
    ON tkk.seisan_seq = tsk.seisan_seq 
WHERE
  tkk.nyushukkin_komoku_id IS NOT NULL 
  /** 会計情報の検索 */
  /*%if searchCondition.isAnkenView */
    AND tkk.anken_id = /* searchCondition.transitionAnkenId */NULL 
  /*%end*/
  /*%if searchCondition.isCustomerView */
    AND tkk.customer_id = /* searchCondition.transitionCustomerId */NULL 
  /*%end*/
  /*%if !searchCondition.isCompleted */
    /*%if searchCondition.isNotEmpty(searchCondition.completedExcludeAnkenStatus) */
      AND tac.anken_status NOT IN /* searchCondition.completedExcludeAnkenStatus */(NULL) 
    /*%end */
  /*%end*/
ORDER BY
  tkk.hassei_date ASC
  , tkk.created_at ASC
;
