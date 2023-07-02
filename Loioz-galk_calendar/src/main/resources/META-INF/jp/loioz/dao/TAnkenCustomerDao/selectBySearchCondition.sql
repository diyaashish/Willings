SELECT DISTINCT
  tc.person_id
  , TRIM( CONCAT( COALESCE(tc.customer_name_sei, ''), ' ', COALESCE(tc.customer_name_mei, ''))) AS customer_name
  , TRIM( CONCAT( COALESCE(tc.customer_name_sei_kana, ''), ' ', COALESCE(tc.customer_name_mei_kana, ''))) AS customer_name_kana
  , tc.customer_type
  , tc.customer_flg
  , tc.advisor_flg
  , tcak.birthday
  , tcak.birthday_display_type
  , tc.zip_code
  , tc.address1
  , tc.address2
FROM
  t_person tc 
  LEFT JOIN t_anken_customer tac 
    USING (customer_id) 
  LEFT JOIN t_person_add_kojin tcak 
    ON tcak.person_id = tc.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
  LEFT JOIN t_person_add_hojin tcah 
    ON tcah.person_id = tc.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
WHERE
  1 = 1
  /** 弁護士は除く */
  AND tc.customer_type != /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2' 

  /** 対象案件に関与者として登録されている名簿は除外 */
  AND NOT EXISTS ( 
    SELECT
      * 
    FROM
      t_kanyosha tk 
    WHERE
      tk.person_id = tc.person_id 
      AND tk.anken_id = /* condition.ankenId */0
  ) 

  /** 対象案件に顧客として登録されている名簿は除外 */
  AND tc.customer_id NOT IN ( 
    SELECT
      customer_id 
    FROM
      t_anken_customer tac 
    WHERE
      tac.anken_id = /* condition.ankenId */0
  ) 
  
/*%if condition.isNotEmpty(condition.personId) */
  AND tc.person_id = /* condition.personId */NULL 
/*%end*/

/*%if condition.isNotEmpty(condition.name) */
  AND ( 
    ( 
      REPLACE ( CONCAT( COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE ( CONCAT( COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE ( CONCAT( COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE ( CONCAT( COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcak.old_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcak.old_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcak.old_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
      OR REPLACE (tcak.old_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcak.yago, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcak.yago, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcak.yago_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcak.yago_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
      OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    )
    /** 旧商号 **/
    OR ( 
      REPLACE (COALESCE(tcah.old_hojin_name, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (COALESCE(tcah.old_hojin_name, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    )
  ) 
/*%end*/
ORDER BY
  tc.person_id DESC 
LIMIT
  0, 100
;
