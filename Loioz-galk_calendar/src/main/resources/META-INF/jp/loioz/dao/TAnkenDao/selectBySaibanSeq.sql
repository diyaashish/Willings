SELECT
  * 
FROM
  t_anken ta
  INNER JOIN t_saiban ts 
    USING (anken_id) 
WHERE
   ts.saiban_seq = /* saibanSeq */1;
