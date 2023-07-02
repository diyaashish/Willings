SELECT
  tk.kanyosha_seq
  , tk.person_id
  , tk.anken_id
  , tark.related_kanyosha_seq
  , tark.kanyosha_type
  , tk.disp_order
  , tark.dairi_flg
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS kanyosha_name
  , tark.created_at
FROM
  t_kanyosha tk 
  LEFT JOIN t_anken_related_kanyosha tark 
    USING (kanyosha_seq) 
  LEFT JOIN t_person tc 
    ON tk.person_id = tc.person_id
WHERE
  tk.anken_id IN /* ankenIdList */(NULL)
;