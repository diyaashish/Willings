SELECT
  TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
  , tk.kanyosha_seq AS id
  , tk.disp_order AS dispOrder
FROM
  t_kanyosha AS tk
  INNER JOIN t_person AS tc
    ON tc.person_id = tk.person_id
WHERE
  tk.anken_id = /* ankenId */null
ORDER BY
  tk.disp_order;