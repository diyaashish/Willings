SELECT
  tk.kanyosha_seq
  , tk.anken_id
  , tk.disp_order
  , tark.dairi_flg
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS aitegata_name
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei_kana,''),' ',COALESCE(tc.customer_name_mei_kana,''))) AS aitegata_name_kana
  , tcak.old_name
  , tcak.old_name_kana
FROM
  t_kanyosha tk 
  LEFT JOIN t_anken_related_kanyosha tark 
    USING (kanyosha_seq) 
  LEFT JOIN t_person tc 
    ON tk.person_id = tc.person_id
  LEFT JOIN t_person_add_kojin tcak
    ON tcak.person_id = tc.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
WHERE
  tk.anken_id IN /* ankenIdList */(NULL) 
  AND tark.kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2';