SELECT
   tc.customer_type
  , CONCAT(COALESCE(tc.customer_name_sei,''),COALESCE(tc.customer_name_mei,'')) AS kanyosha_name
  , tca.daihyo_name
  , tk.person_id
  , tc.address1
  , tc.address2
  , tc.koza_name
  , tc.ginko_name
  , tc.shiten_name
  , tc.shiten_no
  , tc.koza_type
  , tc.koza_no
  , tc.zip_code
FROM
  t_kanyosha AS tk
  INNER JOIN t_person tc
    ON tk.person_id = tc.person_id
  LEFT JOIN t_person_add_hojin tca
    ON tk.person_id = tca.person_id
WHERE
  tk.kanyosha_seq = /* kanyoshaSeq */null
;

