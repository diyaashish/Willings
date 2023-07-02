SELECT
  *
FROM
  t_kaikei_kiroku
WHERE
  customer_id = /* customerId */NULL
  AND seisan_seq = /* seisanSeq */NULL
ORDER BY
  kaikei_kiroku_seq;