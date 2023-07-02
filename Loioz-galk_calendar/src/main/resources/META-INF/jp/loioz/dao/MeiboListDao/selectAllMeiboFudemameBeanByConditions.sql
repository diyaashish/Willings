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
  , tc.remarks
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
  , tcak.old_name
  , tcak.gender_type
  , tcak.birthday
  , CASE 
    WHEN tc.customer_type = '0' 
      THEN tcak.work_place 
    WHEN tc.customer_type = '1' 
      THEN NULL 
    WHEN tc.customer_type = '2' 
      THEN tpal.jimusho_name 
    ELSE NULL 
    END AS work_place
  , CASE 
    WHEN tc.customer_type = '0' 
      THEN tcak.busho_name 
    WHEN tc.customer_type = '1' 
      THEN NULL 
    WHEN tc.customer_type = '2' 
      THEN tpal.busho_name 
    ELSE NULL 
    END AS busho_name
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
  LEFT JOIN t_person_add_lawyer tpal
    ON tc.person_id = tpal.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2'
    
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
      FROM t_person_contact
      WHERE 
        tel_no IS NOT NULL
    ) AS ranked
    -- ROW_NUMBERでふった番号の1のみ取得
    WHERE sort_order = 1
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
  
  /** キーワード */
  /*%if searchConditions.isNotEmpty(searchConditions.searchWord) */
  AND ( 
    ( 
      /** 名簿ID */
      tc.person_id LIKE /* @infix(searchConditions.searchWord) */'%'
      
      /** 名前検索 */
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
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
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
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
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
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
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    OR ( 
      /** 代表者名検索 */
      REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    OR ( 
      /** 代表者名かな検索 */
      REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    OR ( 
      /** 担当者名検索 */
      REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    OR ( 
      /** 担当者名かな検索 */
      REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    )
    OR ( 
      /** 屋号／通称名検索 */
      REPLACE (
        COALESCE(tcak.yago, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    OR ( 
      /** 屋号／通称名（かな）検索 */
      REPLACE (
        COALESCE(tcak.yago_kana, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago_kana, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    ) 
    OR ( 
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
    OR CONCAT( 
      COALESCE(tc.address1, '')
      , COALESCE(tc.address2, '')
      , COALESCE(tc.address_remarks, '')
    ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
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
    OR ( 
      /** 旧商号 **/
      REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%' 
      OR REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.searchWord) */'%'
    )
  ) 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.personId) */
  -- 名簿IDの検索条件
  AND tc.person_id LIKE /* @infix(searchConditions.personId) */'%'
  /*%end */
  
  /** 名前検索ワード */
  /*%if searchConditions.isNotEmpty(searchConditions.keyWords) */
  AND ( 
    ( 
      /** 名前検索 */
      REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
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
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE ( 
        CONCAT( 
          COALESCE(tc.customer_name_sei_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
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
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name, '')
          , COALESCE(tc.customer_name_mei, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
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
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (
        CONCAT( 
          COALESCE(tcak.old_name_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) 
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    ) 
    OR ( 
      /** 代表者名検索 */
      REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    ) 
    OR ( 
      /** 代表者名かな検索 */
      REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    ) 
    OR ( 
      /** 担当者名検索 */
      REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    ) 
    OR ( 
      /** 担当者名かな検索 */
      REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    )
    OR ( 
      /** 屋号／通称名検索 */
      REPLACE (
        COALESCE(tcak.yago, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    ) 
    OR ( 
      /** 屋号／通称名（かな）検索 */
      REPLACE (
        COALESCE(tcak.yago_kana, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (
        COALESCE(tcak.yago_kana, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    ) 
    OR ( 
      /** 旧商号 **/
      REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , '　'
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%' 
      OR REPLACE (
        COALESCE(tcah.old_hojin_name, '')
        , ' '
        , ''
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.keyWords) */'%'
    )
  ) 
  /*%end */

  /*%if searchConditions.isNotEmpty(searchConditions.customerType) */
  /** 顧客種別の検索 */
  AND tc.customer_type = /* searchConditions.customerType.cd */NULL 
  /*%end */
  
  /** 名簿属性 */
  /*%if searchConditions.isNotEmpty(searchConditions.personAttributeCd) */
    /*%if searchConditions.personAttributeCd == @jp.loioz.common.constant.CommonConstant$PersonAttributeCd@CUSTOMER */
    AND tc.customer_type != /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2'
    AND tc.customer_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
    /*%end */
    /*%if searchConditions.personAttributeCd == @jp.loioz.common.constant.CommonConstant$PersonAttributeCd@LAWYER */
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2'
    AND tc.customer_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    AND tc.advisor_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    /*%end */
    /*%if searchConditions.personAttributeCd == @jp.loioz.common.constant.CommonConstant$PersonAttributeCd@OTHER */
    AND tc.customer_type != /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2'
    AND tc.customer_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    AND tc.advisor_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    /*%end */
  /*%end */


  /*%if searchConditions.isNotEmpty(searchConditions.address) */
  /** 住所検索 */
  AND CONCAT( 
    COALESCE(tc.address1, '')
    , COALESCE(tc.address2, '')
    , COALESCE(tc.address_remarks, '')
  ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.address) */'%' 
  /*%end */

  /*%if searchConditions.isNotEmpty(searchConditions.telNo) */
  /** 電話番号検索 */
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
  /** メールアドレス検索 */
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

  /*%if searchConditions.isNotEmpty(searchConditions.customerCreateDateFrom) */
  /** 顧客作成日時の検索 */
  AND tc.customer_created_date >= /*searchConditions.customerCreateDateFrom*/NULL 
  /*%end */

  /*%if searchConditions.isNotEmpty(searchConditions.customerCreateDateTo) */
  /** 顧客作成日時の検索 */
  AND tc.customer_created_date <= /* searchConditions.customerCreateDateTo */NULL 
  /*%end */

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

