SELECT
  ts.saiban_seq
  -- 相手方の氏名を1人分取得
  , CONCAT( 
    COALESCE(tc.customer_name_sei, '')
    , COALESCE(tc.customer_name_mei, '')
  ) AS aitegata_name
  -- 相手方の合計人数を取得
  , ( 
    SELECT
      COUNT(*) 
    FROM
      t_saiban_related_kanyosha tsrk 
    WHERE
      tsrk.saiban_seq = ts.saiban_seq 
      AND tsrk.dairi_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' 
      AND tsrk.kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2'
  ) AS number_of_aitegata 
FROM
  t_saiban ts 
  
  -- １人分の裁判相手方名（かな）。優先順位：筆頭相手方、相手方の順。
  LEFT JOIN ( 
    SELECT
      tsrk.saiban_seq
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.person_id ORDER BY tsrk.main_flg DESC, tc.person_id ASC SEPARATOR ','), ',', 1) person_id
    FROM
      t_saiban_related_kanyosha tsrk
      INNER JOIN t_kanyosha tk
        ON tsrk.kanyosha_seq = tk.kanyosha_seq
      INNER JOIN t_person tc
        ON tk.person_id = tc.person_id
    WHERE
      tsrk.dairi_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' 
      AND tsrk.kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2'
    GROUP BY
      tsrk.saiban_seq
    ) tsrk 
    ON ts.saiban_seq = tsrk.saiban_seq 
  LEFT JOIN t_person tc
    ON tc.person_id = tsrk.person_id
WHERE
  ts.saiban_seq IN /* saibanSeqList */(1);
