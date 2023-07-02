SELECT
  tardop.*
FROM
  t_accg_record_detail_over_payment tardop 
  INNER JOIN t_accg_record_detail tard 
    ON tardop.accg_record_detail_seq = tard.accg_record_detail_seq 
WHERE
  tard.accg_record_seq = /* accgRecordSeq */NULL
;