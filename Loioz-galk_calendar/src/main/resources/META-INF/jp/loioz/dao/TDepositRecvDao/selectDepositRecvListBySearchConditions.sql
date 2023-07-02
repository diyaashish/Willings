SELECT 
/*%if countFlg==true */
-- 件数の取得
  COUNT(DISTINCT tac.anken_id, tac.customer_id) count
/*%end */
/*%if countFlg==false */
-- データの取得
  tp.person_id
  , COALESCE(tp.customer_name_sei, '') AS person_name_sei
  , COALESCE(tp.customer_name_sei_kana, '') AS person_name_sei_kana
  , COALESCE(tp.customer_name_mei, '') AS person_name_mei
  , COALESCE(tp.customer_name_mei_kana, '') AS person_name_mei_kana
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tac.anken_status
  , COALESCE(tdr.nyukin_total, 0) AS nyukin_total
  , COALESCE(tdr.shukkin_total, 0)  AS shukkin_total
  , COALESCE(tdr.zandaka_total, 0) AS zandaka_total
  , tdr.last_edit_at
/*%end */
FROM
  t_anken_customer tac
  INNER JOIN t_anken ta
    ON tac.anken_id = ta.anken_id 
  INNER JOIN t_person tp 
    ON tp.customer_id = tac.customer_id 
  -- 預り金の集計（事務所負担フラグが立っているものは出金に含めない）
  LEFT JOIN (
    SELECT
      anken_id
      , person_id
      , SUM( 
        CASE deposit_complete_flg 
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(deposit_amount, 0)
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
          ELSE 0 
          END
      ) AS nyukin_total
      , SUM( 
        CASE deposit_complete_flg 
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(withdrawal_amount, 0)
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
          ELSE 0 
          END
      ) AS shukkin_total
      , SUM( 
        CASE deposit_complete_flg 
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(deposit_amount, 0)
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
          ELSE 0 
          END
      ) - SUM( 
        CASE deposit_complete_flg 
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(withdrawal_amount, 0)
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
          ELSE 0 
          END
      ) zandaka_total
      , MAX(updated_at) last_edit_at 
    FROM
      t_deposit_recv 
    WHERE
      ( 
        tenant_bear_flg IS NULL 
        OR tenant_bear_flg != /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
      ) 
    GROUP BY
      anken_id
      , person_id
  ) AS tdr
    ON tdr.anken_id = tac.anken_id AND tac.customer_id = tdr.person_id
  -- 売上計上先が有る場合、テーブルを結合
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.salesOwner) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@SALES_OWNER */'1' 
      AND account_seq = /* depositRecvListSearchCondition.salesOwner */'1'
  ) AS tat_sales_owner
    ON tat_sales_owner.anken_id = ta.anken_id 
/*%end */
  -- 担当弁護士が有る場合、テーブルを結合
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.tantoLaywer) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /* depositRecvListSearchCondition.tantoLaywer */'1'
  ) AS tat_lawyer
    ON tat_lawyer.anken_id = ta.anken_id 
/*%end */
  -- 担当事務が有る場合、テーブルを結合
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.tantoJimu) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /* depositRecvListSearchCondition.tantoJimu */'1'
  ) AS tat_jimu
    ON tat_jimu.anken_id = ta.anken_id 
/*%end */

