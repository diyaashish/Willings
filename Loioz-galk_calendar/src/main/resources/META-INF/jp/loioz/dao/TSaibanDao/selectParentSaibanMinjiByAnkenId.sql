SELECT
  ta.anken_id
  , ta.bunya_id
  , ts.saiban_seq
  , ts.saiban_branch_no
  , tsj.jiken_name
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no
  , tst.saiban_tree_seq
  , tst.parent_seq
  , tst.connect_type
  , tst.honso_flg
  , tst.kihon_flg 
FROM
  t_anken ta 
  INNER JOIN t_saiban ts 
    ON ta.anken_id = ts.anken_id 
  LEFT JOIN t_saiban_tree tst 
    ON ts.saiban_seq = tst.saiban_seq 
  LEFT JOIN t_saiban_jiken tsj 
    ON ts.saiban_seq = tsj.saiban_seq 
WHERE
  ta.anken_id = /* ankenId */1
  AND tst.parent_seq IS NULL
ORDER BY
  ts.saiban_branch_no
  , ts.created_at asc
;
