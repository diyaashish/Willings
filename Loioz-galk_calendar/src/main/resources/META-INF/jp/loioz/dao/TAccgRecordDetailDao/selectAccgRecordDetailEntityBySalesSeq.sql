SELECT
  tard.* 
FROM
  t_sales ts 
  INNER JOIN t_accg_doc tad 
    ON ts.anken_id = tad.anken_id 
    AND ts.person_id = tad.person_id 
  INNER JOIN t_accg_record tar 
    ON tad.accg_doc_seq = tar.accg_doc_seq 
  INNER JOIN t_accg_record_detail tard 
    ON tar.accg_record_seq = tard.accg_record_seq 
WHERE
  ts.sales_seq = /* salesSeq */NULL
;
