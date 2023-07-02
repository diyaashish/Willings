SELECT
  tp.* 
FROM
  t_person tp 
  LEFT JOIN t_anken_customer tac 
    USING (customer_id) 
WHERE
  tac.anken_id IN /* ankenIdList */(NULL) 
ORDER BY
  tp.customer_id;