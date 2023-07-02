SELECT
  tac.*
FROM
  t_anken_customer tac 
  INNER JOIN t_person tc 
    USING (customer_id)
WHERE
  tc.person_id = /* personId */1
;