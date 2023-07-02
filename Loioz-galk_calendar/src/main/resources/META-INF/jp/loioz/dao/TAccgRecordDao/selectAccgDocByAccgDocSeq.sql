SELECT
  tad.accg_doc_seq
  , tad.person_id
  , tad.anken_id
  , tad.accg_doc_type
  , tar.accg_record_seq
  , tar.accg_record_seq
  , tar.fee_amount_expect
  , tar.deposit_recv_amount_expect
  , tar.deposit_payment_amount_expect
FROM
  t_accg_doc tad 
  LEFT JOIN t_accg_record tar 
    USING (accg_doc_seq) 
WHERE
  tad.accg_doc_seq = /* accgDocSeq */NULL
;
