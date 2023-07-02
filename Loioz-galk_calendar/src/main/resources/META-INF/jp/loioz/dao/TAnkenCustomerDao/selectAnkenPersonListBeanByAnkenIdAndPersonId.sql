SELECT
  tp.person_id
  , tp.customer_id
  , tp.customer_name_sei
  , tp.customer_name_sei_kana
  , tp.customer_name_mei
  , tp.customer_name_mei_kana
  , tp.customer_flg
  , tp.advisor_flg
  , tp.customer_type
  , tp.customer_created_date
  , ta.anken_id
  , ta.anken_name
  , ta.anken_type
  , ta.bunya_id
  , tac.anken_status
FROM
  t_anken_customer tac 
  INNER JOIN t_anken ta 
    USING (anken_id) 
  INNER JOIN t_person tp 
    ON tac.customer_id = tp.customer_id 
WHERE
  ta.anken_id = /* ankenId */NULL
  AND tp.person_id = /* personId */NULL
;