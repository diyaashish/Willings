SELECT 
  *
FROM
  t_accg_doc_invoice_deposit_t_deposit_recv_mapping
WHERE
  doc_invoice_deposit_seq IN /* docInvoiceDepositSeqList */(null)
  AND deposit_recv_seq IN /* depositRecvSeqList */(null)
;
