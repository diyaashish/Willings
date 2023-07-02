SELECT
  * 
FROM
  t_schedule 
WHERE
  saiban_limit_seq = /*saibanLimitSeq */NULL
  AND deleted_at IS NULL 
  AND deleted_by IS NULL;