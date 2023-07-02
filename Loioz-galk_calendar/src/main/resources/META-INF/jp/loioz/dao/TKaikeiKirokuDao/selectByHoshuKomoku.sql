SELECT
  *
FROM
  t_kaikei_kiroku
WHERE
  1 = 1
/*%if customerId != null */
  AND customer_id = /* customerId */NULL
/*%end */
/*%if ankenId != null */
  AND anken_id = /* ankenId */NULL
/*%end */
/*%if hoshuKomokuId != null*/
  AND (hoshu_komoku_id = /* hoshuKomokuId */NULL)
/*%end */
  ;
