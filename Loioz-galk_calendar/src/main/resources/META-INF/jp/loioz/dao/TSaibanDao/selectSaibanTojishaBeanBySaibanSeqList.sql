SELECT
  * 
FROM
  ( 
    /** 顧客当事者の取得 */
    SELECT
      tsc.saiban_seq AS saiban_seq
      , tc.customer_name_sei AS name_sei
      , tc.customer_name_mei AS name_mei
      , tc.customer_name_sei_kana AS name_sei_kana
      , tc.customer_name_mei AS name_mei_kana
      , NULL AS kanyosha_type
      , tsc.saiban_tojisha_hyoki AS tojisha_hyoki
      , tsc.main_flg AS main_flg
      , NULL AS dairi_flg
      , tsc.created_at AS created_at
    FROM
      t_saiban_customer tsc 
      LEFT JOIN t_person tc 
        ON tsc.customer_id = tc.customer_id 
    UNION ALL 
    /** その他当事者の取得 */
    SELECT
      tsrk.saiban_seq AS saiban_seq
      , tc.customer_name_sei AS name_sei
      , tc.customer_name_mei AS name_mei
      , tc.customer_name_sei_kana AS name_sei_kana
      , tc.customer_name_mei_kana AS name_mei_kana
      , tsrk.kanyosha_type AS kanyosha_type
      , tsrk.saiban_tojisha_hyoki AS tojisha_hyoki
      , tsrk.main_flg AS main_flg
      , tsrk.dairi_flg AS dairi_flg
      , tsrk.created_at AS created_at
    FROM
      t_saiban_related_kanyosha tsrk 
      LEFT JOIN t_kanyosha tk 
        ON tsrk.kanyosha_seq = tk.kanyosha_seq 
      LEFT JOIN t_person tc 
        ON tk.person_id = tc.person_id
  ) AS tojisha 
WHERE
  tojisha.saiban_seq IN /* saibanSeqList */(NULL) 
ORDER BY
  main_flg DESC
  , created_at;
