SELECT
  tc.* 
FROM
t_person tc 
  LEFT JOIN t_person_add_kojin tcak
    ON tcak.person_id = tc.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
  LEFT JOIN t_person_add_hojin tcah
    ON tcah.person_id = tc.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
WHERE
  tc.customer_type = /* condition.customerType */NULL
  
  /*%if condition.isNotEmpty(condition.excludePersonIdList) */
  AND tc.person_id NOT IN /* condition.excludePersonIdList */(NULL)
  /*%end */
  
  /*%if condition.isNotEmpty(condition.keywords) */
  AND (
    tc.person_id LIKE /*@infix(condition.keywords) */'%'
    /** 名前の検索（漢字） */
    OR (
      REPLACE (CONCAT(COALESCE(tc.customer_name_sei,''),COALESCE(tc.customer_name_mei,'')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei,''),COALESCE(tc.customer_name_mei,'')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
    ) 
    /** 名前の検索（ひらがな） */
    OR (
      REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana,''),COALESCE(tc.customer_name_mei_kana,'')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana,''),COALESCE(tc.customer_name_mei_kana,'')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
    )
    /** 旧姓の検索 */
    OR (
      REPLACE (tcak.old_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (tcak.old_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    ) 
    /** 旧姓かなの検索 */
    OR (
      REPLACE (tcak.old_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (tcak.old_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    )
    /** 代表名の検索 */
    OR (
      REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    ) 
    /** 代表名かなの検索 */
    OR (
      REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    )
    /** 担当者名の検索 */
    OR (
      REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    )
    /** 担当者名かなの検索 */
    OR (
      REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    )
    OR ( 
      /** 屋号／通称名検索 */
      REPLACE (
        COALESCE(tcak.yago, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    ) 
    OR ( 
      /** 屋号／通称名（かな）検索 */
      REPLACE (
        COALESCE(tcak.yago_kana, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago_kana, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    ) 
    OR ( 
      /** 旧商号 **/
      REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%' 
      OR REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.keywords) */'%'
    )
  )
  /*%end */
ORDER BY
  tc.created_at DESC 
LIMIT 0, 100
;