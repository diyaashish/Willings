SELECT
  tst.saiban_tree_seq
  , tst.saiban_seq
  , tst.connect_type
  , tsj.jiken_seq
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no
  , tsj.jiken_name 
FROM
  t_saiban_tree tst 
  left join t_saiban_jiken tsj 
    on tst.saiban_seq = tsj.saiban_seq 
WHERE
  tst.parent_seq = /* saibanSeq */1 
ORDER BY
  CAST(COALESCE(tsj.jiken_gengo, 0) AS SIGNED) DESC
  , CAST(COALESCE(tsj.jiken_year, 0) AS SIGNED)
  , CAST(COALESCE(tsj.jiken_no, 0) AS SIGNED)
  , tst.saiban_seq
;
