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
  , CASE 
    WHEN tsak.sosakikan_id IS NULL 
      THEN tsak.kensatsucho_name 
    ELSE msosakikan.sosakikan_name 
    END AS kensatsucho_name_mei
  , ts.saiban_status
  , ts.saiban_start_date
  , ts.saiban_end_date 
/*%end */
FROM
  t_saiban_add_keiji tsak 
  INNER JOIN t_saiban_jiken tsj 
    ON tsak.saiban_seq = tsj.saiban_seq 
    AND tsak.main_jiken_seq = tsj.jiken_seq 
  INNER JOIN t_saiban ts 
    ON ts.saiban_seq = tsak.saiban_seq 
  INNER JOIN t_anken ta 
    ON ts.anken_id = ta.anken_id 
  LEFT JOIN m_saibansho msaibansho 
    ON msaibansho.saibansho_id = ts.saibansho_id 
  -- ソート条件が名前（被告人）の場合、テーブルを結合
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@CUSTOMER_NAME_KANA.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  -- １人分の名前（被告人）（かな）。優先順位：筆頭顧客、顧客の順。
  LEFT JOIN ( 
    SELECT
      tsc.saiban_seq
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.customer_name_sei_kana ORDER BY tsc.main_flg DESC, tsc.customer_id ASC SEPARATOR ','), ',', 1) customer_name_sei_kana
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.customer_name_mei_kana ORDER BY tsc.main_flg DESC, tsc.customer_id ASC SEPARATOR ','), ',', 1) customer_name_mei_kana
    FROM
      t_saiban_customer tsc
      INNER JOIN t_person tc
        ON tsc.customer_id = tc.customer_id
    GROUP BY
      tsc.saiban_seq
    ) tsc 
    ON ts.saiban_seq = tsc.saiban_seq 
  /*%end */
  LEFT JOIN m_sosakikan msosakikan 
    ON tsak.sosakikan_id = msosakikan.sosakikan_id
   
  -- 検察条件に担当弁護士が有る場合、テーブルを結合
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
  1 = 1
  
  -- 検索ワードが有る場合、裁判所、事件番号、事件名、検察庁、顧客名、顧客名（かな）に部分一致する条件を追加
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.searchWord) */
  AND EXISTS ( 
    SELECT
      * 
    FROM
      ( 
        SELECT
          tsak_n.saiban_seq 
          , tsak_n.main_jiken_seq
        FROM
          t_saiban ts_n 
          INNER JOIN t_anken ta_n 
            ON ts_n.anken_id = ta_n.anken_id 
          LEFT JOIN t_saiban_jiken tsj_n 
            ON ts_n.saiban_seq = tsj_n.saiban_seq 
          LEFT JOIN t_saiban_add_keiji tsak_n 
            ON tsj_n.saiban_seq = tsak_n.saiban_seq
          LEFT JOIN m_saibansho msaibansho_n 
            ON msaibansho_n.saibansho_id = ts_n.saibansho_id 
          LEFT JOIN t_saiban_customer tsc_n 
            ON ts_n.saiban_seq = tsc_n.saiban_seq 
          LEFT JOIN t_person tc_n 
            ON tc_n.customer_id = tsc_n.customer_id 
          LEFT JOIN t_person_add_kojin tcak_n
            ON tc_n.person_id = tcak_n.person_id 
            AND tc_n.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
          LEFT JOIN m_sosakikan msosakikan_n 
            ON tsak_n.sosakikan_id = msosakikan_n.sosakikan_id 
        WHERE
          1 = 1
          AND (
            -- 裁判所
            (
              CASE 
                WHEN ts_n.saibansho_id IS NULL 
                  THEN REPLACE (ts_n.saibansho_name, '　', '') 
                ELSE REPLACE (msaibansho_n.saibansho_name, '　', '') 
              END COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR CASE
                WHEN ts_n.saibansho_id IS NULL 
                  THEN REPLACE (ts_n.saibansho_name, ' ', '') 
                ELSE REPLACE (msaibansho_n.saibansho_name, ' ', '') 
              END COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            )
            
            -- 事件番号
            OR CONCAT( 
              CASE 
                WHEN tsj_n.jiken_year 
                  THEN CONCAT( 
                  CASE tsj_n.jiken_gengo 
                    -- 元号が増えた場合に修正が必要
                    WHEN 5 THEN '令和' 
                    WHEN 4 THEN '平成' 
                    WHEN 3 THEN '昭和' 
                    ELSE '' 
                    END
                  , CASE tsj_n.jiken_year
                    WHEN 1 THEN '元'
                    ELSE tsj_n.jiken_year
                    END
                  , '年'
                ) 
                ELSE '' 
                END
              , CASE 
                WHEN tsj_n.jiken_mark IS NULL 
                  THEN '' 
                WHEN tsj_n.jiken_mark = '' 
                  THEN '' 
                ELSE CONCAT('（', tsj_n.jiken_mark, '）') 
                END
              , CASE 
                WHEN tsj_n.jiken_no 
                  THEN CONCAT('第', tsj_n.jiken_no, '号') 
                ELSE '' 
                END
            ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
             
            -- 事件名
            OR (
              REPLACE ( 
                tsj_n.jiken_name 
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE ( 
                tsj_n.jiken_name 
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            )
            
            -- 検察庁
            OR (
              CASE 
                WHEN tsak_n.sosakikan_id IS NULL 
                  THEN REPLACE (tsak_n.kensatsucho_name, '　', '') 
                ELSE REPLACE (msosakikan_n.sosakikan_name, '　', '')
              END COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR CASE 
                WHEN tsak_n.sosakikan_id IS NULL 
                  THEN REPLACE (tsak_n.kensatsucho_name, ' ', '') 
                ELSE REPLACE (msosakikan_n.sosakikan_name, ' ', '')
              END COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            )
             
            -- 顧客名
            OR (
              REPLACE ( 
                CONCAT( 
                  COALESCE(tc_n.customer_name_sei, '')
                  , COALESCE(tc_n.customer_name_mei, '')
                )
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE ( 
                CONCAT( 
                  COALESCE(tc_n.customer_name_sei, '')
                  , COALESCE(tc_n.customer_name_mei, '')
                )
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            ) 
            
            -- 顧客名（かな）
            OR (
              REPLACE ( 
                CONCAT( 
                  COALESCE(tc_n.customer_name_sei_kana, '')
                  , COALESCE(tc_n.customer_name_mei_kana, '')
                )
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE ( 
                CONCAT( 
                  COALESCE(tc_n.customer_name_sei_kana, '')
                  , COALESCE(tc_n.customer_name_mei_kana, '')
                )
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            ) 
            
            -- 顧客旧姓
            OR (
              REPLACE ( 
                CONCAT( 
                  COALESCE(tcak_n.old_name, '')
                  , COALESCE(tc_n.customer_name_mei, '')
                )
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE ( 
                CONCAT( 
                  COALESCE(tcak_n.old_name, '')
                  , COALESCE(tc_n.customer_name_mei, '')
                )
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            ) 
            
            -- 顧客旧姓（かな）
            OR (
              REPLACE ( 
                CONCAT( 
                  COALESCE(tcak_n.old_name_kana, '')
                  , COALESCE(tc_n.customer_name_mei_kana, '')
                )
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE ( 
                CONCAT( 
                  COALESCE(tcak_n.old_name_kana, '')
                  , COALESCE(tc_n.customer_name_mei_kana, '')
                )
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            ) 
            
            -- 屋号／通称名
            OR (
              REPLACE (
                tcak_n.yago
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE (
                tcak_n.yago
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            ) 
            
            -- 屋号／通称名（かな）
            OR (
              REPLACE (
                tcak_n.yago_kana
                , '　'
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
              OR REPLACE (
                tcak_n.yago_kana
                , ' '
                , ''
              ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(saibanListSearchCondition.searchWord) */NULL
            )
          ) 
      ) AS ex_t
    WHERE
      ex_t.saiban_seq = tsak.saiban_seq
      AND ex_t.main_jiken_seq = tsak.main_jiken_seq
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
  
  -- 起訴日が有る場合、起訴日が該当する条件を追加
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanStartDateFrom) */
  AND ts.saiban_start_date >= /*saibanListSearchCondition.saibanStartDateFrom*/'2022/02/22' 
  /*%end */
  /*%if saibanListSearchCondition.isNotEmpty(saibanListSearchCondition.saibanStartDateTo) */
  AND ts.saiban_start_date <= /*saibanListSearchCondition.saibanStartDateTo*/'2022/02/22' 
  /*%end */
  
  -- 判決日が有る場合、判決日が該当する条件を追加
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
  
  -- 名前（被告人）でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@CUSTOMER_NAME_KANA.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , tsc.customer_name_sei_kana /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , tsc.customer_name_mei_kana /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 検察庁でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@KENSATUCHO_NAME.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , kensatsucho_name_mei /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 裁判手続きでのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBAN_STATUS_ID.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , CAST(ts.saiban_status AS SIGNED) /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 起訴日でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBAN_START_DATE.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , ts.saiban_start_date /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  -- 判決日でのソート
  /*%if @jp.loioz.common.constant.SortConstant$SaibanListSortItem@SAIBAN_END_DATE.equalsByCode(saibanListSortCondition.saibanListSortItem.cd) */
  , ts.saiban_end_date /*%if saibanListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC
  /*%end */
  
  , ts.saiban_seq DESC 
/*%end */
;
