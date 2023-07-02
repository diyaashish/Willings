SELECT
  tsl.* 
FROM
  t_saiban_limit tsl 
  INNER JOIN t_saiban_limit_relation tslr 
    USING (saiban_limit_seq) 
WHERE
  tslr.saiban_seq = /* saibanSeq */1;