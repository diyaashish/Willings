SELECT
  tk.kanyosha_seq
  , tk.person_id
  , tk.anken_id
  , tk.disp_order
  , tk.kankei
  , tk.remarks
  , tc.customer_id
  , tc.customer_type
  , tc.customer_name_sei
  , tc.customer_name_sei_kana
  , tc.customer_name_mei
  , tc.customer_name_mei_kana 
  , tcah.daihyo_name
  , tcah.tanto_name
  , tpal.jimusho_name
  , tpal.busho_name
FROM
  t_kanyosha tk 
  INNER JOIN t_person tc 
    USING (person_id) 
  LEFT JOIN t_person_add_hojin tcah
    ON tk.person_id = tcah.person_id
  LEFT JOIN t_person_add_lawyer tpal
    ON tk.person_id = tpal.person_id
WHERE
  tk.kanyosha_seq = /* kanyoshaSeq */NULL 
; 