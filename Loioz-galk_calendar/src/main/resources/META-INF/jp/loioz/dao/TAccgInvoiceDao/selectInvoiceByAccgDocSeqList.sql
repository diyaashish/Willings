SELECT 
  *
FROM
  t_accg_invoice tai
WHERE
  tai.accg_doc_seq IN /* accgDocSeqList */(NULL)
;
