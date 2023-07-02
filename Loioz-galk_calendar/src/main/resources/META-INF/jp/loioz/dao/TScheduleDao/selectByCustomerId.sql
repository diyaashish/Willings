SELECT
  * 
FROM
  t_schedule 
WHERE
  customer_id = /* customerId */1
AND deleted_at IS NULL
AND deleted_by IS NULL;