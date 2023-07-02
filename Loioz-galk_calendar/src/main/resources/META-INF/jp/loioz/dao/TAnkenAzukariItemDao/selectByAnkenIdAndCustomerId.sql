SELECT
  * 
FROM
  t_anken_azukari_item 
WHERE
  anken_id = /* ankenId */NULL 
  AND ( 
    azukari_from_customer_id = /* customerId */NULL
    OR return_to_customer_id = /* customerId */NULL
  )
;
