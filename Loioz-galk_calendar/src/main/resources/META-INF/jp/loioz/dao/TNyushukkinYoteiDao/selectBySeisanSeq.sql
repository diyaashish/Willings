SELECT
  *
FROM
  t_nyushukkin_yotei tny 
WHERE
  tny.seisan_seq = /* seisanSeq */NULL
ORDER BY
  nyushukkin_yotei_seq;