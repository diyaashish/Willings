SELECT
  ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei, ''), ' ', COALESCE(tc.customer_name_mei, ''))) AS customer_name
  ,tac.anken_status
FROM
  t_anken ta
  INNER JOIN t_anken_customer tac
    ON ta.anken_id = tac.anken_id
  INNER JOIN t_person tc
    ON tac.customer_id = tc.customer_id
WHERE
  1 = 1
/*%if ankenId != null */
  AND tac.anken_id = /* ankenId */1
/*%end */
/*%if customerId != null */
  AND tac.customer_id = /* customerId */1
/*%end */
;