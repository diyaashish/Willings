SELECT
  tark.anken_id
  , tark.kanyosha_seq AS kanyosha_seq
  , tark.kanyosha_type AS kanyosha_type
  , tark.dairi_flg AS dairi_flg
  , tp_main.person_id AS person_id
  , tp_main.customer_name_sei AS customer_name_sei
  , tp_main.customer_name_sei_kana AS customer_name_sei_kana
  , tp_main.customer_name_mei AS customer_name_mei
  , tp_main.customer_name_mei_kana AS customer_name_mei_kana
  /** 代理人情報 */
  , tark_sub.kanyosha_seq AS alternate_kanyosha_seq
  , tark_sub.kanyosha_type AS alternate_kanyosha_type
  , tark_sub.dairi_flg AS alternate_dairi_flg
  , tp_sub.person_id AS alternate_person_id
  , tp_sub.customer_name_sei AS alternate_customer_name_sei
  , tp_sub.customer_name_sei_kana AS alternate_customer_name_sei_kana
  , tp_sub.customer_name_mei AS alternate_customer_name_mei
  , tp_sub.customer_name_mei_kana AS alternate_customer_name_mei_kana
FROM
  t_anken_related_kanyosha tark
  LEFT JOIN t_kanyosha tk_main
    ON tark.kanyosha_seq = tk_main.kanyosha_seq
  LEFT JOIN t_person tp_main
    ON tk_main.person_id = tp_main.person_id
  /** 代理人情報と自己結合 */
  LEFT JOIN t_anken_related_kanyosha tark_sub
    ON tark.related_kanyosha_seq = tark_sub.kanyosha_seq
    AND tark.anken_id = tark_sub.anken_id
  LEFT JOIN t_kanyosha tk_sub
    ON tark_sub.kanyosha_seq = tk_sub.kanyosha_seq
  LEFT JOIN t_person tp_sub
    ON tk_sub.person_id = tp_sub.person_id
WHERE
  tark.anken_id IN /* ankenIdList */(NULL)
ORDER BY 
  tark.anken_id, tark.kanyosha_seq
;