SELECT
  tad.* 
FROM
  t_accg_doc tad 
  INNER JOIN t_accg_statement tas 
    ON tad.accg_doc_seq = tas.accg_doc_seq 
WHERE
  tad.anken_id = /* ankenId */NULL 
  AND tas.refund_to_person_id = /* refundToPersonId */NULL
;
