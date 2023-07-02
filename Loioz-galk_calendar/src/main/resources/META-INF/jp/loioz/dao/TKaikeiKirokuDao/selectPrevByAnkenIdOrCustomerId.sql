SELECT
  *
FROM
  t_kaikei_kiroku
WHERE
  (anken_id = /* ankenId */NULL
  OR customer_id = /* customerId */NULL)
  AND nyushukkin_komoku_id IS NOT NULL
ORDER BY
  created_at DESC
  , kaikei_kiroku_seq DESC
LIMIT
  1;