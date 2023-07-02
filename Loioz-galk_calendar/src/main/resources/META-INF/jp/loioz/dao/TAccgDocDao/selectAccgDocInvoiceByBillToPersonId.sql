SELECT
  tad.* 
FROM
  t_accg_doc tad 
  INNER JOIN t_accg_invoice tai 
    ON tad.accg_doc_seq = tai.accg_doc_seq 
WHERE
  tad.anken_id = /* ankenId */NULL 
  AND tai.bill_to_person_id = /* billToPersonId */NULL
;
