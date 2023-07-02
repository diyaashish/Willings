SELECT
  tadid.*
FROM
  t_accg_doc_invoice tadi
  INNER JOIN t_accg_doc_invoice_deposit tadid 
    ON tadi.doc_invoice_seq = tadid.doc_invoice_seq
WHERE
  tadi.accg_doc_seq = /* accgDocSeq */NULL 
;
