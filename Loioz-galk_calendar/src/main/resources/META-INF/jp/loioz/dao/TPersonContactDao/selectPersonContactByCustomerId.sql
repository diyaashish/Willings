SELECT
  tcc.* 
FROM
  t_person tc 
  INNER JOIN t_person_contact tcc 
    USING (person_id) 
WHERE
  tc.customer_id IN /* customerId */(NULL)
;
