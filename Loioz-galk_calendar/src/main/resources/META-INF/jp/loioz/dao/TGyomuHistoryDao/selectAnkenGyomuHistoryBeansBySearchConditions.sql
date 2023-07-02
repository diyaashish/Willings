SELECT
  tgh.gyomu_history_seq
  , tgh.transition_type
  , tgh.saiban_seq
  , tgh.subject
  , tgh.main_text
  , tgh.important_flg
  , tgh.kotei_flg
  , tgh.dengon_sent_flg
  , tgh.created_by
  , tgh.supported_at
  , tghc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , tgha.anken_id
  , ta.anken_name
  , ta.bunya_id
  , ts.saiban_branch_no
FROM
  t_gyomu_history tgh 
  LEFT JOIN t_gyomu_history_anken tgha 
    USING (gyomu_history_seq) 
  LEFT JOIN t_gyomu_history_customer tghc 
    USING (gyomu_history_seq) 
  LEFT JOIN t_person tc
    USING (customer_id)
  LEFT JOIN t_anken ta
    USING (anken_id)
  LEFT JOIN t_saiban ts
    USING (saiban_seq)
WHERE
  tgh.gyomu_history_seq IN /* seqList */(null)
  AND tgha.anken_id = /* searchForm.transitionAnkenId */null 
  /*%if searchForm.searchCustomerId != null */
   AND EXISTS ( 
    SELECT
      * 
    FROM
      t_gyomu_history_customer
    WHERE
      gyomu_history_seq = tgh.gyomu_history_seq 
      AND customer_id = /* searchForm.searchCustomerId */null
  )
  /*%end */
  /*%if searchForm.isImportant */
   AND tgh.important_flg = '1'
  /*%end */
  /*%if searchForm.searchText !="" */
  AND (
    tgh.subject LIKE /* @infix(searchForm.searchText) */''
    OR tgh.main_text LIKE /* @infix(searchForm.searchText) */''
  )
  /*%end */
ORDER BY
  tgh.kotei_flg DESC
  , tgh.supported_at DESC
  , tgh.updated_at DESC;