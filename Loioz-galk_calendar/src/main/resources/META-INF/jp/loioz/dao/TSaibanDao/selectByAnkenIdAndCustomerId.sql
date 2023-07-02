SELECT
  ts.* 
FROM
  t_saiban ts 
  LEFT JOIN t_saiban_customer tsc 
    ON ts.saiban_seq = tsc.saiban_seq 
WHERE
  ts.anken_id = /* ankenId */NULL 
  AND tsc.customer_id = /* customerId */NULL;