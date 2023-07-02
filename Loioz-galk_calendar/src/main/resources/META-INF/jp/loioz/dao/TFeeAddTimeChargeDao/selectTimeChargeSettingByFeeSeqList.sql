SELECT 
  *
FROM
  t_fee_add_time_charge tfatc
WHERE
  tfatc.fee_seq IN /* feeSeqList */(null)
;
