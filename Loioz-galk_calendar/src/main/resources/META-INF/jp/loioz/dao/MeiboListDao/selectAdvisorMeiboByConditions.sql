SELECT distinct
/*%if countFlg==true */
-- 件数の取得
  COUNT(*) count
/*%end */
/*%if countFlg==false */
  tc.person_id
  , tc.customer_id
  , tc.customer_flg
  , tc.customer_type
  , tc.customer_name_sei
  , tc.customer_name_mei
  , tc.customer_name_sei_kana
  , tc.customer_name_mei_kana
  , tc.zip_code
  , tc.address1
  , tc.address2
  , tc.address_remarks
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
  , tc.customer_created_date
  , tc.remarks
/*%end */
FROM
  t_advisor_contract tkc
  INNER JOIN t_person tc 
    ON tkc.person_id = tc.person_id
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
  -- 契約中を対象とする
  tkc.contract_status IN (/* @jp.loioz.common.constant.CommonConstant$ContractStatus@CONTRACT_NEW.cd */'1', /* @jp.loioz.common.constant.CommonConstant$ContractStatus@CONTRACTING.cd */'2')
  
  /** キーワード */
  /*%if searchConditions.isNotEmpty(searchConditions.searchWord) */
  AND ( 
    ( 
      -- 名簿ID
      tkc.person_id LIKE /* @infix(searchConditions.searchWord) */'%'
    
      -- 顧客名検索
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
      -- 顧客名かな検索
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
      -- 旧姓検索
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
      -- 旧姓かな検索
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
      -- 屋号／通称名検索
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
      -- 屋号／通称名（かな）検索
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
  -- 名簿Noの検索
  AND tkc.person_id LIKE /* @infix(searchConditions.personId) */'%'
  /*%end */
  
  /** 名前検索ワード */
  /*%if searchConditions.isNotEmpty(searchConditions.keyWords) */
  AND ( 
    ( 
      -- 顧客名検索
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
      -- 顧客名かな検索
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
      -- 旧姓検索
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
      -- 旧姓かな検索
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
      -- 屋号／通称名検索
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
      -- 屋号／通称名（かな）検索
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
  
  /*%if searchConditions.isNotEmpty(searchConditions.address) */
  -- 住所の検索 
  AND CONCAT( 
        COALESCE(tc.address1, '')
        , COALESCE(tc.address2, '')
        , COALESCE(tc.address_remarks, '')
      ) COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchConditions.address) */'%' 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.telNo) */
  -- 電話番号検索
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
  -- メールアドレス検索
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
  -- 顧客作成日時の検索
  AND tc.customer_created_date >= /*searchConditions.customerCreateDateFrom*/NULL 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.customerCreateDateTo) */
  -- 顧客作成日時の検索
  AND tc.customer_created_date <= /* searchConditions.customerCreateDateTo */NULL 
  /*%end */
  
  /*%if searchConditions.isNotEmpty(searchConditions.remarks) */
  -- 特記事項の検索
  AND tc.remarks LIKE /* @infix(searchConditions.remarks) */'%' 
  /*%end */
ORDER BY
  1 = 1
  -- countを取得するときは、ORDER BY を設定しない
  /*%if countFlg==false */
  
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$AdvisorMeiboListSortItem@PERSON_ID */
  -- 名簿IDでのソート
  , tc.person_id /*%if sortConditions.isDesc() */ DESC /*%end */
  /*%end */
  
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$AdvisorMeiboListSortItem@NAME */
  -- 名前（かな）でのソート
  , tc.customer_name_sei_kana /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.customer_name_mei_kana /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */
  
  /*%if sortConditions.sortItem == @jp.loioz.common.constant.meiboList.MeiboListConstant$AdvisorMeiboListSortItem@CUSTOMER_CREATE_DATE */
  -- 顧客作成日でのソート
  , tc.customer_created_date /*%if sortConditions.isDesc() */ DESC /*%end */
  , tc.person_id DESC 
  /*%end */
  
  /*%end */
;
