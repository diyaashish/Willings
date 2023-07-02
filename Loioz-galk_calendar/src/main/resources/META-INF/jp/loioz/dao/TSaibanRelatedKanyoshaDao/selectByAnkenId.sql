SELECT
  tsrk.* 
FROM
  t_saiban ts 
  INNER JOIN t_saiban_related_kanyosha tsrk 
    ON ts.saiban_seq = tsrk.saiban_seq 
WHERE
  ts.anken_id = /* ankenId */NULL;