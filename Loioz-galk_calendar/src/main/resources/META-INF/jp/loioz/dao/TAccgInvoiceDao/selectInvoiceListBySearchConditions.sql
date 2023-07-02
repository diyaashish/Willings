SELECT 
  tad.accg_doc_seq
  , tai.invoice_seq
  , tp.person_id
  , COALESCE(tp.customer_name_sei, '') AS person_name_sei
  , COALESCE(tp.customer_name_sei_kana, '') AS person_name_sei_kana
  , COALESCE(tp.customer_name_mei, '') AS person_name_mei
  , COALESCE(tp.customer_name_mei_kana, '') AS person_name_mei_kana
  , ta.anken_id
  , ta.anken_name
  , tai.invoice_type
  , tai.invoice_issue_status
  , tai.invoice_date
  , tai.invoice_no
  , tai.due_date
  , tai.invoice_amount
  , tai.invoice_payment_status
  , tai.invoice_memo
  , tar.accg_record_seq
FROM
  t_accg_doc tad 
  INNER JOIN t_accg_invoice tai 
    USING (accg_doc_seq) 
  INNER JOIN t_anken_customer tac 
    ON tad.person_id = tac.customer_id 
    AND tad.anken_id = tac.anken_id 
  INNER JOIN t_anken ta 
    ON tac.anken_id = ta.anken_id 
  INNER JOIN t_person tp 
    ON tp.person_id = tac.customer_id 
  LEFT JOIN t_accg_record tar 
    USING (accg_doc_seq)  
  -- 担当弁護士が有る場合、テーブルを結合
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.tantoLaywer) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /* invoiceListSearchCondition.tantoLaywer */'1'
  ) AS tat_lawyer
    ON tat_lawyer.anken_id = ta.anken_id 
/*%end */
  -- 担当事務が有る場合、テーブルを結合
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.tantoJimu) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /* invoiceListSearchCondition.tantoJimu */'1'
  ) AS tat_jimu
    ON tat_jimu.anken_id = ta.anken_id 
/*%end */

WHERE

  1 = 1
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.searchWord) */
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
            REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 顧客名旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 顧客名旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 名簿ID検索
            OR tac.customer_id LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

            -- 案件名検索、案件ID検索
            OR REPLACE (ta.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR REPLACE (ta.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.searchWord) */''
            OR ta.anken_id LIKE /* @infix(invoiceListSearchCondition.searchWord) */''

      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
/*%end*/

  -- 請求書番号に部分一致する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceNo) */
  AND tai.invoice_no LIKE /* @infix(invoiceListSearchCondition.invoiceNo, '\') */''
/*%end*/

  -- 発行ステータスが該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceIssueStatus) */
  AND tai.invoice_issue_status = /* invoiceListSearchCondition.invoiceIssueStatus */'' 
/*%end*/

  -- 入金状況が該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoicePaymentStatus) */
  AND tai.invoice_payment_status = /* invoiceListSearchCondition.invoicePaymentStatus */'' 
/*%end*/

  -- 請求日が該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceDateFrom) */
  AND invoice_date >= /*invoiceListSearchCondition.invoiceDateFrom*/'' 
/*%end */
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceDateTo) */
  AND invoice_date <= /*invoiceListSearchCondition.invoiceDateTo*/'' 
/*%end */

  -- 請求額が該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceAmountFrom) */
  AND invoice_amount >= /*invoiceListSearchCondition.invoiceAmountFrom*/'' 
/*%end */
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceAmountTo) */
  AND invoice_amount <= /*invoiceListSearchCondition.invoiceAmountTo*/'' 
/*%end */

  -- 支払期日が該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.dueDateFrom) */
  AND due_date >= /*invoiceListSearchCondition.dueDateFrom*/'' 
/*%end */
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.dueDateTo) */
  AND due_date <= /*invoiceListSearchCondition.dueDateTo*/'' 
/*%end */

  -- 請求方法が該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceType) */
  AND tai.invoice_type = /* invoiceListSearchCondition.invoiceType */'' 
/*%end*/

  -- メモが該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.invoiceMemo) */
  AND (
    REPLACE (tai.invoice_memo, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.invoiceMemo) */''
    OR REPLACE (tai.invoice_memo, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(invoiceListSearchCondition.invoiceMemo) */''
  )
/*%end*/

  -- 売上計上先が該当する条件を追加
/*%if invoiceListSearchCondition.isNotEmpty(invoiceListSearchCondition.salesOwner) */
  AND tai.sales_account_seq = /* invoiceListSearchCondition.salesOwner */''
/*%end*/

ORDER BY
  1 = 1
  -- 名簿IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@PERSON_ID.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , CAST(tp.person_id AS SIGNED) /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 名前（かな）でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@PERSON_NAME.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , tp.customer_name_sei_kana /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
  , tp.customer_name_mei_kana /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 案件IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@ANKEN_ID.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , ta.anken_id /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@ANKEN_NAME.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , ta.anken_name /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 請求方法でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@INVOICE_TYPE.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , CAST(tai.invoice_type AS SIGNED) /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 発行ステータスでのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@INVOICE_ISSUE_STATUS.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , CAST(tai.invoice_issue_status AS SIGNED) /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 入金状況でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@INVOICE_PAYMENT_STATUS.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , CAST(tai.invoice_payment_status AS SIGNED) /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 請求日でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@INVOICE_DATE.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , invoice_date /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 支払期日でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@DUE_DATE.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , due_date /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 請求額でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@INVOICE_AMOUNT.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  , invoice_amount /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 請求番号でのソート
/*%if @jp.loioz.common.constant.SortConstant$InvoiceListSortItem@INVOICE_NO.equalsByCode(invoiceListSortCondition.sortItem.cd) */
  -- 文字列の数値が、「1,11,12,2,22」ではなく「1,2,3,4,10,20」と並ぶように文字数でのソートを先に入れる
  , CHAR_LENGTH(invoice_no) /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
  , invoice_no /*%if invoiceListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  , tad.created_at DESC 
;
