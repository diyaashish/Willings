SELECT
  ta.anken_id AS id
  , ta.anken_name AS name
FROM
  t_anken_customer tac 
  LEFT JOIN t_anken ta 
    USING (anken_id) 
WHERE
  tac.customer_id = /* customerId */null
ORDER BY
  ta.anken_id;