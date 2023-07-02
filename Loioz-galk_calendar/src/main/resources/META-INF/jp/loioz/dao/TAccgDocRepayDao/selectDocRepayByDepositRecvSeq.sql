SELECT
  tadr.*
FROM
  t_accg_doc_repay tadr 
  INNER JOIN t_accg_doc_repay_t_deposit_recv_mapping tadrtdrm
    ON tadr.doc_repay_seq = tadrtdrm.doc_repay_seq 
WHERE
  tadrtdrm.deposit_recv_seq IN /* depositRecvSeqList */(NULL) 
;
