SELECT
  tard.* 
FROM
  t_accg_doc tad 
  INNER JOIN t_accg_record tar 
    ON tad.accg_doc_seq = tar.accg_doc_seq 
  INNER JOIN t_accg_record_detail tard 
    ON tar.accg_record_seq = tard.accg_record_seq 
WHERE
  tad.accg_doc_seq = /* accgDocSeq */NULL
;