SELECT
  taai.* 
FROM
  t_anken_azukari_item taai 
WHERE
  azukari_from_kanyosha_seq IN ( 
    SELECT
      kanyosha_seq 
    FROM
      t_kanyosha 
    WHERE
      person_id = /* personId */1
  ) 
;
