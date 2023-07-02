SELECT
  * 
FROM
  t_anken_sekken 
WHERE
  anken_id = /* ankenId */NULL 
  AND customer_id IN /* customerIdList */(1) 
ORDER BY
  customer_id
  , sekken_start_at ASC;
