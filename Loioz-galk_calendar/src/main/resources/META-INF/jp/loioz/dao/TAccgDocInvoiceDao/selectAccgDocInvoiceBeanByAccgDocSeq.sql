SELECT
 -- 請求項目に関するデータ
  tadi.doc_invoice_seq
  , tadi.doc_invoice_order
  -- 請求項目-報酬に関するデータ
  , tadif.doc_invoice_fee_seq
  , tadif.fee_transaction_date
  , tadif.sum_text AS fee_sum_text
  -- 報酬に関するデータ
  , tf.fee_seq
  , tf.fee_item_name
  , tf.fee_amount
  , tf.tax_flg
  , tf.tax_rate_type
  , tf.withholding_flg
  , tf.withholding_amount
  , tf.fee_time_charge_flg
  , tfatc.hour_price
  , tfatc.work_time_minute
  -- 請求項目-預り金に関するデータ
  , tadidtdrm.deposit_recv_seq
  , tadid.doc_invoice_deposit_seq
  , tadid.deposit_transaction_date
  , tadid.deposit_item_name
  , tadid.deposit_amount
  , tadid.invoice_deposit_type
  , tadid.sum_text AS deposit_sum_text
  -- 請求項目-その他に関するデータ
  , tadio.doc_invoice_other_seq
  , tadio.other_item_type
  , tadio.other_transaction_date
  , tadio.other_item_name
  , tadio.other_amount
  , tadio.discount_tax_rate_type
  , tadio.discount_withholding_flg
  , tadio.sum_text AS other_sum_text 
FROM
  t_accg_doc_invoice tadi
  -- 請求項目-報酬
  LEFT JOIN t_accg_doc_invoice_fee tadif
    ON tadi.doc_invoice_seq = tadif.doc_invoice_seq
  -- 報酬
  LEFT JOIN t_fee tf
    ON tadif.fee_seq = tf.fee_seq
  LEFT JOIN t_fee_add_time_charge tfatc
    ON tf.fee_seq = tfatc.fee_seq
  -- 請求項目-預り金
  LEFT JOIN t_accg_doc_invoice_deposit tadid
    ON tadi.doc_invoice_seq = tadid.doc_invoice_seq
  LEFT JOIN (
    SELECT
      doc_invoice_deposit_seq
      , GROUP_CONCAT(deposit_recv_seq) AS deposit_recv_seq
    FROM
      t_accg_doc_invoice_deposit_t_deposit_recv_mapping mapping
    GROUP BY
      mapping.doc_invoice_deposit_seq
    ) AS tadidtdrm
      ON tadid.doc_invoice_deposit_seq = tadidtdrm.doc_invoice_deposit_seq
  -- 請求項目-その他
  LEFT JOIN t_accg_doc_invoice_other tadio
    ON tadi.doc_invoice_seq = tadio.doc_invoice_seq
WHERE
  tadi.accg_doc_seq = /* accgDocSeq */'1'
ORDER BY
  tadi.doc_invoice_order
;
