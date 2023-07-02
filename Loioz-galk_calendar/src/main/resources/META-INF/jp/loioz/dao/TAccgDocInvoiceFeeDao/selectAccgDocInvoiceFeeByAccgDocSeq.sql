SELECT
  tadif.*
FROM
  t_accg_doc_invoice tadi
  INNER JOIN t_accg_doc_invoice_fee tadif
    ON tadi.doc_invoice_seq = tadif.doc_invoice_seq
WHERE
  tadi.accg_doc_seq = /* accgDocSeq */null
;