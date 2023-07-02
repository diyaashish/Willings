SELECT
  max(seisan_id) 
FROM
  t_seisan_kiroku 
WHERE
  customer_id = /* customerId */1;