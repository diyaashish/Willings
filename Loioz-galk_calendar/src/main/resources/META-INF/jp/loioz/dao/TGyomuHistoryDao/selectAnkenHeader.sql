SELECT
  tc.customer_id AS id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
FROM
  t_anken_customer tac 
  LEFT JOIN t_person tc 
    USING (customer_id) 
WHERE
  tac.anken_id = /* ankenId */null
ORDER BY
  tc.customer_id;