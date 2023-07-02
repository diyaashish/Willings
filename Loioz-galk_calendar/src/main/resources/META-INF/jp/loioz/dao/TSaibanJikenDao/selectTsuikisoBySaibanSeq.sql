SELECT
  tsj.jiken_seq
  , tsj.saiban_seq
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no
  , tsj.jiken_name 
FROM
  t_saiban_jiken tsj 
  INNER JOIN t_saiban_add_keiji tsak 
    ON tsj.saiban_seq = tsak.saiban_seq 
WHERE
  tsj.saiban_seq = /* saibanSeq */1
  AND tsj.jiken_seq != /* mainJikenSeq */1 
ORDER BY
  tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , LENGTH(tsj.jiken_no)
  , tsj.jiken_no
;
