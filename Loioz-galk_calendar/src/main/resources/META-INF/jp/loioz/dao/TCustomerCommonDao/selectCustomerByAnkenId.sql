SELECT
  TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
  , tc.customer_id AS id
FROM
  t_anken_customer AS tac
  INNER JOIN t_person AS tc
    ON tac.customer_id = tc.customer_id
WHERE
  tac.anken_id = /* ankenId */null
ORDER BY
  CONCAT(COALESCE(tc.customer_name_sei,''),COALESCE(tc.customer_name_mei,''));