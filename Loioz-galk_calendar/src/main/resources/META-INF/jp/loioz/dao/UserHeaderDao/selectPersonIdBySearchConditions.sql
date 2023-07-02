SELECT
  /*%if searchConditions.isGetCount */
  COUNT(*)
  /*%else */
  tp.person_id
  /*%end */
FROM
  t_person tp 
  LEFT JOIN t_person_add_hojin tcah 
    ON tp.person_id = tcah.person_id 
    AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1' 
  LEFT JOIN t_person_add_kojin tcak 
    ON tp.person_id = tcak.person_id 
    AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
WHERE
  1 = 1 
  AND ( 
    /** 名簿IDの検索条件 */
    tp.person_id LIKE /* @infix(searchConditions.keywords) */'%' 

    /** 名前検索 */
    OR REPLACE ( CONCAT( COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( CONCAT( COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

    /** 名前（かな）検索 */
    OR REPLACE ( CONCAT( COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( CONCAT( COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

    /** 旧姓検索 */
    OR REPLACE ( CONCAT( COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( CONCAT( COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 旧姓（かな）検索 */
    OR REPLACE ( CONCAT( COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( CONCAT( COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 代表者名かな検索 */
    OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 代表者名検索 */
    OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 担当者名検索 */
    OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 担当者名かな検索 */
    OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 屋号／通称名検索 */
    OR REPLACE ( COALESCE(tcak.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( COALESCE(tcak.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 屋号／通称名（かな）検索 */
    OR REPLACE ( COALESCE(tcak.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( COALESCE(tcak.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 特記事項検索 */
    OR REPLACE ( COALESCE(tp.remarks, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( COALESCE(tp.remarks, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'

    /** 住所検索 */
    OR REPLACE ( CONCAT( COALESCE(tp.address1, ''), COALESCE(tp.address2, ''), COALESCE(tp.address_remarks, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( COALESCE(tp.address_remarks, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( CONCAT( COALESCE(tp.address1, ''), COALESCE(tp.address2, ''), COALESCE(tp.address_remarks, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
    OR REPLACE ( COALESCE(tp.address_remarks, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 

    /** 電話番号検索 */
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_person_contact 
      WHERE
        person_id = tp.person_id 
        AND REPLACE (tel_no, '-', '') LIKE REPLACE (/* @infix(searchConditions.keywords) */'%', '-', '')
    )

    /** 旧商号 **/
    OR ( 
      REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%' 
      OR REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keywords) */'%'
    )
  )
ORDER BY
  tp.person_id DESC , tp.created_at DESC
/*%if searchConditions.isNotEmpty(searchConditions.limitCount) */
LIMIT
  /* searchConditions.limitCount */0
/*%end */
;