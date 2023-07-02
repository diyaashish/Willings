SELECT
  * 
FROM
  t_gyomu_history_customer 
WHERE
  customer_id = /* customerId */NULL 
  AND gyomu_history_seq IN /* gyomuHistorySeqList */(NULL);