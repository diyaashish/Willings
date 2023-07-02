SELECT
  ts.* 
FROM
  t_saiban ts 
  INNER JOIN t_saiban_tree tst 
    ON ts.saiban_seq = tst.saiban_seq 
WHERE
  tst.parent_seq = /* parentSaibanSeq */1;
