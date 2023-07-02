SELECT
  tadfd.* 
FROM
  t_accg_doc_file tadf 
  INNER JOIN t_accg_doc_file_detail tadfd 
    ON tadf.accg_doc_file_seq = tadfd.accg_doc_file_seq 
WHERE
  tadf.accg_doc_seq = /* accgDocSeq */NULL
;
