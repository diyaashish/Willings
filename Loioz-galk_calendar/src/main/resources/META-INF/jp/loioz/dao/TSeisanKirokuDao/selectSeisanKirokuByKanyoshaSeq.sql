SELECT
  * 
FROM
  t_seisan_kiroku
WHERE
  1 = 1
  AND ( 
    seikyu_kanyosha_seq = /* kanyoshaSeq */null 
    OR henkin_kanyosha_seq = /* kanyoshaSeq */null
  )
  AND deleted_at IS NULL 
  AND deleted_by IS NULL
;