SELECT 
/*%if countFlg==true */
-- 件数の取得
  COUNT(*) count
/*%end */
/*%if countFlg==false */
  tp.person_id
  , COALESCE(tp.customer_name_sei, '') person_name_sei
  , COALESCE(tp.customer_name_sei_kana, '') person_name_sei_kana
  , COALESCE(tp.customer_name_mei, '') person_name_mei
  , COALESCE(tp.customer_name_mei_kana, '') person_name_mei_kana
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tac.anken_status anken_status
  , IFNULL(ftapaps_all_status.total_amount, 0) AS fee_total_amount
  , IFNULL(ftapaps_status_unclaimed.status_total_amount, 0) AS fee_unclaimed_total_amount
  , ftapaps_all_status.last_edit_at AS last_edit_at 
/*%end */
FROM
  t_anken_customer tac
  INNER JOIN t_anken ta
    ON tac.anken_id = ta.anken_id 
  INNER JOIN t_person tp 
    ON tp.person_id = tac.customer_id
  -- 「未請求」の報酬の合計金額
  LEFT JOIN ( 
    SELECT
      tf.anken_id
      , tf.person_id
      , (IFNULL(sum(tf.fee_amount), 0) + IFNULL(sum(tf.tax_amount), 0)) status_total_amount 
    FROM
      t_fee tf 
    WHERE
      -- 未請求
      tf.fee_payment_status = /* @jp.loioz.common.constant.CommonConstant$FeePaymentStatus@UNCLAIMED */'1' 
    GROUP BY
      tf.anken_id
      , tf.person_id
  ) ftapaps_status_unclaimed 
    ON tac.anken_id = ftapaps_status_unclaimed.anken_id 
    AND tac.customer_id = ftapaps_status_unclaimed.person_id
  -- 全ての報酬の合計金額と最終更新日時
  LEFT JOIN ( 
    SELECT
      tf.anken_id
      , tf.person_id
      , (IFNULL(sum(tf.fee_amount), 0) + IFNULL(sum(tf.tax_amount), 0)) total_amount
      , MAX(tf.updated_at) last_edit_at 
    FROM
      t_fee tf 
    GROUP BY
      tf.anken_id
      , tf.person_id
  ) ftapaps_all_status 
    ON tac.anken_id = ftapaps_all_status.anken_id 
    AND tac.customer_id = ftapaps_all_status.person_id
  -- 売上計上先が有る場合、テーブル結合
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.salesOwner) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_sales_owner
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@SALES_OWNER */'1' 
      AND account_seq = /* feeListSearchCondition.salesOwner */'1'
  ) AS tat_sales_owner
    ON tat_sales_owner.anken_id = ta.anken_id 
/*%end */
  -- 担当弁護士が有る場合、テーブルを結合
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.tantoLaywer) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /* feeListSearchCondition.tantoLaywer */'1'
  ) AS tat_lawyer
    ON tat_lawyer.anken_id = ta.anken_id 
/*%end */
  -- 担当事務が有る場合、テーブルを結合
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.tantoJimu) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /* feeListSearchCondition.tantoJimu */'1'
  ) AS tat_jimu
    ON tat_jimu.anken_id = ta.anken_id 
/*%end */

WHERE
-- 検索ワードで検索する場合の条件
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.searchWord) */
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
            REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

            -- 担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''

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
        REPLACE (ta_2.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
        OR REPLACE (ta_2.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.searchWord) */''
      )
  )
/*%end*/

-- 検索条件で検索する場合の条件
/*%if !feeListSearchCondition.isNotEmpty(feeListSearchCondition.searchWord) */
  1 = 1
  -- ステータスが該当する条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.ankenStatusList) */
  AND tac.anken_status IN /* feeListSearchCondition.ankenStatusList */(null) 
/*%end*/
  -- 名簿IDに部分一致する条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.personId) */
  AND tp.person_id LIKE /* @infix(feeListSearchCondition.personId) */''
/*%end*/
  -- 名前が有る場合、顧客名、顧客名（かな）、旧姓、旧姓（かな）、代表者名、代表者名（かな）、担当者名、担当者名（かな）に部分一致する条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.personName) */
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
            REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

            -- 担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.personName) */''

      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
/*%end */
  -- 案件名に部分一致する条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.ankenName) */
  AND EXISTS (
    SELECT
      *
    FROM 
      t_anken ta_2
    WHERE
      ta_2.anken_id = tac.anken_id
      -- 案件名検索
      AND (
        REPLACE (ta_2.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.ankenName) */''
        OR REPLACE (ta_2.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(feeListSearchCondition.ankenName) */''
      )
  )
/*%end */
  -- 分野が該当する条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.bunyaId) */
  AND ta.bunya_id = /*feeListSearchCondition.bunyaId*/'1' 
/*%end */
  -- 報酬合計の範囲指定条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.feeTotalFrom) */
  AND IFNULL(ftapaps_all_status.total_amount, 0) >= /*feeListSearchCondition.feeTotalFrom*/0
/*%end */
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.feeTotalTo) */
  AND IFNULL(ftapaps_all_status.total_amount, 0) <= /*feeListSearchCondition.feeTotalTo*/0
/*%end */
  -- 未請求あり
/*%if feeListSearchCondition.unclaimed */
  AND IFNULL(ftapaps_status_unclaimed.status_total_amount, 0) > 0
/*%end */
  -- 最終更新日条件を追加
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.lastEditAtFrom) */
  AND ftapaps_all_status.last_edit_at >= /*feeListSearchCondition.lastEditAtFrom*/'2022/02/22'
/*%end*/
/*%if feeListSearchCondition.isNotEmpty(feeListSearchCondition.lastEditAtTo) */
  AND ftapaps_all_status.last_edit_at <= /*feeListSearchCondition.lastEditAtTo*/'2022/02/22'
/*%end*/
/*%end*/

ORDER BY
  1 = 1
  -- 名簿IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@PERSON_ID.equalsByCode(feeListSortCondition.sortItem.cd) */
  , CAST(tp.person_id AS SIGNED) /*%if feeListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@ANKEN_NAME.equalsByCode(feeListSortCondition.sortItem.cd) */
  , ta.anken_name /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 名前（かな）でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@PERSON_NAME.equalsByCode(feeListSortCondition.sortItem.cd) */
  , tp.customer_name_sei_kana /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , tp.customer_name_mei_kana /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 案件IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@ANKEN_ID.equalsByCode(feeListSortCondition.sortItem.cd) */
  , CAST(ta.anken_id AS SIGNED) /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 分野でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@BUNYA_ID.equalsByCode(feeListSortCondition.sortItem.cd) */
  , CAST(ta.bunya_id AS SIGNED) /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@ANKEN_NAME.equalsByCode(feeListSortCondition.sortItem.cd) */
  , ta.anken_name /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 顧客ステータスでのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@ANKEN_STATUS.equalsByCode(feeListSortCondition.sortItem.cd) */
  , CAST(tac.anken_status AS SIGNED) /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 報酬合計でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@FEE_TOTAL.equalsByCode(feeListSortCondition.sortItem.cd) */
  , IFNULL(ftapaps_all_status.total_amount, 0) /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 未処理でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@UNTREATED.equalsByCode(feeListSortCondition.sortItem.cd) */
  , IFNULL(ftapaps_status_unclaimed.status_total_amount, 0) /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
  -- 最終更新日時でのソート
/*%if @jp.loioz.common.constant.SortConstant$FeeListSortItem@LAST_UPDATED_AT.equalsByCode(feeListSortCondition.sortItem.cd) */
  , ftapaps_all_status.last_edit_at /*%if feeListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
;
