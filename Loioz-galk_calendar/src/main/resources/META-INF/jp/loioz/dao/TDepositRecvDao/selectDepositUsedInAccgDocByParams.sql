SELECT
  * 
FROM
  t_deposit_recv tdr 
WHERE
  tdr.deposit_type = /* depositType */'' 
  AND tdr.using_accg_doc_seq = /* usingAccgDocSeq */1;
