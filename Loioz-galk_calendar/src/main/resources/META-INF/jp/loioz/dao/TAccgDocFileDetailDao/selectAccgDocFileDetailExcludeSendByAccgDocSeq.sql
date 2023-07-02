SELECT
  tadfd.* 
FROM
  t_accg_doc_file tadf 
  INNER JOIN t_accg_doc_file_detail tadfd 
    ON tadf.accg_doc_file_seq = tadfd.accg_doc_file_seq 
  LEFT JOIN t_accg_doc_act_send_file tadasf 
    ON tadf.accg_doc_file_seq = tadasf.accg_doc_file_seq 
WHERE
  tadasf.accg_doc_act_send_file_seq IS NULL 
  AND tadf.accg_doc_seq = /* accgDocSeq */NULL
;