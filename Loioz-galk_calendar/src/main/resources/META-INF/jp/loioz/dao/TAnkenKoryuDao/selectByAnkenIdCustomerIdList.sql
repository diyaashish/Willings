SELECT
  * 
FROM
  t_anken_koryu 
WHERE
  anken_id = /* ankenId */NULL 
  AND customer_id IN /* customerIdList */(1) 
ORDER BY
  customer_id
  , koryu_date
;