SELECT
  ta.anken_id
  -- 相手方の氏名を1人分取得
  , ( 
    SELECT
      CONCAT( 
        COALESCE(tkc.kanyosha_name_sei, '')
        , COALESCE(tkc.kanyosha_name_mei, '')
      ) 
    FROM
      t_anken ta_n 
      LEFT JOIN ( 
        -- 案件IDごとに最初に作成した相手方を取得する
        SELECT
          anken_id
          , kanyosha_seq
        FROM (
          SELECT
            anken_id
            , kanyosha_seq
            -- 案件IDごとにcreated_atの昇順で番号をふる
            , ROW_NUMBER() OVER (PARTITION BY anken_id ORDER BY created_at ASC) AS sort_order
          FROM 
            t_anken_related_kanyosha
          WHERE
            dairi_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' 
            AND kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2' 
        ) AS ranked
        WHERE
          -- ROW_NUMBERで、ふった番号の1だけを対象
          sort_order = 1
      ) tark 
        ON tark.anken_id = ta_n.anken_id 
      LEFT JOIN ( 
        SELECT
          tk.kanyosha_seq
          , tc.customer_id
          , tc.customer_name_sei AS kanyosha_name_sei
          , tc.customer_name_mei AS kanyosha_name_mei
          , tc.customer_name_sei_kana AS kanyosha_name_sei_kana
          , tc.customer_name_mei_kana AS kanyosha_name_mei_kana
        FROM
          t_kanyosha tk 
          LEFT JOIN t_person tc
            on tc.person_id = tk.person_id
      ) tkc 
        ON tark.kanyosha_seq = tkc.kanyosha_seq 
    WHERE
      ta_n.anken_id = ta.anken_id
  ) AS aitegata_name
  -- 相手方の合計人数を取得
  , ( 
    SELECT
      COUNT(*)
    FROM
      t_anken_related_kanyosha tark 
      LEFT JOIN t_kanyosha tk 
        ON tark.kanyosha_seq = tk.kanyosha_seq 
    WHERE
      tark.anken_id = ta.anken_id 
      AND tark.dairi_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' 
      AND tark.kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2'
  ) AS number_of_aitegata
FROM
  t_anken ta
WHERE
  ta.anken_id IN /* ankenIdList */(1) 
; 