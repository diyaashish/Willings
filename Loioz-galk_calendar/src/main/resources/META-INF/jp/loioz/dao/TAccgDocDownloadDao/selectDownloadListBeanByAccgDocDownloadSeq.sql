SELECT
  tadd.issue_date
  , tadasf.accg_doc_act_send_file_seq
  , tadasf.download_last_at
  , tadf.accg_doc_file_type
  , tadf.file_extension 
  , tadf.accg_doc_seq
FROM
  t_accg_doc_download tadd 
  INNER JOIN t_accg_doc_act_send_file tadasf 
    ON tadd.accg_doc_act_send_seq = tadasf.accg_doc_act_send_seq 
  INNER JOIN t_accg_doc_file tadf 
    ON tadasf.accg_doc_file_seq = tadf.accg_doc_file_seq 
WHERE
  tadd.accg_doc_download_seq = /* accgDocDownloadSeq */NULL 
ORDER BY
  tadd.accg_doc_download_seq
 ;
