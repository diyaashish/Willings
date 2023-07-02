SELECT
  tp.person_id
  , tac.customer_id
  , tp.customer_type
  , tp.customer_flg
  , tp.advisor_flg
  , tp.customer_type
  , tp.customer_created_date
  , tp.customer_name_sei
  , tp.customer_name_sei_kana
  , tp.customer_name_mei
  , tp.customer_name_mei_kana
  , ta.anken_id
  , ta.anken_name
  , ta.anken_type
  , ta.bunya_id
  , tac.anken_status 
FROM
  t_accg_doc tad 
  INNER JOIN t_anken_customer tac 
    ON tad.anken_id = tac.anken_id 
    AND tad.person_id = tac.customer_id 
  INNER JOIN t_anken ta 
    ON tac.anken_id = ta.anken_id 
  INNER JOIN t_person tp 
    ON tac.customer_id = tp.person_id 
WHERE
  tad.accg_doc_seq = /* accgDocSeq */NULL
;
