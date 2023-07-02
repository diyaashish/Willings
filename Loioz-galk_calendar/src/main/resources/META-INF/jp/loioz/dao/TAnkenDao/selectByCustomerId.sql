SELECT
  ta.* 
FROM
  t_anken ta 
  INNER JOIN t_anken_customer tac 
    USING (anken_id) 
WHERE
  tac.customer_id = /* customerId */1;