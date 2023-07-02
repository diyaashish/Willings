SELECT
/*%if countFlg==true */
  -- 件数の取得
  COUNT(*) count 
/*%end */
/*%if countFlg==false */
  -- データの取得
  ts.saiban_seq
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , CASE 
    WHEN ts.saibansho_id IS NULL 
      THEN ts.saibansho_name 
    ELSE msaibansho.saibansho_name 
    END AS saibansho_name_mei
  , tsj.jiken_seq
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no
  , tsj.jiken_name
  , ts.saiban_status
  , ts.saiban_start_date
  , ts.saiban_end_date
/*%end */
FROM
  t_saiban ts 
  INNER JOIN t_anken ta 
    ON ta.anken_id = ts.anken_id 
  INNER JOIN t_saiban_tree tst 
    ON ts.saiban_seq = tst.saiban_seq 
  LEFT JOIN t_saiban_jiken tsj 
    ON ts.saiban_seq = tsj.saiban_seq 
  LEFT JOIN m_saibansho msaibansho 
    ON msaibansho.saibansho_id = ts.saibansho_id 
  
  -- ソート条件が名前（当事者）の場合、テーブルを結合
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@CUSTOMER_NAME_KANA.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  -- １人分の裁判顧客名（かな）。優先順位：筆頭顧客、顧客の順。
  LEFT JOIN ( 
    SELECT
      tsc.saiban_seq
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.customer_name_sei_kana ORDER BY tsc.main_flg DESC, tc.person_id ASC SEPARATOR ','), ',', 1) customer_name_sei_kana
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.customer_name_mei_kana ORDER BY tsc.main_flg DESC, tc.person_id ASC SEPARATOR ','), ',', 1) customer_name_mei_kana
    FROM
      t_saiban_customer tsc
      INNER JOIN t_person tc
        ON tsc.customer_id = tc.customer_id
    GROUP BY
      tsc.saiban_seq
    ) tsc 
    ON ts.saiban_seq = tsc.saiban_seq 
  /*%end */
  
  -- ソート条件が相手方の場合、テーブルを結合
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@AITEGATA_NAME_KANA.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  -- １人分の裁判相手方名（かな）。優先順位：筆頭相手方、相手方の順。
  LEFT JOIN ( 
    SELECT
      tsrk.saiban_seq
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.customer_name_sei_kana ORDER BY tsrk.main_flg DESC, tc.person_id ASC SEPARATOR ','), ',', 1) aitegata_name_sei_kana
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.customer_name_mei_kana ORDER BY tsrk.main_flg DESC, tc.person_id ASC SEPARATOR ','), ',', 1) aitegata_name_mei_kana
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
  /*%end */
   
  -- 検索条件に担当弁護士が有る場合、テーブルを結合
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.tantoLaywerId) */
  INNER JOIN ( 
    SELECT
      * 
    FROM
      t_saiban_tanto 
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /*saibanListSearchCondition.tantoLaywerId*/'1'
  ) tat_lawyer 
    ON tat_lawyer.saiban_seq = ts.saiban_seq 
  /*%end */
  
  -- 検索条件に担当事務が有る場合、テーブルを結合
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.tantoJimuId) */
  INNER JOIN ( 
    SELECT
      * 
    FROM
      t_saiban_tanto 
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /*saibanListSearchCondition.tantoJimuId*/'1'
  ) tat_jimu 
    ON tat_jimu.saiban_seq = ts.saiban_seq 
  /*%end */
