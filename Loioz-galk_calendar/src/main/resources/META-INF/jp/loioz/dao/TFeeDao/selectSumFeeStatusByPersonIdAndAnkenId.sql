SELECT
  tf.person_id
  , tf.anken_id
  , tf.fee_payment_status
  , COALESCE(SUM(tf.fee_amount), 0) as fee_amount
  , COALESCE(SUM(tf.tax_amount), 0) as tax_amount
  , COALESCE(SUM(tf.withholding_amount), 0) as withholding_amount
FROM
  t_fee tf
WHERE
  tf.person_id = /* personId */NULL 
  AND tf.anken_id = /* ankenId */NULL
GROUP BY
  tf.person_id
  , tf.anken_id
  , tf.fee_payment_status
ORDER BY
  tf.person_id
  , tf.anken_id
;