SELECT
  tark.anken_id
  , tark.kanyosha_seq
  , tark.related_kanyosha_seq
  , tark.kanyosha_type
  , tark.dairi_flg
  , tc.person_id
  , tc.customer_id
  , tc.customer_name_sei
  , tc.customer_name_mei
  , tc.customer_flg
  , tc.advisor_flg
  , tc.customer_type
  , tk.kankei
  , tk.remarks
FROM
  t_anken_related_kanyosha tark 
  LEFT JOIN t_kanyosha tk 
    ON tark.kanyosha_seq = tk.kanyosha_seq 
  LEFT JOIN t_person tc 
    ON tk.person_id = tc.person_id 
WHERE
  tark.anken_id = /* ankenId */NULL 
  AND tark.dairi_flg = /* dairiFlg */'0' 
  AND tark.kanyosha_type = /* kanyoshaType */'1' 
ORDER BY
  tark.created_at
