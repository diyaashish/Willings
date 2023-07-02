SELECT
  tsrk.saiban_seq
  , tsrk.kanyosha_seq AS kanyosha_seq
  , tsrk.kanyosha_type AS kanyosha_type
  , tsrk.saiban_tojisha_hyoki AS saiban_tojisha_hyoki
  , tsrk.main_flg AS main_flg
  , tsrk.dairi_flg AS dairi_flg
  , tp_main.person_id AS person_id
  , tp_main.customer_name_sei AS customer_name_sei
  , tp_main.customer_name_sei_kana AS customer_name_sei_kana
  , tp_main.customer_name_mei AS customer_name_mei
  , tp_main.customer_name_mei_kana AS customer_name_mei_kana
  /** 代理人情報 */
  , tsrk_sub.kanyosha_seq AS alternate_kanyosha_seq
  , tsrk_sub.kanyosha_type AS alternate_kanyosha_type
  , tsrk_sub.saiban_tojisha_hyoki AS alternate_saiban_tojisha_hyoki
  , tsrk_sub.main_flg AS alternate_main_flg
  , tsrk_sub.dairi_flg AS alternate_dairi_flg
  , tp_sub.person_id AS alternate_person_id
  , tp_sub.customer_name_sei AS alternate_customer_name_sei
  , tp_sub.customer_name_sei_kana AS alternate_customer_name_sei_kana
  , tp_sub.customer_name_mei AS alternate_customer_name_mei
  , tp_sub.customer_name_mei_kana AS alternate_customer_name_mei_kana
FROM
  t_saiban_related_kanyosha tsrk
  LEFT JOIN t_kanyosha tk_main
    ON tsrk.kanyosha_seq = tk_main.kanyosha_seq
  LEFT JOIN t_person tp_main
    ON tk_main.person_id = tp_main.person_id
  /** 代理人情報と自己結合 */
  LEFT JOIN t_saiban_related_kanyosha tsrk_sub
    ON tsrk.related_kanyosha_seq = tsrk_sub.kanyosha_seq
    AND tsrk.saiban_seq = tsrk_sub.saiban_seq
  LEFT JOIN t_kanyosha tk_sub
    ON tsrk_sub.kanyosha_seq = tk_sub.kanyosha_seq
  LEFT JOIN t_person tp_sub
    ON tk_sub.person_id = tp_sub.person_id
WHERE
  tsrk.saiban_seq IN /* saibanSeqList */(NULL)
ORDER BY 
  tsrk.saiban_seq, tsrk.kanyosha_seq
;