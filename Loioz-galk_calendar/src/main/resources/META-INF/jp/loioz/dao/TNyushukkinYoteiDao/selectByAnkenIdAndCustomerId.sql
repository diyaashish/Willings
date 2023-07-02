SELECT
  * 
FROM
  t_nyushukkin_yotei 
WHERE
  anken_id = /* ankenId */NULL 
  AND customer_id = /* customerId */NULL
    /*%if nyushukkinFlg */
  AND nyushukkin_gaku IS NULL
  /*%end*/
  ;