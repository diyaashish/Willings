SELECT 
  /*%if searchConditions.isGetCount */
  COUNT(DISTINCT CASE 
  /** 子裁判の場合は、親の裁判SEQ */
  WHEN tst.saiban_seq IS NOT NULL AND tst.parent_seq IS NOT NULL 
    THEN tst.parent_seq 
  /** それ以外 */
  ELSE ts.saiban_seq 
  END ) AS saiban_seq
  /*%else */
  CASE 
  /** 子裁判の場合は、親の裁判SEQ */
  WHEN tst.saiban_seq IS NOT NULL AND tst.parent_seq IS NOT NULL 
    THEN tst.parent_seq 
  /** それ以外 */
  ELSE ts.saiban_seq 
  END AS saiban_seq
  /*%end */
FROM
  t_saiban ts 
  LEFT JOIN t_saiban_tree tst
    ON ts.saiban_seq = tst.saiban_seq
WHERE
  1 = 1 
  AND ( 
    /** 案件IDの検索 */
    ts.anken_id LIKE /* @infix(searchConditions.keywords) */'%'  
    
    /** 事件情報の検索 */
    OR ts.saiban_seq IN (
      SELECT
        CASE 
          /** 子裁判の場合は、親の裁判SEQ */
          WHEN tsj_sub.saiban_seq IS NOT NULL 
          AND tst_sub.parent_seq IS NOT NULL 
            THEN tst_sub.parent_seq 
          /** それ以外は */
          ELSE tsj_sub.saiban_seq 
          END AS saiban_seq 
      FROM
        t_saiban_jiken tsj_sub 
        LEFT JOIN t_saiban_tree tst_sub 
          ON tsj_sub.saiban_seq = tst_sub.saiban_seq 
      WHERE
        1 = 1 
        AND ( 
          /** 事件情報の検索 */
          REPLACE (tsj_sub.jiken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tsj_sub.jiken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 裁判事件番号の検索 */
          OR CONCAT( 
            /** 和暦の可変 */
            CASE tsj_sub.jiken_gengo 
              /*%for enum :  @jp.loioz.common.constant.CommonConstant$EraType@jpValuesList() */
              WHEN /* enum.cd */'5' THEN /* enum.val */'令和' 
              /*%end */
              ELSE '' 
              END
            , 
            /** 〇〇年 */
            CASE tsj_sub.jiken_year 
              WHEN 1 THEN '元年' 
              ELSE CONCAT(tsj_sub.jiken_year, '年') 
              END
            , 
            /** （〇〇） */
            IFNULL( 
              CASE tsj_sub.jiken_mark 
                WHEN tsj_sub.jiken_mark <> '' THEN '' 
                ELSE CONCAT('（', tsj_sub.jiken_mark, '）') 
                END
              , ''
            ) 
            , 
            /** 第〇〇号 */
            IFNULL( 
              CASE tsj_sub.jiken_no 
                WHEN tsj_sub.jiken_no <> '' THEN '' 
                ELSE CONCAT('第', tsj_sub.jiken_no, '号') 
                END
              , ''
            )
          ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
        ) 
      GROUP BY
        saiban_seq
    )

    /** 顧客情報の検索 */
    OR ts.saiban_seq IN ( 
      SELECT
        CASE 
          /** 子裁判の場合は、親の裁判SEQ */
          WHEN tst_sub1.saiban_seq IS NOT NULL AND tst_sub1.parent_seq IS NOT NULL 
            THEN tst_sub1.parent_seq 
          /** それ以外 */
          ELSE ts_sub1.saiban_seq 
          END AS saiban_seq 
      FROM
        t_saiban ts_sub1 
        LEFT JOIN t_saiban_tree tst_sub1 
          ON ts_sub1.saiban_seq = tst_sub1.saiban_seq 
        INNER JOIN t_saiban_customer tsc_sub1 
          ON ts_sub1.saiban_seq = tsc_sub1.saiban_seq 
        INNER JOIN t_person tp 
          ON tsc_sub1.customer_id = tp.customer_id 
        LEFT JOIN t_person_add_hojin tpah 
          ON tp.person_id = tpah.person_id 
          AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1' 
        LEFT JOIN t_person_add_kojin tpak 
          ON tp.person_id = tpak.person_id 
          AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
      WHERE
        1 = 1 
        AND ( 
          /** 名前検索 */
          REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 名前（かな）検索 */
          OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 旧姓検索 */
          OR REPLACE (CONCAT(COALESCE(tpak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (CONCAT(COALESCE(tpak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 旧姓（かな）検索 */
          OR REPLACE (CONCAT(COALESCE(tpak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (CONCAT(COALESCE(tpak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 代表者名かな検索 */
          OR REPLACE (tpah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tpah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 代表者名検索 */
          OR REPLACE (tpah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tpah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 担当者名検索 */
          OR REPLACE (tpah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tpah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 担当者名かな検索 */
          OR REPLACE (tpah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tpah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 屋号／通称名検索 */
          OR REPLACE (COALESCE(tpak.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (COALESCE(tpak.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 屋号／通称名（かな）検索 */
          OR REPLACE (COALESCE(tpak.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (COALESCE(tpak.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
        ) 
      GROUP BY
        saiban_seq 
    ) 
    /** 当事者情報の検索 */
    OR ts.saiban_seq IN ( 
      SELECT
        CASE 
          /** 子裁判の場合は、親の裁判SEQ */
          WHEN tst_sub2.saiban_seq IS NOT NULL AND tst_sub2.parent_seq IS NOT NULL 
            THEN tst_sub2.parent_seq 
          /** それ以外は */
          ELSE ts_sub2.saiban_seq
          END AS saiban_seq 
      FROM
        t_saiban ts_sub2 
        LEFT JOIN t_saiban_tree tst_sub2 
          ON ts_sub2.saiban_seq = tst_sub2.saiban_seq 
        INNER JOIN t_kanyosha tk_sub2 
          ON ts_sub2.anken_id = tk_sub2.anken_id 
        INNER JOIN t_person tp_sub2 
          ON tk_sub2.person_id = tp_sub2.person_id 
        LEFT JOIN t_person_add_hojin tcah_sub2 
          ON tp_sub2.person_id = tcah_sub2.person_id 
          AND tp_sub2.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1' 
        LEFT JOIN t_person_add_kojin tcak_sub2 
          ON tp_sub2.person_id = tcak_sub2.person_id 
          AND tp_sub2.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
      WHERE
        1 = 1 
        AND ( 
          /** 名前検索 */
          REPLACE(CONCAT(COALESCE(tp_sub2.customer_name_sei, ''), COALESCE(tp_sub2.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (CONCAT(COALESCE(tp_sub2.customer_name_sei, ''), COALESCE(tp_sub2.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 名前（かな）検索 */
          OR REPLACE(CONCAT(COALESCE(tp_sub2.customer_name_sei_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE(CONCAT(COALESCE(tp_sub2.customer_name_sei_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 旧姓検索 */
          OR REPLACE(CONCAT(COALESCE(tcak_sub2.old_name, ''), COALESCE(tp_sub2.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE(CONCAT(COALESCE(tcak_sub2.old_name, ''), COALESCE(tp_sub2.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 旧姓（かな）検索 */
          OR REPLACE(CONCAT(COALESCE(tcak_sub2.old_name_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE(CONCAT(COALESCE(tcak_sub2.old_name_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 代表者名かな検索 */
          OR REPLACE (tcah_sub2.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tcah_sub2.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 代表者名検索 */
          OR REPLACE (tcah_sub2.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tcah_sub2.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 担当者名検索 */
          OR REPLACE (tcah_sub2.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tcah_sub2.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 担当者名かな検索 */
          OR REPLACE (tcah_sub2.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (tcah_sub2.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 屋号／通称名検索 */
          OR REPLACE (COALESCE(tcak_sub2.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (COALESCE(tcak_sub2.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          /** 屋号／通称名（かな）検索 */
          OR REPLACE (COALESCE(tcak_sub2.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'  
          OR REPLACE (COALESCE(tcak_sub2.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
        ) 
      GROUP BY saiban_seq 
    )
    /** 検索文字列 OR句 END */
  ) 

/** GROUP BY */
/** 特定条件化でのみGROUP BYを行う際、 if文では句を跨ぐことができず、自動削除もされないためができないので、組み込み変数を利用して記載する */
  /*%if !searchConditions.isGetCount */
    /*# "GROUP BY saiban_seq, ts.saiban_seq"*/
  /*%end */
  
ORDER BY 
  ts.created_at DESC

/*%if searchConditions.isNotEmpty(searchConditions.limitCount) */
LIMIT
  /* searchConditions.limitCount */0
/*%end */
;