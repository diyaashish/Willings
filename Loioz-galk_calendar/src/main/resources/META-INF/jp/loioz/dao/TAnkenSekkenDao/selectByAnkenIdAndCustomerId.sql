SELECT
  * 
FROM
  t_anken_sekken 
WHERE
  anken_id = /* ankenId */NULL 
  AND customer_id = /* customerId */NULL 
ORDER BY
  sekken_start_at ASC;
