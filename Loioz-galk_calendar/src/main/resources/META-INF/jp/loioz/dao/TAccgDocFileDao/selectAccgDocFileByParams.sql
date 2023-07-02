SELECT
  *
FROM
  t_accg_doc_file tadf
WHERE
  tadf.accg_doc_seq IN /* accgDocSeqList */(1)
  AND tadf.accg_doc_file_type = /* accgDocFileTypeCd */'1'
;
