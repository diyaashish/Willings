SELECT
  tadi.*
FROM
  t_accg_doc_invoice tadi
  INNER JOIN t_accg_doc_invoice_deposit tadid
    ON tadi.doc_invoice_seq = tadid.doc_invoice_seq
  INNER JOIN t_accg_doc_invoice_deposit_t_deposit_recv_mapping tadidtdrm
    ON tadid.doc_invoice_deposit_seq = tadidtdrm.doc_invoice_deposit_seq
WHERE
  tadidtdrm.deposit_recv_seq IN /* depositRecvSeqList */(NULL)
;