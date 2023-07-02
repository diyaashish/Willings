SELECT
  * 
FROM
  t_kaikei_kiroku 
WHERE
  seisan_seq = /* seisanSeq */NULL 
  AND pool_type IS NOT NULL;
