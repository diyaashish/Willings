SELECT
  ta.anken_id
  , ta.anken_name
  , ta.bunya_id 
FROM
  t_anken AS ta 
WHERE
  anken_id = ( 
    SELECT
      tkk.pool_saki_anken_id 
    FROM
      t_kaikei_kiroku AS tkk 
    WHERE
      tkk.seisan_seq = /* seisanSeq */NULL
      AND tkk.pool_type = 1
    LIMIT
      1
  );