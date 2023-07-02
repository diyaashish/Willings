SELECT 
  *
FROM
  t_deposit_recv tdr
WHERE
  tdr.deposit_recv_seq IN /*depositRecvSeqList*/(null)
;
