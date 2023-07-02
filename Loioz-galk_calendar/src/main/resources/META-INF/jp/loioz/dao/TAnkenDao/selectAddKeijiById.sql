SELECT
  ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , ta.anken_created_date
  , ta.anken_type
  , ta.jian_summary
  , taak.lawyer_select_type 
FROM
  t_anken ta 
  LEFT JOIN t_anken_add_keiji taak 
    USING (anken_id) 
WHERE
  anken_id = /* ankenId */1;
