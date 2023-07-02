SELECT
  *
FROM
  t_kaikei_kiroku
WHERE
  anken_id = /* ankenId */NULL
  AND customer_id = /* customerId */NULL
  AND hoshu_komoku_id IS NOT NULL
ORDER BY
  created_at DESC
  , kaikei_kiroku_seq DESC
LIMIT
  1;
