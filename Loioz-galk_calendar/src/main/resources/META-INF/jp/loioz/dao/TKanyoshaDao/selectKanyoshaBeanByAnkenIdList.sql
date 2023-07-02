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
FROM
  t_kanyosha tk 
  INNER JOIN t_person tc 
    USING (person_id) 
WHERE
  tk.anken_id IN /* ankenIdList */(NULL)
ORDER BY
  tk.anken_id, tk.disp_order
;
