SELECT
  * 
FROM
  t_anken_jiken 
WHERE
  anken_id = /* ankenId */NULL 
  AND customer_id = /* customerId */1 
ORDER BY
  customer_id
  , CASE WHEN taiho_date IS NULL THEN 1 ELSE 0 END
  , taiho_date;
