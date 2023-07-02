SELECT
  * 
FROM
  t_kaikei_kiroku 
WHERE
  anken_id = /* ankenId */NULL 
  AND customer_id = /* customerId */NULL
 /*%if seisanFlg */
  AND seisan_date IS NULL
  /*%end*/;