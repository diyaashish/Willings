SELECT
  *
FROM
  t_kaikei_kiroku
WHERE
  customer_id = /* customerId */null
  AND anken_id = /* ankenId */null
  AND (hoshu_komoku_id = /* timeCharge */null)
ORDER BY
/*%if orderByHasseiDate */
  hassei_date ASC,created_at ASC
/*%else */
  created_at DESC
/*%end */