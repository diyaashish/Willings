SELECT
  tc.ginko_name
  , tc.shiten_name
  , tc.shiten_no
  , tc.koza_type
  , tc.koza_no
  , tc.koza_name 
FROM
  t_person tc
WHERE
  tc.customer_id = /* customerId */null; 