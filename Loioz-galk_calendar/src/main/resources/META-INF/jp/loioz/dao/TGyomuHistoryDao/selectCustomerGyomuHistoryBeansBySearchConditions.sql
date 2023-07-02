SELECT
  tgh.gyomu_history_seq
  , tgh.transition_type
  , tgh.subject
  , tgh.main_text
  , tgh.important_flg
  , tgh.kotei_flg
  , tgh.supported_at
  , tgh.dengon_sent_flg
  , tgh.created_by
  , tghc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , tgha.anken_id 
  , ta.anken_name
  , ta.bunya_id
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
WHERE
  tgh.gyomu_history_seq IN /* seqList */(null)
  AND tghc.customer_id = /* searchForm.customerId */null 
  /*%if searchForm.searchAnkenId != null */
   AND EXISTS ( 
    SELECT
      * 
    FROM
      t_gyomu_history_anken 
    WHERE
      gyomu_history_seq = tgh.gyomu_history_seq 
      AND anken_id = /* searchForm.searchAnkenId */null
  )
  /*%end */
  /*%if searchForm.isImportant*/
   AND tgh.important_flg = '1'
  /*%end */
  /*%if searchForm.searchText !="" */
  AND (
    tgh.subject LIKE /* @infix(searchForm.searchText) */''
    OR tgh.main_text LIKE /* @infix(searchForm.searchText) */''
  )
  /*%end */
ORDER BY
  tgh.kotei_flg DESC,
  tgh.supported_at DESC,
  tgh.updated_at DESC;