SELECT
  *
FROM
  t_accg_doc_invoice_deposit tadid 
WHERE
  tadid.doc_invoice_deposit_seq IN /* docInvoiceDepositSeqList */(1) 
;