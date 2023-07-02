SELECT
  * 
FROM
  t_seisan_kiroku 
WHERE
  ( 
    seikyu_kanyosha_seq IN ( 
      SELECT
        kanyosha_seq 
      FROM
        t_kanyosha 
      WHERE
        person_id = /* personId */1
    ) 
    OR henkin_kanyosha_seq IN ( 
      SELECT
        kanyosha_seq 
      FROM
        t_kanyosha 
      WHERE
        person_id = /* personId */1
    )
  ) 
  AND deleted_at IS NULL 
  AND deleted_by IS NULL
;
