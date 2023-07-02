SELECT 
/*%if countFlg==true */
-- 件数の取得
  COUNT(DISTINCT ta.anken_id, tac.customer_id) count
/*%end */
/*%if countFlg==false */
-- データの取得
  ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , ta.anken_created_date
  , tc.person_id
  , tac.customer_id
  , COALESCE(tc.customer_name_sei, '') customer_name_sei
  , COALESCE(tc.customer_name_sei_kana, '') customer_name_sei_kana
  , COALESCE(tc.customer_name_mei, '') customer_name_mei
  , COALESCE(tc.customer_name_mei_kana, '') customer_name_mei_kana
  , tac.anken_status
  , tc.customer_created_date
  , tac.junin_date 
/*%end */
FROM
  t_anken ta 
  LEFT JOIN t_anken_customer tac 
    ON ta.anken_id = tac.anken_id 
  INNER JOIN t_person tc 
    ON tc.customer_id = tac.customer_id 
  -- 担当弁護士が有る場合、テーブルを結合
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.tantoLaywer) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /*ankenListSearchCondition.tantoLaywer*/'1'
  ) AS tat_lawyer
    ON tat_lawyer.anken_id = ta.anken_id 
/*%end */
  -- 担当事務が有る場合、テーブルを結合
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.tantoJimu) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /*ankenListSearchCondition.tantoJimu*/'1'
  ) AS tat_jimu
    ON tat_jimu.anken_id = ta.anken_id 
/*%end */
WHERE
  1 = 1
  -- 顧問契約先
  AND tc.advisor_flg = '1'
  -- ステータスが該当する条件を追加
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.ankenStatusList) */
  AND tac.anken_status IN /*ankenListSearchCondition.ankenStatusList*/(1) 
/*%end*/
/*%if !ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.ankenStatusList) */
  AND tac.anken_status IS NULL 
/*%end*/
  -- 検索ワードが有る場合、案件名、相手方名、相手方名（かな）、顧客名、顧客名（かな）に部分一致する条件を追加
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.searchWord) */
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
          LEFT JOIN t_anken_tanto tat_lawyer 
            ON tat_lawyer.anken_id = ta.anken_id 
          LEFT JOIN t_anken_tanto tat_jimu 
            ON tat_jimu.anken_id = ta.anken_id 
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

            -- 案件ID
            ta.anken_id LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 案件名検索
            OR REPLACE (ta.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (ta.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者名検索 
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei, ''), COALESCE(tkc.kanyosha_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei, ''), COALESCE(tkc.kanyosha_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
             
            -- 関与者名（かな）検索
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_name_sei_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者旧姓検索
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name, ''), COALESCE(tkc.kanyosha_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name, ''), COALESCE(tkc.kanyosha_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tkc.kanyosha_old_name_kana, ''), COALESCE(tkc.kanyosha_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者代表者検索
            OR REPLACE (tkc.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tkc.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者代表者(かな)検索
            OR REPLACE (tkc.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tkc.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者担当者検索
            OR REPLACE (tkc.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tkc.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者担当者(かな)検索
            OR REPLACE (tkc.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tkc.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者屋号検索
            OR REPLACE (COALESCE(tkc.kanyosha_yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (COALESCE(tkc.kanyosha_yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 関与者屋号（かな）検索
            OR REPLACE (COALESCE(tkc.kanyosha_yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (COALESCE(tkc.kanyosha_yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名検索
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei, ''), COALESCE(tc.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tc.customer_name_sei_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tc.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tc.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tc.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客名担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客屋号検索
            OR REPLACE (COALESCE(tcak.yago, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (COALESCE(tcak.yago, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL

            -- 顧客屋号（かな）検索
            OR REPLACE (COALESCE(tcak.yago_kana, ''), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
            OR REPLACE (COALESCE(tcak.yago_kana, ''), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(ankenListSearchCondition.searchWord) */NULL
      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
/*%end */

  -- 分野が有る場合、分野が該当する条件を追加
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.bunyaId) */
  AND ta.bunya_id = /*ankenListSearchCondition.bunyaId*/'1' 
/*%end */
  -- 案件登録日が有る場合、案件登録日が該当する条件を追加
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.ankenCreateDateFrom) */
  AND ta.anken_created_date >= /*ankenListSearchCondition.ankenCreateDateFrom*/'2022/02/22' 
/*%end */
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.ankenCreateDateTo) */
  AND ta.anken_created_date <= /*ankenListSearchCondition.ankenCreateDateTo*/'2022/02/22' 
/*%end */
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.customerCreateDateTo) */
  AND tc.customer_created_date <= /*ankenListSearchCondition.customerCreateDateTo*/'2022/02/22' 
/*%end */
  -- 受任日が有る場合、受任日が該当する条件を追加
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.jyuninDateFrom) */
  AND tac.junin_date >= /*ankenListSearchCondition.jyuninDateFrom*/'2022/02/22' 
/*%end */
/*%if ankenListSearchCondition.isNotEmpty(ankenListSearchCondition.jyuninDateTo) */
  AND tac.junin_date <= /*ankenListSearchCondition.jyuninDateTo*/'2022/02/22' 
/*%end */
ORDER BY
  1 = 1
  -- countを取得するときは、ORDER BY を設定しない
/*%if countFlg==false */
  -- 案件IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@ANKEN_ID.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , ta.anken_id 
/*%if ankenListSortCondition.isDesc() */
  DESC 
/*%end */
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@ANKEN_NAME.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , ta.anken_name
/*%if ankenListSortCondition.isDesc() */
  DESC 
  , ta.anken_id DESC 
/*%end */
/*%end */
  -- 案件登録日でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@ANKEN_CREATE_DATE.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , ta.anken_created_date 
/*%if ankenListSortCondition.isDesc() */
  DESC 
/*%end */
  , ta.anken_id DESC 
/*%end */
  -- 顧客登録日でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@CUSTOMER_CREATE_DATE.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , tc.customer_created_date 
/*%if ankenListSortCondition.isDesc() */
  DESC 
/*%end */
  , ta.anken_id DESC 
/*%end */
  -- 受任日でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@JUNIN_DATE.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , tac.junin_date 
/*%if ankenListSortCondition.isDesc() */
  DESC 
/*%end */
  , ta.anken_id DESC 
/*%end */
  -- 分野でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@BUNYA_ID.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , CAST(ta.bunya_id AS SIGNED) 
/*%if ankenListSortCondition.isDesc() */
  DESC 
/*%end */
  , ta.anken_id DESC 
/*%end */
  -- 進捗でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@ANKEN_STATUS_ID.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , CAST(tac.anken_status AS SIGNED) 
/*%if ankenListSortCondition.isDesc() */
  DESC 
/*%end */
  , ta.anken_id DESC 
/*%end */
  -- 顧客名（かな）でのソート
/*%if @jp.loioz.common.constant.SortConstant$AnkenListSortItem@CUSTOMER_NAME_KANA.equalsByCode(ankenListSortCondition.ankenListSortItem.cd) */
  , tc.customer_name_sei_kana /*%if ankenListSortCondition.isDesc() */ DESC /*%end */
  , tc.customer_name_mei_kana /*%if ankenListSortCondition.isDesc() */ DESC /*%end */
  , ta.anken_id DESC 
/*%end */
/*%end */
;
