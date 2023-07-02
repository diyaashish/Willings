SELECT
  ta.anken_id
  , ta.bunya_id
  , ts.saiban_seq
  , tsj.jiken_seq
  , tsj.jiken_name
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no 
FROM
  t_anken ta 
  INNER JOIN t_saiban ts 
    USING (anken_id) 
  LEFT JOIN t_saiban_jiken tsj 
    USING (saiban_seq) 
  INNER JOIN t_saiban_add_keiji tsak 
    ON tsj.jiken_seq = tsak.main_jiken_seq 
WHERE
  ta.anken_id = /* ankenId */1
ORDER BY
 ts.saiban_branch_no;
