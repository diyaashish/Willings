SELECT
  * 
FROM
  ( 
    SELECT
      tsrk.saiban_seq AS saiban_seq
      , tsrk.kanyosha_seq AS kanyosha_seq
      , NULL AS customer_id
	  , tsrk.related_kanyosha_seq AS related_kanyosha_seq
      , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
      , tsrk.kanyosha_type AS kanyosha_type
      , tsrk.saiban_tojisha_hyoki AS tojisha_hyoki
      , tsrk.main_flg AS main_flg
      , tsrk.dairi_flg AS dairi_flg 
    FROM
      t_saiban_related_kanyosha tsrk 
      LEFT JOIN t_kanyosha tk
        ON tk.kanyosha_seq = tsrk.kanyosha_seq
      LEFT JOIN t_person tc
        ON tk.person_id = tc.person_id
    WHERE
      tsrk.saiban_seq = /* saibanSeq */null
    UNION ALL 
    SELECT
      tsc.saiban_seq AS saiban_seq
      , NULL AS kanyosha_seq
      , tsc.customer_id AS customerId
	  , NULL AS related_kanyosha_seq
      , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS name
      , NULL AS kanyosha_type
      , tsc.saiban_tojisha_hyoki AS tojisha_hyoki
      , tsc.main_flg AS main_flg
      , NULL AS dairi_flg 
    FROM
      t_saiban_customer tsc 
      INNER JOIN t_person tc 
        USING (customer_id) 
    WHERE
      tsc.saiban_seq = /* saibanSeq */null
  ) AS list 
ORDER BY
  list.saiban_seq;
