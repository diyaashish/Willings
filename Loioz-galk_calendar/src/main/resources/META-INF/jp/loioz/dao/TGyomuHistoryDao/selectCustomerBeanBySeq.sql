SELECT
  tghc.customer_id
, TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
FROM
  t_gyomu_history_customer tghc
  LEFT JOIN t_person tc
    ON tghc.customer_id = tc.customer_id
WHERE
  tghc.gyomu_history_seq = /* seq */8;