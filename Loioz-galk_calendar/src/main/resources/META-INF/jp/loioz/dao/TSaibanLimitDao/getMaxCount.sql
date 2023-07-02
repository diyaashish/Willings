SELECT
  tsl.limit_date_count
FROM
  t_saiban_limit tsl 
  INNER JOIN t_saiban_limit_relation tslr 
    USING (saiban_limit_seq) 
WHERE
  tslr.saiban_seq = /* saibanSeq */1
ORDER BY
  saiban_limit_seq DESC
LIMIT 1;