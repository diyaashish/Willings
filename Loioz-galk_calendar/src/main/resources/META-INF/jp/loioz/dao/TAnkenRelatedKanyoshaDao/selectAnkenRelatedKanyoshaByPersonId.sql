SELECT
  tark.*
FROM
  t_anken_related_kanyosha tark 
  INNER JOIN t_kanyosha tk 
    ON tark.kanyosha_seq = tk.kanyosha_seq 
WHERE
  tk.person_id = /* personId */'1'
;