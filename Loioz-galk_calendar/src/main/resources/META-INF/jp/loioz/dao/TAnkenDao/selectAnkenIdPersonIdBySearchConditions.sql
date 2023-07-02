SELECT 
  ta.anken_id
  , MIN(tac.customer_id) person_id
  , ta.created_at
FROM
  t_anken ta 
  LEFT JOIN t_anken_customer tac 
    ON ta.anken_id = tac.anken_id 
  INNER JOIN t_person tc 
    ON tc.customer_id = tac.customer_id 
  -- 担当弁護士が有る場合、テーブルを結合
/*%if condition.isNotEmpty(condition.tantoLaywer) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /*condition.tantoLaywer*/'1'
  ) AS tat_lawyer
    ON tat_lawyer.anken_id = ta.anken_id 
/*%end */
  -- 担当事務が有る場合、テーブルを結合
/*%if condition.isNotEmpty(condition.tantoJimu) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /*condition.tantoJimu*/'1'
  ) AS tat_jimu
    ON tat_jimu.anken_id = ta.anken_id 
/*%end */
WHERE
  -- 案件タスク追加をしていない案件を対象とする
  NOT EXISTS ( 
    SELECT
      * 
    FROM
      t_task_anken tta 
    WHERE
      tta.account_seq = /* accountSeq */'1' 
      AND tta.anken_id = ta.anken_id
  )
  -- 名前が有る場合、関与者名、関与者名（かな）、顧客名、顧客名（かな）、旧姓、旧姓（かな）、代表者、代表者（かな）、担当者、担当者（かな）に部分一致する条件を追加
/*%if condition.isNotEmpty(condition.name) */
  AND EXISTS (
    SELECT
      *
    FROM 
      ( 
        SELECT 
          ta.anken_id
          , tac.customer_id
        FROM
          t_anken ta 
          LEFT JOIN t_anken_customer tac 
            ON ta.anken_id = tac.anken_id 
          LEFT JOIN t_person tc 
            ON tc.customer_id = tac.customer_id 
          LEFT JOIN t_person_add_kojin tcak
            ON tc.person_id = tcak.person_id 
            AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
          LEFT JOIN t_person_add_hojin tcah
            ON tc.person_id = tcah.person_id 
            AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
          LEFT JOIN ( 
            SELECT
              tk.kanyosha_seq
              , tk.anken_id
              , tc.customer_name_sei AS kanyosha_name_sei
              , tc.customer_name_mei AS kanyosha_name_mei
              , tc.customer_name_sei_kana AS kanyosha_name_sei_kana
              , tc.customer_name_mei_kana AS kanyosha_name_mei_kana 
              , tcak.old_name AS kanyosha_old_name
              , tcak.old_name_kana AS kanyosha_old_name_kana
              , tcak.yago AS kanyosha_yago
              , tcak.yago_kana AS kanyosha_yago_kana
              , tcah.daihyo_name AS daihyo_name
              , tcah.daihyo_name_kana AS daihyo_name_kana
              , tcah.tanto_name AS tanto_name
              , tcah.tanto_name_kana AS tanto_name_kana
            FROM
              t_kanyosha tk 
              LEFT JOIN t_person tc
                ON tk.person_id = tc.person_id
              LEFT JOIN t_person_add_kojin tcak
                ON tc.person_id = tcak.person_id 
                AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
              LEFT JOIN t_person_add_hojin tcah
                ON tc.person_id = tcah.person_id 
                AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
          ) tkc 
            ON ta.anken_id = tkc.anken_id  
        WHERE
            
            -- 関与者名検索 
            REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei, ''), COALESCE(tkc.kanyosha_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei, ''), COALESCE(tkc.kanyosha_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
             
            -- 関与者名（かな）検索
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者旧姓検索
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name, ''), COALESCE(tkc.kanyosha_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name, ''), COALESCE(tkc.kanyosha_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者代表者検索
            OR REPLACE (tkc.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tkc.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者代表者(かな)検索
            OR REPLACE (tkc.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tkc.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者担当者検索
            OR REPLACE (tkc.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tkc.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者担当者(かな)検索
            OR REPLACE (tkc.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tkc.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者屋号検索
            OR REPLACE (COALESCE(tkc.kanyosha_yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (COALESCE(tkc.kanyosha_yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 関与者屋号（かな）検索
            OR REPLACE (COALESCE(tkc.kanyosha_yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (COALESCE(tkc.kanyosha_yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名検索
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tc.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tc.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客名担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客屋号検索
            OR REPLACE (COALESCE(tcak.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (COALESCE(tcak.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL

            -- 顧客屋号（かな）検索
            OR REPLACE (COALESCE(tcak.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
            OR REPLACE (COALESCE(tcak.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.name) */NULL
      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
/*%end */

  -- 案件IDが有る場合、案件IDが該当する条件を追加
/*%if condition.isNotEmpty(condition.ankenId) */
  AND ta.anken_id LIKE /* @infix(condition.ankenId) */NULL 
/*%end */

  -- 分野が有る場合、分野が該当する条件を追加
/*%if condition.isNotEmpty(condition.bunyaId) */
  AND ta.bunya_id = /*condition.bunyaId*/'1' 
/*%end */

  -- 案件名が有る場合、案件名に部分一致する条件を追加
/*%if condition.isNotEmpty(condition.ankenName) */
  AND EXISTS (
    SELECT
      *
    FROM 
      ( 
        SELECT 
          ta.anken_id
        FROM
          t_anken ta 
        WHERE
            -- 案件名検索
            REPLACE (ta.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.ankenName) */NULL
            OR REPLACE (ta.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(condition.ankenName) */NULL
      ) AS exists_t
    WHERE
      exists_t.anken_id = ta.anken_id
  )
/*%end */

  -- 案件登録日が有る場合、案件登録日が該当する条件を追加
/*%if condition.isNotEmpty(condition.ankenCreateDateFrom) */
  AND ta.anken_created_date >= /*condition.ankenCreateDateFrom*/'2022/02/22' 
/*%end */
/*%if condition.isNotEmpty(condition.ankenCreateDateTo) */
  AND ta.anken_created_date <= /*condition.ankenCreateDateTo*/'2022/02/22' 
/*%end */

GROUP BY
  ta.anken_id
ORDER BY
  ta.anken_id DESC 
LIMIT 0, 100
;