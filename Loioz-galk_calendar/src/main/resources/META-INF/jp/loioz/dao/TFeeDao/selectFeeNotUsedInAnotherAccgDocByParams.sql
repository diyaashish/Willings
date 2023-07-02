SELECT
  tf.fee_seq
  , tac.customer_id AS person_id
  , tac.anken_id
  , tf.fee_item_name
  , tf.fee_date
  , tf.fee_payment_status
  , tf.tax_flg
  , tf.tax_rate_type
  , tf.withholding_flg  , COALESCE(tf.fee_amount, 0) AS fee_amount
  , COALESCE(tf.tax_amount, 0) AS tax_amount
  , COALESCE(tf.withholding_amount, 0) AS withholding_amount
  -- 消費税込みの報酬額
  , COALESCE(tf.fee_amount, 0) + COALESCE(tf.tax_amount, 0) AS fee_amount_tax_in
  -- 消費税、源泉徴収税引後の報酬額
  , CASE tf.withholding_flg
      WHEN /* @jp.loioz.common.constant.CommonConstant$WithholdingFlg@DO_NOT */'0' THEN COALESCE(tf.fee_amount, 0) + COALESCE(tf.tax_amount, 0) 
      WHEN /* @jp.loioz.common.constant.CommonConstant$WithholdingFlg@DO */'1' THEN COALESCE(tf.fee_amount, 0) + COALESCE(tf.tax_amount, 0) - COALESCE(tf.withholding_amount, 0) 
      ELSE COALESCE(tf.fee_amount, 0) + COALESCE(tf.tax_amount, 0) 
    END AS after_withholding_tax
  , tai.invoice_no
  , tai.invoice_payment_status
  , tas.statement_no
  , tas.statement_refund_status
  , tf.fee_time_charge_flg
  , tfatc.hour_price
  , tfatc.work_time_minute
  , tf.fee_memo
  , tf.sum_text 
  , tf.created_at
  , tf.created_by
  , tf.updated_at
  , tf.updated_by
FROM
  t_anken_customer tac 
  INNER JOIN t_fee tf 
    ON tf.anken_id = tac.anken_id 
    AND tac.customer_id = tf.person_id 
  LEFT JOIN t_fee_add_time_charge tfatc
    ON tfatc.fee_seq = tf.fee_seq
  LEFT JOIN t_accg_invoice tai 
    ON tf.accg_doc_seq = tai.accg_doc_seq
  LEFT JOIN t_accg_statement tas 
    ON tf.accg_doc_seq = tas.accg_doc_seq
WHERE
  (tf.accg_doc_seq IS NULL OR tf.accg_doc_seq = /* accgDocSeq */null)
  AND tf.fee_payment_status = /* @jp.loioz.common.constant.CommonConstant$FeePaymentStatus@UNCLAIMED */'1'
  /*%if ankenId != null */
  AND tac.anken_id = /* ankenId */null
  /*%end */
  /*%if personId != null */
  AND tac.customer_id = /* personId */null
  /*%end */

ORDER BY
  tf.fee_seq
;
