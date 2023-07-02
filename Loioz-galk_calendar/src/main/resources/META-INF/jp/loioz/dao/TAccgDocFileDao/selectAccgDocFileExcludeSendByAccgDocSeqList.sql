SELECT
  tadf.* 
FROM
  t_accg_doc_file tadf 
  LEFT JOIN t_accg_doc_act_send_file tadasf 
    ON tadf.accg_doc_file_seq = tadasf.accg_doc_file_seq 
WHERE
  tadasf.accg_doc_act_send_file_seq IS NULL
  AND tadf.accg_doc_seq IN /* accgDocSeqList */(NULL)
;