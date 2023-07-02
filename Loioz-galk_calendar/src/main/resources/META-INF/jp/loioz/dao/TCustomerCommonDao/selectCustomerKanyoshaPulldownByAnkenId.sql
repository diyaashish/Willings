SELECT
  TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
  , tc.person_id AS id
  , 1 AS sort_order
FROM
  t_anken_customer AS tac
  INNER JOIN t_person AS tc
    ON tac.customer_id = tc.customer_id
WHERE
  tac.anken_id = /* ankenId */null

UNION ALL

SELECT
  TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
  , tk.person_id AS id
  , 2 AS sort_order
FROM
  t_kanyosha AS tk
  INNER JOIN t_person AS tc
    ON tc.person_id = tk.person_id
WHERE
  tk.anken_id = /* ankenId */null
ORDER BY sort_order
;