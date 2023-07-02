SELECT
  tsrk.saiban_seq
  , tsrk.kanyosha_seq
  , tsrk.related_kanyosha_seq
  , tsrk.kanyosha_type
  , tsrk.saiban_tojisha_hyoki
  , tsrk.main_flg
  , tsrk.dairi_flg
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
  t_saiban_related_kanyosha tsrk 
  INNER JOIN t_kanyosha tk 
    ON tsrk.kanyosha_seq = tk.kanyosha_seq 
  LEFT JOIN t_person tc 
    USING (person_id) 
WHERE
  tsrk.saiban_seq = /* saibanSeq */NULL 
  AND tsrk.dairi_flg = /* dairiFlg */'0' 
  AND tsrk.kanyosha_type = /* kanyoshaType */'1' 
ORDER BY
  tk.person_id;
