SELECT 
  *
FROM
  t_accg_doc_repay_t_deposit_recv_mapping
WHERE
  doc_repay_seq IN /* docRepaySeqList */(null)
  AND deposit_recv_seq IN /* depositRecvSeqList */(null)
;
