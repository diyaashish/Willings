SELECT
  * 
FROM
  t_nyushukkin_yotei 
WHERE
  1 = 1 
  AND ( 
    nyukin_kanyosha_seq = /* kanyoshaSeq */NULL 
    OR shukkin_kanyosha_seq = /* kanyoshaSeq */NULL
  )
;
