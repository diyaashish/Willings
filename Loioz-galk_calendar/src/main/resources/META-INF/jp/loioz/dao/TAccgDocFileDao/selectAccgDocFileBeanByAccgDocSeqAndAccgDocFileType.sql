SELECT
  tadf.accg_doc_file_seq
  , tadf.accg_doc_seq
  , tadf.accg_doc_file_type
  , tadf.file_extension
  , tadf.recreate_standby_flg
  , tadfd.accg_doc_file_detail_seq
  , tadfd.file_branch_no
  , tadfd.s3_object_key
  , tadasf.accg_doc_act_send_file_seq
FROM
  t_accg_doc_file tadf 
  LEFT JOIN t_accg_doc_file_detail tadfd 
    ON tadf.accg_doc_file_seq = tadfd.accg_doc_file_seq 
  LEFT JOIN t_accg_doc_act_send_file tadasf
    ON tadf.accg_doc_file_seq = tadasf.accg_doc_file_seq
WHERE
  tadf.accg_doc_seq = /* accgDocSeq */NULL
  AND tadf.accg_doc_file_type = /* accgDocFileType.cd */NULL
;
