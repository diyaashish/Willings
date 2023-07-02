SELECT 
  tad.accg_doc_seq
  , tas.statement_seq
  , tp.person_id
  , COALESCE(tp.customer_name_sei, '') AS person_name_sei
  , COALESCE(tp.customer_name_sei_kana, '') AS person_name_sei_kana
  , COALESCE(tp.customer_name_mei, '') AS person_name_mei
  , COALESCE(tp.customer_name_mei_kana, '') AS person_name_mei_kana
  , ta.anken_id
  , ta.anken_name
  , tas.statement_issue_status
  , tas.statement_date
  , tas.statement_no
  , tas.refund_date
  , tas.statement_amount
  , tas.statement_refund_status
  , tas.statement_memo
  , tar.accg_record_seq
FROM
  t_accg_doc tad 
  INNER JOIN t_accg_statement tas 
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
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.tantoLaywer) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto tat_lawyer
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND account_seq = /* statementListSearchCondition.tantoLaywer */'1'
  ) AS tat_lawyer
    ON tat_lawyer.anken_id = ta.anken_id 
/*%end */
  -- 担当事務が有る場合、テーブルを結合
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.tantoJimu) */
  INNER JOIN (
    SELECT
      *
    FROM
      t_anken_tanto
    WHERE
      tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND account_seq = /* statementListSearchCondition.tantoJimu */'1'
  ) AS tat_jimu
    ON tat_jimu.anken_id = ta.anken_id 
/*%end */

WHERE

  1 = 1
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.searchWord) */
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
            REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 顧客名検索（かな）
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tp.customer_name_sei_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 顧客名旧姓検索 
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name, ''), COALESCE(tp.customer_name_mei, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 顧客名旧姓（かな）検索
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (CONCAT(COALESCE(tcak.old_name_kana, ''), COALESCE(tp.customer_name_mei_kana, '')), ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 代表者検索
            OR REPLACE (tcah.daihyo_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 代表者(かな)検索
            OR REPLACE (tcah.daihyo_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (tcah.daihyo_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 担当者検索
            OR REPLACE (tcah.tanto_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 担当者(かな)検索
            OR REPLACE (tcah.tanto_name_kana, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (tcah.tanto_name_kana, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 名簿ID検索
            OR tac.customer_id LIKE /* @infix(statementListSearchCondition.searchWord) */''

            -- 案件名検索、案件ID検索
            OR REPLACE (ta.anken_name, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR REPLACE (ta.anken_name, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.searchWord) */''
            OR ta.anken_id LIKE /* @infix(statementListSearchCondition.searchWord) */''

      ) AS exists_t
    WHERE
      exists_t.anken_id = tac.anken_id
      AND exists_t.customer_id = tac.customer_id 
  )
/*%end*/

  -- 請求書番号に部分一致する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementNo) */
  AND tas.statement_no LIKE /* @infix(statementListSearchCondition.statementNo, '\') */''
/*%end*/

  -- 発行ステータスが該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementIssueStatus) */
  AND tas.statement_issue_status = /* statementListSearchCondition.statementIssueStatus */'' 
/*%end*/

  -- 精算状況が該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementRefundStatus) */
  AND tas.statement_refund_status = /* statementListSearchCondition.statementRefundStatus*/'' 
/*%end*/

  -- 精算日が該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementDateFrom) */
  AND statement_date >= /*statementListSearchCondition.statementDateFrom*/'' 
/*%end */
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementDateTo) */
  AND statement_date <= /*statementListSearchCondition.statementDateTo*/'' 
/*%end */

  -- 精算額が該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementAmountFrom) */
  AND statement_amount >= /*statementListSearchCondition.statementAmountFrom*/'' 
/*%end */
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementAmountTo) */
  AND statement_amount <= /*statementListSearchCondition.statementAmountTo*/'' 
/*%end */

  -- 期日が該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.refundDateFrom) */
  AND refund_date >= /*statementListSearchCondition.refundDateFrom*/'' 
/*%end */
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.refundDateTo) */
  AND refund_date <= /*statementListSearchCondition.refundDateTo*/'' 
/*%end */

  -- メモが該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.statementMemo) */
  AND (
    REPLACE (tas.statement_memo, '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.statementMemo) */''
    OR REPLACE (tas.statement_memo, ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(statementListSearchCondition.statementMemo) */''
  )
/*%end*/

  -- 売上計上先が該当する条件を追加
/*%if statementListSearchCondition.isNotEmpty(statementListSearchCondition.salesOwner) */
  AND tas.sales_account_seq = /* statementListSearchCondition.salesOwner */''
/*%end*/

ORDER BY
  1 = 1
  -- 名簿IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@PERSON_ID.equalsByCode(statementListSortCondition.sortItem.cd) */
  , CAST(tp.person_id AS SIGNED) /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 名前（かな）でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@PERSON_NAME.equalsByCode(statementListSortCondition.sortItem.cd) */
  , tp.customer_name_sei_kana /*%if statementListSortCondition.isDesc() */ DESC /*%end */
  , tp.customer_name_mei_kana /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 案件IDでのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@ANKEN_ID.equalsByCode(statementListSortCondition.sortItem.cd) */
  , ta.anken_id /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 案件名でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@ANKEN_NAME.equalsByCode(statementListSortCondition.sortItem.cd) */
  , ta.anken_name /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 発行ステータスでのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@STATEMENT_ISSUE_STATUS.equalsByCode(statementListSortCondition.sortItem.cd) */
  , CAST(tas.statement_issue_status AS SIGNED) /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 精算状況でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@STATEMENT_REFUND_STATUS.equalsByCode(statementListSortCondition.sortItem.cd) */
  , CAST(tas.statement_refund_status AS SIGNED) /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 精算日でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@STATEMENT_DATE.equalsByCode(statementListSortCondition.sortItem.cd) */
  , statement_date /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 期日でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@REFUND_DATE.equalsByCode(statementListSortCondition.sortItem.cd) */
  , refund_date /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 精算額でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@STATEMENT_AMOUNT.equalsByCode(statementListSortCondition.sortItem.cd) */
  , statement_amount /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
  -- 精算番号でのソート
/*%if @jp.loioz.common.constant.SortConstant$StatementListSortItem@STATEMENT_NO.equalsByCode(statementListSortCondition.sortItem.cd) */
  -- 文字列の数値が、「1,11,12,2,22」ではなく「1,2,3,4,10,20」と並ぶように文字数でのソートを先に入れる
  , CHAR_LENGTH(statement_no) /*%if statementListSortCondition.isDesc() */ DESC /*%end */
  , statement_no /*%if statementListSortCondition.isDesc() */ DESC /*%end */
/*%end */
;
