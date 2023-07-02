SELECT
  tgh.gyomu_history_seq 
FROM
  t_gyomu_history tgh 
  LEFT JOIN t_gyomu_history_anken tgha 
    USING (gyomu_history_seq) 
  LEFT JOIN t_gyomu_history_customer tghc 
    USING (gyomu_history_seq) 
WHERE
  tghc.customer_id = /* searchForm.customerId */null 
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
GROUP BY
  tgh.gyomu_history_seq
ORDER BY
  tgh.kotei_flg DESC,
  tgh.supported_at DESC,
  tgh.updated_at DESC;