SELECT
/*%if countFlg==true */
-- 件数の取得
  COUNT(*) count
/*%end */
/*%if countFlg==false */
  tc.person_id
  , tc.customer_id
  , tc.customer_flg
  , tc.advisor_flg
  , tc.customer_type
  , tc.customer_name_sei AS name_sei
  , tc.customer_name_mei AS name_mei
  , tc.customer_name_sei_kana AS name_sei_kana
  , tc.customer_name_mei_kana AS name_mei_kana
  , tc.zip_code AS zip_code
  , tc.address1 AS address1
  , tc.address2 AS address2
  , CASE 
    WHEN tcc_yusen_tel.tel_no IS NULL 
      THEN tcc_tel.tel_no
    ELSE tcc_yusen_tel.tel_no
    END AS tel_no
  , CASE
    WHEN tcc_yusen_fax.fax_no IS NULL
      THEN tcc_fax.fax_no
    ELSE tcc_yusen_fax.fax_no
    END AS fax_no
  , CASE
    WHEN tcc_yusen_mail_address.mail_address IS NULL
      THEN tcc_mail_address.mail_address
    ELSE tcc_yusen_mail_address.mail_address
    END AS mail_address
  , tc.customer_created_date
  , EXISTS (
      SELECT
        *
      FROM
        t_anken_customer tac
      WHERE
        tac.customer_id = tc.person_id
  ) AS exists_anken_customer
  , EXISTS (
      SELECT
        *
      FROM
        t_kanyosha tk
      WHERE
        tk.person_id = tc.person_id
  ) AS exists_kanyosha
/*%end */
FROM
  t_person tc 
  LEFT JOIN t_person_add_hojin tcah 
    ON tc.person_id = tcah.person_id 
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@HOJIN */'1'
  LEFT JOIN t_person_add_kojin tcak 
    ON tc.person_id = tcak.person_id 
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@KOJIN */'0'
    
  -- 優先電話番号
  LEFT JOIN ( 
    SELECT
      tcc_n.person_id
      , tcc_n.tel_no 
    FROM
      t_person_contact tcc_n
    WHERE
      tcc_n.yusen_tel_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' 
  ) tcc_yusen_tel 
    ON tc.person_id = tcc_yusen_tel.person_id
  
  -- 優先FAX番号
  LEFT JOIN (
    SELECT
      tcc_n.person_id
      , tcc_n.fax_no
    FROM
      t_person_contact tcc_n
    WHERE
      tcc_n.yusen_fax_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
  ) tcc_yusen_fax
    ON tc.person_id = tcc_yusen_fax.person_id
  
  -- 優先メールアドレス
  LEFT JOIN (
    SELECT
      tcc_n.person_id
      , tcc_n.mail_address
    FROM
      t_person_contact tcc_n
    WHERE
      tcc_n.yusen_mail_address_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
  ) tcc_yusen_mail_address
    ON tc.person_id = tcc_yusen_mail_address.person_id
  
  -- 電話番号
  LEFT JOIN (
    -- 名簿ID、連絡先区分、作成日でソートし先頭の1件の電話番号を取得
    SELECT 
      person_id
      , tel_no
    FROM (
      SELECT
        person_id
        , tel_no
        -- 名簿IDごとに電話番号の連絡先区分、作成日でソートし、番号をふる
        , ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY tel_no_type IS NULL, tel_no_type ASC, created_at ASC) AS sort_order
      FROM 
        t_person_contact
      WHERE 
        tel_no IS NOT NULL
    ) AS ranked
    -- ROW_NUMBERでふった番号の1のみ取得
    WHERE 
      sort_order = 1
  ) tcc_tel
    ON tc.person_id = tcc_tel.person_id

  -- FAX番号
  LEFT JOIN (
    -- 連絡先区分、作成日でソートし先頭の1件のFAX番号を取得
    SELECT 
      person_id
      , fax_no
    FROM (
      SELECT
        person_id
        , fax_no
        -- 名簿IDごとにFAX番号の連絡先区分、作成日でソートし、番号をふる
        , ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY fax_no_type IS NULL, fax_no_type ASC, created_at ASC) AS sort_order
      FROM 
        t_person_contact
      WHERE 
        fax_no IS NOT NULL
    ) AS ranked
    -- ROW_NUMBERでふった番号の1のみ取得
    WHERE 
      sort_order = 1
  ) tcc_fax
    ON tc.person_id = tcc_fax.person_id

  -- メールアドレス
  LEFT JOIN (
    -- 連絡先区分、作成日でソートし先頭の1件のFAX番号を取得
    SELECT 
      person_id
      , mail_address
    FROM (
      SELECT
        person_id
        , mail_address
        -- 名簿IDごとにメールアドレスの連絡先区分、作成日でソートし、番号をふる
        , ROW_NUMBER() OVER (PARTITION BY person_id ORDER BY mail_address_type IS NULL, mail_address_type ASC, created_at ASC) AS sort_order
      FROM 
        t_person_contact
      WHERE 
        mail_address IS NOT NULL
    ) AS ranked
    -- ROW_NUMBERでふった番号の1のみ取得
    WHERE 
      sort_order = 1
  ) tcc_mail_address
    ON tc.person_id = tcc_mail_address.person_id
WHERE
  1 = 1
  
  /** 一覧のキーワード検索と同じ条件 */
  AND ( 
    ( 
      /** 名簿ID */
      tc.person_id LIKE /* @infix(quickSearchText) */'%'
      
      /** 名前検索 */
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 名前（かな）検索 */
      REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 旧姓検索 */
      REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 旧姓（かな）検索 */
      REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 代表者名検索 */
      REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 代表者名かな検索 */
      REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 担当者名検索 */
      REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 担当者名かな検索 */
      REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    )
    OR ( 
      /** 屋号／通称名検索 */
      REPLACE (
        COALESCE(tcak.yago, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 屋号／通称名（かな）検索 */
      REPLACE (
        COALESCE(tcak.yago_kana, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago_kana, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    OR ( 
      /** 特記事項 */
      REPLACE (
        COALESCE(tc.remarks, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (
        COALESCE(tc.remarks, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    ) 
    /** 住所検索 */
    OR CONCAT( 
      COALESCE(tc.address1, '')
      , COALESCE(tc.address2, '')
      , COALESCE(tc.address_remarks, '')
    ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    /** 電話番号検索 */
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_person_contact 
      WHERE
        person_id = tc.person_id 
        AND REPLACE (tel_no, '-', '') LIKE REPLACE (/* @infix(quickSearchText) */'%', '-', '')
    )
    OR ( 
      /** 旧商号 **/
      REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%' 
      OR REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(quickSearchText) */'%'
    )
  ) 

ORDER BY
  1 = 1
  -- countを取得するときは、ORDER BY を設定しない
  /*%if countFlg==false */
  
    /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$AllMeiboListSortItem@PERSON_ID */
  -- 名簿IDでのソート
  , tc.person_id /*%if sortConditions.isDesc() */ DESC /*%end */
  /*%end */
  
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$AllMeiboListSortItem@NAME */
  -- 名前（かな）でのソート
  , tc.customer_name_sei_kana /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.customer_name_mei_kana /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */

  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$AllMeiboListSortItem@CUSTOMER_CREATE_DATE */
  -- 顧客登録日でのソート
  , tc.customer_created_date /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */
  
  /*%end */
;
