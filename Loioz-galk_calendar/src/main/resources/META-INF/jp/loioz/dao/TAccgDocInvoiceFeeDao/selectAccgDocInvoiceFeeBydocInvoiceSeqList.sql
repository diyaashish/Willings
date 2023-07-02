SELECT
  *
FROM
  t_accg_doc_invoice_fee tadif
WHERE
  tadif.doc_invoice_seq IN /* docInvoiceSeqList */(null)
;