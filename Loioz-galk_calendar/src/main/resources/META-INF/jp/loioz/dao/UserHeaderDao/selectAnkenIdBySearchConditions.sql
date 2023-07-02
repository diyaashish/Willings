SELECT
  /*%if searchConditions.isGetCount */
  COUNT(*)
  /*%else */
  ta.anken_id 
  /*%end */
FROM
  t_anken ta
WHERE
  1 = 1
  AND (
    /** 案件IDの検索 */
    ta.anken_id LIKE /* @infix(searchConditions.keywords) */'%' 
    
    /** 案件名の検索 */
    OR ta.anken_name LIKE /* @infix(searchConditions.keywords) */'%' 
    
    /** 顧客名の検索 */
    OR EXISTS (
      SELECT
        * 
      FROM
        t_anken_customer tac_sub1 
        INNER JOIN t_person tp_sub1 
          ON tac_sub1.customer_id = tp_sub1.customer_id
        LEFT JOIN t_person_add_hojin tcah_sub1 
          ON tp_sub1.person_id = tcah_sub1.person_id 
          AND tp_sub1.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1' 
        LEFT JOIN t_person_add_kojin tcak_sub1 
          ON tp_sub1.person_id = tcak_sub1.person_id 
          AND tp_sub1.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
      WHERE 
        tac_sub1.anken_id = ta.anken_id
        AND (
          /** 名前検索 */
          REPLACE ( CONCAT( COALESCE(tp_sub1.customer_name_sei, ''), COALESCE(tp_sub1.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tp_sub1.customer_name_sei, ''), COALESCE(tp_sub1.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

          /** 名前（かな）検索 */
          OR REPLACE ( CONCAT( COALESCE(tp_sub1.customer_name_sei_kana, ''), COALESCE(tp_sub1.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tp_sub1.customer_name_sei_kana, ''), COALESCE(tp_sub1.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

          /** 旧姓検索 */
          OR REPLACE ( CONCAT( COALESCE(tcak_sub1.old_name, ''), COALESCE(tp_sub1.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tcak_sub1.old_name, ''), COALESCE(tp_sub1.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 旧姓（かな）検索 */
          OR REPLACE ( CONCAT( COALESCE(tcak_sub1.old_name_kana, ''), COALESCE(tp_sub1.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tcak_sub1.old_name_kana, ''), COALESCE(tp_sub1.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 代表者名かな検索 */
          OR REPLACE (tcah_sub1.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE (tcah_sub1.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 代表者名検索 */
          OR REPLACE (tcah_sub1.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE (tcah_sub1.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 担当者名検索 */
          OR REPLACE (tcah_sub1.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE (tcah_sub1.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

           /** 担当者名かな検索 */
          OR REPLACE (tcah_sub1.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE (tcah_sub1.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 屋号／通称名検索 */
          OR REPLACE ( COALESCE(tcak_sub1.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( COALESCE(tcak_sub1.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 屋号／通称名（かな）検索 */
          OR REPLACE ( COALESCE(tcak_sub1.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( COALESCE(tcak_sub1.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'
       )
    )
       
    /** 当事者名の検索 */
    OR EXISTS (
      SELECT
        *
      FROM
        t_kanyosha tk_sub2
        INNER JOIN t_person tp_sub2
          ON tk_sub2.person_id = tp_sub2.person_id
        LEFT JOIN t_person_add_hojin tcah_sub2 
          ON tp_sub2.person_id = tcah_sub2.person_id 
          AND tp_sub2.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1' 
        LEFT JOIN t_person_add_kojin tcak_sub2 
          ON tp_sub2.person_id = tcak_sub2.person_id 
          AND tp_sub2.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
      WHERE
        tk_sub2.anken_id = ta.anken_id
        AND (
          /** 名前検索 */
          REPLACE ( CONCAT( COALESCE(tp_sub2.customer_name_sei, ''), COALESCE(tp_sub2.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tp_sub2.customer_name_sei, ''), COALESCE(tp_sub2.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

          /** 名前（かな）検索 */
          OR REPLACE ( CONCAT( COALESCE(tp_sub2.customer_name_sei_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tp_sub2.customer_name_sei_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

          /** 旧姓検索 */
          OR REPLACE ( CONCAT( COALESCE(tcak_sub2.old_name, ''), COALESCE(tp_sub2.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tcak_sub2.old_name, ''), COALESCE(tp_sub2.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 旧姓（かな）検索 */
          OR REPLACE ( CONCAT( COALESCE(tcak_sub2.old_name_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( CONCAT( COALESCE(tcak_sub2.old_name_kana, ''), COALESCE(tp_sub2.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

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
          OR REPLACE ( COALESCE(tcak_sub2.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( COALESCE(tcak_sub2.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

          /** 屋号／通称名（かな）検索 */
          OR REPLACE ( COALESCE(tcak_sub2.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
          OR REPLACE ( COALESCE(tcak_sub2.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'
        )
    )
    
  )
ORDER BY 
  ta.anken_id DESC , ta.created_at DESC
/*%if searchConditions.isNotEmpty(searchConditions.limitCount) */
LIMIT
  /* searchConditions.limitCount */0
/*%end */
;