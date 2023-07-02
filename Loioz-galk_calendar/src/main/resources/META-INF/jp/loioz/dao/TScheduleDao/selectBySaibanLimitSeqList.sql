SELECT
  * 
FROM
  t_schedule 
WHERE
  saiban_limit_seq IN /* saibanLimitSeqList */(1) 
  AND deleted_at IS NULL 
  AND deleted_by IS NULL;