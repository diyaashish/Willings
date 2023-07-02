SELECT
  taipp.*
FROM
  t_accg_invoice tai
  INNER JOIN t_accg_invoice_payment_plan taipp
    ON tai.invoice_seq = taipp.invoice_seq
WHERE
  tai.accg_doc_seq = /* accgDocSeq */null
;