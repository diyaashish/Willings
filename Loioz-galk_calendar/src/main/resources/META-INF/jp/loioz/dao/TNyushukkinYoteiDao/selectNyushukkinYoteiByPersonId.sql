SELECT
  * 
FROM
  t_nyushukkin_yotei 
WHERE
  nyukin_kanyosha_seq IN ( 
    SELECT
      kanyosha_seq 
    FROM
      t_kanyosha 
    WHERE
      person_id = /* personId */1
  ) 
  OR shukkin_kanyosha_seq IN ( 
    SELECT
      kanyosha_seq 
    FROM
      t_kanyosha 
    WHERE
      person_id = /* personId */1
  )
;