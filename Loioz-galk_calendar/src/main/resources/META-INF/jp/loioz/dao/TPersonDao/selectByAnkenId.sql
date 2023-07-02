SELECT
  tc.* 
FROM
  t_person tc 
  LEFT JOIN t_anken_customer tac 
    USING (customer_id) 
WHERE
  tac.anken_id = /* ankenId */1 
ORDER BY
  tc.customer_id;