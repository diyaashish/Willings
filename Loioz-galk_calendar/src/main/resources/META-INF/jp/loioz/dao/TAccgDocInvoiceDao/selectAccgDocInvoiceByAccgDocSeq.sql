SELECT
  *
FROM
  t_accg_doc_invoice tadi
WHERE
  tadi.accg_doc_seq = /* accgDocSeq */'1'
ORDER BY
  tadi.doc_invoice_order
;