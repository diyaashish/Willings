SELECT
  ta.anken_id
  , ta.anken_type
  , ta.bunya_id
  , taak.lawyer_select_type 
FROM
  t_anken ta 
  INNER JOIN t_saiban ts 
    USING (anken_id) 
  INNER JOIN t_anken_add_keiji taak 
    USING (anken_id) 
WHERE
  ts.saiban_seq = /* saibanSeq */1;
