SELECT
  tard.accg_record_detail_seq
  , tard.accg_record_seq
  , tard.record_date
  , tard.record_type
  , tard.record_separate_input_flg
  , tard.record_amount
  , tard.record_fee_amount
  , tard.record_deposit_recv_amount
  , tard.record_deposit_payment_amount
  , tard.remarks
  , tardop.accg_record_detail_over_payment_seq
  , tardop.over_payment_amount
  , tardop.over_payment_refund_flg
FROM
  t_accg_record_detail tard 
  LEFT JOIN t_accg_record_detail_over_payment tardop 
    ON tard.accg_record_detail_seq = tardop.accg_record_detail_seq 
WHERE
  tard.accg_record_seq = /* accgRecordSeq */NULL
;
