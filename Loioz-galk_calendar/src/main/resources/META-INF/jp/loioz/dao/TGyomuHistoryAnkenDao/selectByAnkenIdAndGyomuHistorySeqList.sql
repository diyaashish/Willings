SELECT
  * 
FROM
  t_gyomu_history_anken 
WHERE
  anken_id = /* ankenId */NULL 
  AND gyomu_history_seq IN /* gyomuHistorySeqList */(NULL);