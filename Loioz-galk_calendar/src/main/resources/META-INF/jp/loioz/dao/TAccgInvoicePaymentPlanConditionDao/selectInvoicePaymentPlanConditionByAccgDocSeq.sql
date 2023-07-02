SELECT 
  taippc.*
FROM
  t_accg_invoice tai
  INNER JOIN t_accg_invoice_payment_plan_condition taippc
    ON tai.invoice_seq = taippc.invoice_seq
WHERE
  tai.accg_doc_seq = /* accgDocSeq */null
;