WHERE
  tst.parent_seq IS NULL
  
  -- 検索条件の検索ワードでメイン裁判、子裁判を対象にデータを検索
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.searchWord) */
  AND EXISTS ( 
    SELECT
      *
    FROM (
      SELECT
        CASE 
          WHEN tst.parent_seq IS NULL 
            THEN ts_n.saiban_seq 
          ELSE tst.parent_seq 
          END AS saiban_seq 
      FROM
        t_saiban ts_n 
        INNER JOIN t_anken ta 
          ON ta.anken_id = ts_n.anken_id 
        INNER JOIN t_saiban_tree tst 
          ON ts_n.saiban_seq = tst.saiban_seq 
        LEFT JOIN t_saiban_jiken tsj 
          ON ts_n.saiban_seq = tsj.saiban_seq 
        LEFT JOIN m_saibansho msaibansho 
          ON msaibansho.saibansho_id = ts_n.saibansho_id 
        LEFT JOIN t_saiban_customer tsc 
          ON ts_n.saiban_seq = tsc.saiban_seq 
        LEFT JOIN t_person tc 
          ON tc.customer_id = tsc.customer_id 
        LEFT JOIN t_person_add_kojin tcak
          ON tc.person_id = tcak.person_id 
          AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
        LEFT JOIN ( 
          SELECT
            saiban_seq
            , kanyosha_seq 
          FROM
            t_saiban_related_kanyosha 
          WHERE
            dairi_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' 
            AND kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2' 
        ) tsrk 
          ON tsrk.saiban_seq = ts_n.saiban_seq 
        LEFT JOIN ( 
          SELECT
            tk.kanyosha_seq
            , tc.customer_id
            , tc.customer_name_sei
            , tc.customer_name_mei
            , tc.customer_name_sei_kana
            , tc.customer_name_mei_kana
            , tcak.old_name AS kanyosha_old_name
            , tcak.old_name_kana AS kanyosha_old_name_kana
            , tcak.yago AS kanyosha_yago
            , tcak.yago_kana AS kanyosha_yago_kana
          FROM
            t_kanyosha tk 
            LEFT JOIN t_person tc 
              ON tk.person_id = tc.person_id 
            LEFT JOIN t_person_add_kojin tcak
              ON tc.person_id = tcak.person_id 
              AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
        ) tkc 
          ON tsrk.kanyosha_seq = tkc.kanyosha_seq 
      WHERE
        1 = 1
        -- 検索ワードが有る場合、裁判所、事件番号、事件名、相手方、顧客名、顧客名（かな）に部分一致する条件を追加
        AND ( 
          -- 裁判所
          (
            CASE 
              WHEN ts_n.saibansho_id IS NULL 
                THEN REPLACE (ts_n.saibansho_name, '　', '') 
              ELSE REPLACE (msaibansho.saibansho_name, '　', '') 
            END COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR CASE 
              WHEN ts_n.saibansho_id IS NULL 
                THEN REPLACE (ts_n.saibansho_name, ' ', '') 
              ELSE REPLACE (msaibansho.saibansho_name, ' ', '') 
            END COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          )
        
          -- 事件番号
          OR CONCAT( 
            CASE 
              WHEN tsj.jiken_year 
                THEN CONCAT( 
                CASE tsj.jiken_gengo 
                  -- 元号が増えた場合は修正が必要
                  WHEN 5 THEN '令和' 
                  WHEN 4 THEN '平成' 
                  WHEN 3 THEN '昭和' 
                  ELSE '' 
                  END
                , CASE tsj.jiken_year
                  WHEN 1 THEN '元'
                  ELSE tsj.jiken_year
                  END
                , '年'
              ) 
              ELSE '' 
              END
            , CASE 
              WHEN tsj.jiken_mark IS NULL 
                THEN '' 
              WHEN tsj.jiken_mark = '' 
                THEN '' 
              ELSE CONCAT('（', tsj.jiken_mark, '）') 
              END
            , CASE 
              WHEN tsj.jiken_no 
                THEN CONCAT('第', tsj.jiken_no, '号') 
              ELSE '' 
              END
          ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          
          -- 事件名
          OR (
            REPLACE ( 
              tsj.jiken_name 
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              tsj.jiken_name 
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 相手方の名前
          OR (
            REPLACE ( 
              CONCAT( 
                COALESCE(tkc.customer_name_sei, '')
                , COALESCE(tkc.customer_name_mei, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              CONCAT( 
                COALESCE(tkc.customer_name_sei, '')
                , COALESCE(tkc.customer_name_mei, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 相手方の名前（かな）
          OR (
            REPLACE ( 
              CONCAT( 
                COALESCE(tkc.customer_name_sei_kana, '')
                , COALESCE(tkc.customer_name_mei_kana, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              CONCAT( 
                COALESCE(tkc.customer_name_sei_kana, '')
                , COALESCE(tkc.customer_name_mei_kana, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 相手方の旧姓 + 名
          OR (
            REPLACE ( 
              CONCAT( 
                COALESCE(tkc.kanyosha_old_name, '')
                , COALESCE(tkc.customer_name_mei, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              CONCAT( 
                COALESCE(tkc.kanyosha_old_name, '')
                , COALESCE(tkc.customer_name_mei, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 相手方の旧姓（かな） + 名（かな）
          OR (
            REPLACE ( 
              CONCAT( 
                COALESCE(tkc.kanyosha_old_name_kana, '')
                , COALESCE(tkc.customer_name_mei_kana, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              CONCAT( 
                COALESCE(tkc.kanyosha_old_name_kana, '')
                , COALESCE(tkc.customer_name_mei_kana, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 相手方の屋号／通称名
          OR (
            REPLACE (
              tkc.kanyosha_yago
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE (
              tkc.kanyosha_yago
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          )
          
          -- 相手方の屋号／通称名（かな）
          OR (
            REPLACE (
              tkc.kanyosha_yago_kana
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE (
              tkc.kanyosha_yago_kana
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 顧客名
          OR (
            REPLACE (
              CONCAT( 
                COALESCE(tc.customer_name_sei, '')
                , COALESCE(tc.customer_name_mei, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE (
              CONCAT( 
                COALESCE(tc.customer_name_sei, '')
                , COALESCE(tc.customer_name_mei, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          )  
          
          -- 顧客名（かな）
          OR (
            REPLACE (
              CONCAT( 
                COALESCE(tc.customer_name_sei_kana, '')
                , COALESCE(tc.customer_name_mei_kana, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE (
              CONCAT( 
                COALESCE(tc.customer_name_sei_kana, '')
                , COALESCE(tc.customer_name_mei_kana, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 顧客旧姓 
          OR (
            REPLACE (
              CONCAT( 
                COALESCE(tcak.old_name, '')
                , COALESCE(tc.customer_name_mei, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE (
              CONCAT( 
                COALESCE(tcak.old_name, '')
                , COALESCE(tc.customer_name_mei, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 顧客旧姓（かな）
          OR (
            REPLACE ( 
              CONCAT( 
                COALESCE(tcak.old_name_kana, '')
                , COALESCE(tc.customer_name_mei_kana, '')
              )
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              CONCAT( 
                COALESCE(tcak.old_name_kana, '')
                , COALESCE(tc.customer_name_mei_kana, '')
              )
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          ) 
          
          -- 顧客屋号／通称名
          OR (
            REPLACE ( 
              tcak.yago
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              tcak.yago
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          )
          
          -- 顧客屋号／通称名（かな）
          OR (
            REPLACE ( 
              tcak.yago_kana
              , '　'
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            OR REPLACE ( 
              tcak.yago_kana
              , ' '
              , ''
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
          )
        ) 
    ) AS ex_t
    WHERE ex_t.saiban_seq = ts.saiban_seq
  ) 
  /*%end */
  
  -- 裁判手続きがある場合、裁判手続きが該当する条件を追加
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanStatusList) */
  AND ts.saiban_status IN /*saibanListSearchCondition.saibanStatusList*/(1) 
  /*%end*/
  
  -- 分野が有る場合、分野が該当する条件を追加
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.bunyaId) */
  AND ta.bunya_id = /*saibanListSearchCondition.bunyaId*/'1' 
  /*%end */
  
  -- 申立日が有る場合、申立日が該当する条件を追加
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanStartDateFrom) */
  AND ts.saiban_start_date >= /*saibanListSearchCondition.saibanStartDateFrom*/'2022/02/22' 
  /*%end */
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanStartDateTo) */
  AND ts.saiban_start_date <= /*saibanListSearchCondition.saibanStartDateTo*/'2022/02/22' 
  /*%end */
  
  -- 終了日が有る場合、終了日が該当する条件を追加
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanEndDateFrom) */
  AND ts.saiban_end_date >= /*saibanListSearchCondition.saibanEndDateFrom*/'2022/02/22' 
  /*%end */
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanEndDateTo) */
  AND ts.saiban_end_date <= /*saibanListSearchCondition.saibanEndDateTo*/'2022/02/22' 
  /*%end */
ORDER BY
  1 = 1 
  -- countを取得するときは、ORDER BY を設定しない
  /*%if countFlg==false */
  
  -- 案件IDでのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@ANKEN_ID.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , ta.anken_id /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  /*%end */
  
  -- 分野でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@BUNYA_ID.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , CAST(ta.bunya_id AS SIGNED) /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 裁判所名でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBANSHO_NAME.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , saibansho_name_mei /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 事件番号でのソート（事件年、マーク、番号が未入力のものはNULLデータとして昇順のときは先頭、降順のときは最後に表示する）
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@JIKEN_NO.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , ((tsj.jiken_year = '' OR tsj.jiken_year IS NULL) AND (tsj.jiken_mark = '' OR tsj.jiken_mark IS NULL) AND (tsj.jiken_no = '' OR tsj.jiken_no IS NULL)) /*%if saibanListSortCondition.isDesc() */ ASC /*%end */ /*%if !saibanListSortCondition.isDesc() */ DESC /*%end */
  , CAST(
      CASE 
        WHEN ((tsj.jiken_year = '' OR tsj.jiken_year IS NULL) AND (tsj.jiken_mark = '' OR tsj.jiken_mark IS NULL) AND (tsj.jiken_no = '' OR tsj.jiken_no IS NULL)) THEN NULL
        ELSE tsj.jiken_gengo
      END 
    AS SIGNED) /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , CAST(
      CASE 
        WHEN tsj.jiken_year IS NULL THEN 0
        WHEN tsj.jiken_year = '' THEN 0
        ELSE tsj.jiken_year
      END 
    AS SIGNED) /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , CASE 
      WHEN tsj.jiken_mark IS NULL THEN ''
      ELSE tsj.jiken_mark
    END /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , CAST(
      CASE
        WHEN tsj.jiken_no IS NULL THEN 0
        WHEN tsj.jiken_no = '' THEN 0
        ELSE tsj.jiken_no
      END 
    AS SIGNED) /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 事件名でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@JIKEN_NAME.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , tsj.jiken_name /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 顧客名（かな）でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@CUSTOMER_NAME_KANA.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , tsc.customer_name_sei_kana /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , tsc.customer_name_mei_kana /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 相手方（かな）でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@AITEGATA_NAME_KANA.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , tsrk.aitegata_name_sei_kana /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , tsrk.aitegata_name_mei_kana /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 裁判手続きでのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBAN_STATUS_ID.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , CAST(ts.saiban_status AS SIGNED) /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 申立日でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBAN_START_DATE.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , ts.saiban_start_date /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 終了日でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBAN_END_DATE.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , ts.saiban_end_date /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  , ts.saiban_seq DESC 
  /*%end */
;
