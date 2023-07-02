SELECT
  *
FROM
  t_nyushukkin_yotei
WHERE
  anken_id = /* ankenId */NULL
  OR customer_id = /* customerId */NULL
ORDER BY
  created_at DESC
  , nyushukkin_yotei_seq DESC
LIMIT
  1;