WHERE
-- 検索ワードで検索する場合の条件
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.searchWord) */
  EXISTS (
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
          LEFT JOIN t_person tp 
            ON tp.customer_id = tac.customer_id 
          LEFT JOIN t_person_add_kojin tcak
            ON tp.person_id = tcak.person_id 
            AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
          LEFT JOIN t_person_add_hojin tcah
            ON tp.person_id = tcah.person_id 
            AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
        WHERE
            -- 顧客名検索
            REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

            -- 担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''

      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
  OR EXISTS (
    SELECT
      *
    FROM 
      t_anken ta_2
    WHERE
      ta_2.anken_id = tac.anken_id
      -- 案件名検索
      AND (
        REPLACE (ta_2.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
        OR REPLACE (ta_2.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.searchWord) */''
      )
  )
/*%end*/

-- 検索条件で検索する場合の条件
/*%if !depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.searchWord) */
  1 = 1
  -- ステータスが該当する条件を追加
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.ankenStatusList) */
  AND tac.anken_status IN /* depositRecvListSearchCondition.ankenStatusList */(null) 
/*%end*/
  -- 名簿IDに部分一致する条件を追加
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.personId) */
  AND tp.person_id LIKE /* @infix(depositRecvListSearchCondition.personId) */''
/*%end*/
  -- 名前が有る場合、顧客名、顧客名（かな）、旧姓、旧姓（かな）、代表者名、代表者名（かな）、担当者名、担当者名（かな）に部分一致する条件を追加
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.personName) */
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
          LEFT JOIN t_person tp 
            ON tp.customer_id = tac.customer_id 
          LEFT JOIN t_person_add_kojin tcak
            ON tp.person_id = tcak.person_id 
            AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
          LEFT JOIN t_person_add_hojin tcah
            ON tp.person_id = tcah.person_id 
            AND tp.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
        WHERE
            -- 顧客名検索
            REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

            -- 担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.personName) */''

      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
/*%end */
  -- 案件名に部分一致する条件を追加
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.ankenName) */
  AND EXISTS (
    SELECT
      *
    FROM 
      t_anken ta_2
    WHERE
      ta_2.anken_id = tac.anken_id
      -- 案件名検索
      AND (
        REPLACE (ta_2.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.ankenName) */''
        OR REPLACE (ta_2.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(depositRecvListSearchCondition.ankenName) */''
      )
  )
/*%end */
  -- 分野が該当する条件を追加
/*%if depositRecvListSearchCondition.isNotEmpty(depositRecvListSearchCondition.bunyaId) */
  AND ta.bunya_id = /*depositRecvListSearchCondition.bunyaId*/'' 
/*%end */
/*%end*/

  -- 預り金残高が該当する条件を追加
/*%if @jp.loioz.common.constant.CommonConstant$TotalDepositBalance@PLUS_ONLY.equalsByCode(depositRecvListSearchCondition.totalDepositBalance) */
  AND tdr.zandaka_total > 0
/*%end */
/*%if @jp.loioz.common.constant.CommonConstant$TotalDepositBalance@MINUS_ONLY.equalsByCode(depositRecvListSearchCondition.totalDepositBalance) */
  AND tdr.zandaka_total < 0
/*%end */

ORDER BY
  1 = 1
  -- 名簿IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@PERSON_ID.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , CAST(tp.person_id AS SIGNED) /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@ANKEN_NAME.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , ta.anken_name /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 名前（かな）でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@PERSON_NAME.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , tp.customer_name_sei_kana /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , tp.customer_name_mei_kana /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 案件IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@ANKEN_ID.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , CAST(ta.anken_id AS SIGNED) /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 分野IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@BUNYA_ID.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , CAST(ta.bunya_id AS SIGNED) /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@ANKEN_NAME.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , ta.anken_name /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 顧客ステータスでのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@ANKEN_STATUS.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , CAST(tac.anken_status AS SIGNED) /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 入金額でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@DEPOSIT_AMOUNT.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , tdr.nyukin_total /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 出金額でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@WITHDRAWAL_AMOUNT.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , tdr.shukkin_total /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 預り金残高でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@DEPOSIT_BALANCE.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , tdr.zandaka_total /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 最終更新日時でのソート
/*%if @jp.loioz.common.constant.SortConstant$DepositRecvListSortItem@LAST_EDIT_AT.equalsByCode(depositRecvListSortCondition.sortItem.cd) */
  , tdr.last_edit_at /*%if depositRecvListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
;
