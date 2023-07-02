SELECT
  tadi.accg_doc_seq
  , SUM( 
    CASE tadid.invoice_deposit_type 
      WHEN /* @jp.loioz.common.constant.CommonConstant$InvoiceDepositType@DEPOSIT */'1' THEN COALESCE(tadid.deposit_amount, 0)
      ELSE 0 
      END
  ) AS total_deposit_amount
  , SUM( 
    CASE tadid.invoice_deposit_type 
      WHEN /* @jp.loioz.common.constant.CommonConstant$InvoiceDepositType@EXPENSE */'2' THEN COALESCE(tadid.deposit_amount, 0)
      ELSE 0 
      END
  ) AS total_advance_money
FROM
  t_accg_doc_invoice tadi
  INNER JOIN t_accg_doc_invoice_deposit tadid
    ON tadi.doc_invoice_seq = tadid.doc_invoice_seq
WHERE
  tadi.accg_doc_seq = /* accgDocSeq */NULL
GROUP BY
  tadi.accg_doc_seq
;
