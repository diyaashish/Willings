SELECT
  * 
FROM
  t_saiban_limit_relation tslr 
  INNER JOIN t_saiban_limit tsl 
    ON tslr.saiban_limit_seq = tsl.saiban_limit_seq 
WHERE
  tslr.saiban_seq = /* saibanSeq */NULL 
  AND tsl.limit_date_count > /* count */NULL
;