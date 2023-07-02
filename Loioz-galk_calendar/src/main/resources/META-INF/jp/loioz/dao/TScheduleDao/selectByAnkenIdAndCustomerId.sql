SELECT
  * 
FROM
  t_schedule 
WHERE
  customer_id = /* customerId */null 
  AND anken_id = /* ankenId */null 
  AND deleted_at IS NULL 
  AND deleted_by IS NULL;
