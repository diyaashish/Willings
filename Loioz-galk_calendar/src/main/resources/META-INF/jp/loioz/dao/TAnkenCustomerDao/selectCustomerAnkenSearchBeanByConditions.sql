SELECT
  ta.anken_id
  , mb.bunya_name
  , ta.anken_name
  , GROUP_CONCAT( 
    CONCAT( 
      coalesce(tc.customer_name_sei,'')
      , case 
        when tcak.old_name is not null 
          then concat(' (', coalesce(tcak.old_name, ''), ')') 
        else '' 
        end
      , ' '
      , coalesce(tc.customer_name_mei, '')
    ) separator ', '
  ) as customer_name 
FROM
  t_anken ta 
  INNER JOIN t_anken_customer tac 
    USING (anken_id) 
  INNER JOIN t_person tc 
    USING (customer_id) 
  LEFT JOIN t_person_add_kojin tcak 
    ON tcak.person_id = tc.person_id 
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
  LEFT JOIN m_bunya mb
    ON ta.bunya_id = mb.bunya_id
WHERE
  tc.person_id != /* condition.personId */NULL

  /*%if condition.isNotEmpty(condition.ankenId) */
  AND ta.anken_id = /* condition.ankenId */NULL 
  /*%end*/
  
  /*%if condition.isNotEmpty(condition.excludeAnkenIdList) */
  AND ta.anken_id NOT IN  /* condition.excludeAnkenIdList */(NULL) 
  /*%end*/

  /*%if condition.isNotEmpty(condition.bunya) */
  AND ta.bunya_id = /* condition.bunya */NULL
  /*%end*/

  /*%if condition.isNotEmpty(condition.ankenName) */
  AND ( 
    REPLACE (ta.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.ankenName) */'%'
    OR REPLACE (ta.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.ankenName) */'%'
  ) 
  /*%end*/

  /*%if condition.isNotEmpty(condition.name) */
  AND ( 
    ( 
      REPLACE (CONCAT(COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcak.old_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
      OR REPLACE (tcak.old_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR ( 
      REPLACE (tcak.old_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
      OR REPLACE (tcak.old_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
    ) 
    OR EXISTS ( 
      SELECT
        tk_sub.kanyosha_seq 
      FROM
        t_kanyosha tk_sub 
        INNER JOIN t_anken_related_kanyosha tark_sub
          ON tk_sub.kanyosha_seq = tark_sub.kanyosha_seq
        INNER JOIN t_person tc_sub 
          ON tk_sub.person_id = tc_sub.person_id 
        LEFT JOIN t_person_add_kojin tcak_sub 
          ON tcak_sub.person_id = tc_sub.person_id 
          AND tc_sub.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0' 
      WHERE
        tk_sub.anken_id = ta.anken_id 
        AND tark_sub.kanyosha_type = /* @jp.loioz.common.constant.CommonConstant$KanyoshaType@AITEGATA */'2' 
        AND tark_sub.dairi_flg = '0' 
        AND ( 
          (
            REPLACE (CONCAT(COALESCE(tc_sub.customer_name_sei, ''), COALESCE(tc_sub.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
            OR REPLACE (CONCAT(COALESCE(tc_sub.customer_name_sei, ''), COALESCE(tc_sub.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
          ) 
          OR ( 
            REPLACE (CONCAT(COALESCE(tc_sub.customer_name_sei_kana, ''), COALESCE(tc_sub.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
            OR REPLACE (CONCAT(COALESCE(tc_sub.customer_name_sei_kana, ''), COALESCE(tc_sub.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
          ) 
          OR ( 
            REPLACE (tcak_sub.old_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%' 
            OR REPLACE (tcak_sub.old_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
          ) 
          OR ( 
            REPLACE (tcak_sub.old_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
            OR REPLACE (tcak_sub.old_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */'%'
          )
        )
    )
  ) 
/*%end */
GROUP BY
  ta.anken_id
  , mb.bunya_name
  , ta.anken_name
ORDER BY
  ta.anken_id DESC
LIMIT
  0, 100
