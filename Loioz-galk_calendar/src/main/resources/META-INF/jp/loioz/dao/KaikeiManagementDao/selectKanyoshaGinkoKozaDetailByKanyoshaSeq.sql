SELECT
  tc.ginko_name
  , tc.shiten_name
  , tc.shiten_no
  , tc.koza_type
  , tc.koza_no
  , tc.koza_name 
FROM
  t_kanyosha tk  
  INNER JOIN t_person tc 
    USING (person_id) 
WHERE
  tk.kanyosha_seq = /* kanyoshaSeq */NULL
;