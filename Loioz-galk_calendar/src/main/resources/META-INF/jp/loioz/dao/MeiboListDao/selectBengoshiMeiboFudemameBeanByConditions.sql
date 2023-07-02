SELECT
/*%if countFlg==true */
  -- 件数の取得
  COUNT(*) count
/*%end */
/*%if countFlg==false */
  tc.person_id
  , tc.customer_flg
  , tc.advisor_flg
  , tc.customer_type
  , tc.customer_name_sei AS bengoshi_name_sei
  , tc.customer_name_sei_kana AS bengoshi_name_sei_kana
  , tc.customer_name_mei AS bengoshi_name_mei
  , tc.customer_name_mei_kana AS bengoshi_name_mei_kana
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
    WHEN tcc_yusen_mail.mail_address IS NULL
      THEN tcc_mail.mail_address
    ELSE tcc_yusen_mail.mail_address
    END AS mail_address
  , tpal.jimusho_name
  , tpal.busho_name
  , tc.zip_code
  , tc.address1
  , tc.address2
  , tc.address_remarks
  , tc.customer_created_date
  , tc.remarks
/*%end */
FROM
  t_person tc 
  LEFT JOIN t_person_add_lawyer tpal
    ON tc.person_id = tpal.person_id
  
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
  ) tcc_yusen_mail
    ON tc.person_id = tcc_yusen_mail.person_id
  
  -- 電話番号
  LEFT JOIN (
    -- 連絡先区分、作成日でソートし先頭の1件の電話番号を取得
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
    -- 連絡先区分、作成日でソートし先頭の1件のメールアドレスを取得
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
  ) tcc_mail
    ON tc.person_id = tcc_mail.person_id
WHERE
  -- 弁護士を対象
  tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER.cd */'2'
  
  -- キーワード
  /*%if searchConditions.isNotEmpty(searchConditions.searchWord) */
  AND ( 
    -- 名簿ID
    tc.person_id LIKE /* @infix(searchConditions.searchWord) */'%'
    OR 
    ( 
      -- 弁護士名
      CONCAT( 
        COALESCE(tc.customer_name_sei, '')
        , COALESCE(tc.customer_name_mei, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
    ) 
    OR 
    ( 
      -- 弁護士名（かな）
      CONCAT( 
        COALESCE(tc.customer_name_sei_kana, '')
        , COALESCE(tc.customer_name_mei_kana, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
    ) 
    OR
    ( 
      /** 特記事項 */
      REPLACE (
        COALESCE(tc.remarks, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (
        COALESCE(tc.remarks, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    /** 住所検索 */
    OR (
      CONCAT( 
        COALESCE(tc.address1, '')
        , COALESCE(tc.address2, '')
        , COALESCE(tc.address_remarks, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    )
    /** 電話番号検索 */
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_person_contact 
      WHERE
        person_id = tc.person_id 
        AND REPLACE (tel_no, '-', '') LIKE REPLACE (/* @infix(searchConditions.searchWord) */'%', '-', '')
    )
  ) 
  /*%end */
  
  -- 名簿IDが有れば、名簿IDが該当する条件を追加
  /*%if searchConditions.isNotEmpty(searchConditions.personId) */
  AND tc.person_id LIKE /* @infix(searchConditions.personId) */'%'
  /*%end */
  
  -- 名前検索ワード
  /*%if searchConditions.isNotEmpty(searchConditions.keyWords) */
  AND ( 
    ( 
      -- 弁護士名
      CONCAT( 
        COALESCE(tc.customer_name_sei, '')
        , COALESCE(tc.customer_name_mei, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
    ) 
    OR 
    ( 
      -- 弁護士名（かな）
      CONCAT( 
        COALESCE(tc.customer_name_sei_kana, '')
        , COALESCE(tc.customer_name_mei_kana, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
    ) 
  ) 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.jimushoName) */
  -- 事務所名の検索
  AND tpal.jimusho_name LIKE /* @infix(searchConditions.jimushoName) */'%'
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.address) */
  -- 住所の検索 
  AND CONCAT( 
        COALESCE(tc.address1, '')
        , COALESCE(tc.address2, '')
        , COALESCE(tc.address_remarks, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.address) */'%' 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.telNo) */
  -- 電話番号の検索 
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_person_contact 
    WHERE
      person_id = tc.person_id 
      AND REPLACE (tel_no, '-', '') LIKE /* @infix(searchConditions.telNo) */'%'
  ) 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.mailAddress) */
  -- メールアドレスの検索 
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_person_contact 
    WHERE
      person_id = tc.person_id 
      AND mail_address LIKE /* @infix(searchConditions.mailAddress) */'%'
  ) 
  /*%end */

  /*%if searchConditions.isNotEmpty(searchConditions.createDateFrom) */
  -- 作成日時の検索
  AND tc.customer_created_date >= /*searchConditions.createDateFrom*/null
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.createDateTo) */
  -- 作成日時の検索
  AND tc.customer_created_date <= /* searchConditions.createDateTo */null
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.remarks) */
  -- 特記事項の検索
  AND tc.remarks COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.remarks) */'%' 
  /*%end */
  
ORDER BY
  1 = 1
  -- countを取得するときは、ORDER BY を設定しない
  /*%if countFlg==false */
  
  -- 名簿IDでのソート
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$BengoshiMeiboListSortItem@PERSON_ID */
  , tc.person_id /*%if sortConditions.isDesc() */ DESC /*%end */
  /*%end */
  
  -- 弁護士名（かな）でのソート
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$BengoshiMeiboListSortItem@NAME */
  , tc.customer_name_sei_kana /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.customer_name_mei_kana /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */
  
  -- 作成日でのソート
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$BengoshiMeiboListSortItem@CREATE_DATE */
  , tc.customer_created_date /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */
  
  -- 事務所名でのソート
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$BengoshiMeiboListSortItem@JIMUSHO_NAME */
  , tpal.jimusho_name /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */
  
  /*%end */
;