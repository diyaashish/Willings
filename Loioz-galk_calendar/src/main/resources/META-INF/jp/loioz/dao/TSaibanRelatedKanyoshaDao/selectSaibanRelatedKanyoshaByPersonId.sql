SELECT
  tsrk.*
FROM
  t_saiban_related_kanyosha tsrk 
  INNER JOIN t_kanyosha tk 
    ON tsrk.kanyosha_seq = tk.kanyosha_seq 
WHERE
  tk.person_id = /* personId */1 
;
