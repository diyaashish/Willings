SELECT 
-- データの取得
  tdr.deposit_recv_seq
  , tac.customer_id AS person_id
  , tac.anken_id
  , tdr.deposit_item_name
  , tdr.deposit_date
  , tdr.deposit_complete_flg
  , tdr.deposit_type
  , tdr.created_type
  , tdr.expense_invoice_flg
  , COALESCE(tdr.deposit_amount, 0) AS deposit_amount
  , COALESCE(tdr.withdrawal_amount, 0) AS withdrawal_amount
  , tdr.using_accg_doc_seq
  , tai_using.invoice_seq AS using_invoice_seq
  , tai_using.invoice_no AS using_invoice_no
  , tai_using.invoice_payment_status AS using_invoice_payment_status
  , tas_using.statement_seq AS using_statement_seq
  , tas_using.statement_no AS using_statement_no
  , tas_using.statement_refund_status AS using_statement_refund_status
  , tdr.created_accg_doc_seq
  , tai_created.invoice_seq AS created_invoice_seq
  , tai_created.invoice_no AS created_invoice_no
  , tai_created.invoice_payment_status AS created_invoice_payment_status
  , tas_created.statement_seq AS created_statement_seq
  , tas_created.statement_no AS created_statement_no
  , tas_created.statement_refund_status AS created_statement_refund_status
  , tdr.tenant_bear_flg
  , tdr.sum_text
  , tdr.deposit_recv_memo
  , tdr.uncollectible_flg
FROM
  t_anken_customer tac
  INNER JOIN t_deposit_recv tdr
    ON tdr.anken_id = tac.anken_id AND tac.customer_id = tdr.person_id
  LEFT JOIN t_accg_invoice tai_using
    ON tdr.using_accg_doc_seq = tai_using.accg_doc_seq
  LEFT JOIN t_accg_statement tas_using
    ON tdr.using_accg_doc_seq = tas_using.accg_doc_seq
  LEFT JOIN t_accg_invoice tai_created
    ON tdr.created_accg_doc_seq = tai_created.accg_doc_seq
  LEFT JOIN t_accg_statement tas_created
    ON tdr.created_accg_doc_seq = tas_created.accg_doc_seq

WHERE
  1 = 1
  /*%if ankenId != null */
  AND tac.anken_id = /* ankenId */null
  /*%end */
  
  /*%if personId != null */
  AND tac.customer_id = /* personId */null
  /*%end */
  
  /*%if depositRecvSeq != null */
  AND tdr.deposit_recv_seq = /* depositRecvSeq */null
  /*%end */

ORDER BY
  1 = 1
  
/*%if depositRecvDetailSortCondition.sortItem != null */

  -- 種別でのソート
  /*%if @jp.loioz.common.constant.SortConstant$DepositRecvDetailSortItem@DEPOSIT_TYPE.equalsByCode(depositRecvDetailSortCondition.sortItem.cd) */
  , tdr.deposit_type /*%if depositRecvDetailSortCondition.isDesc() */ DESC /*%end */
  /*%end */

  -- 項目でのソート
  /*%if @jp.loioz.common.constant.SortConstant$DepositRecvDetailSortItem@ITEM.equalsByCode(depositRecvDetailSortCondition.sortItem.cd) */
  , tdr.deposit_item_name /*%if depositRecvDetailSortCondition.isDesc() */ DESC /*%end */
  /*%end */
  
  -- 発生日でのソート
  /*%if @jp.loioz.common.constant.SortConstant$DepositRecvDetailSortItem@DEPOSIT_DATE.equalsByCode(depositRecvDetailSortCondition.sortItem.cd) */
  , tdr.deposit_date /*%if depositRecvDetailSortCondition.isDesc() */ DESC /*%end */
  -- 発生日に日付が設定されていないものの中で、未完了のものが上位にくるように完了フラグを第2ソートキーにする
  , tdr.deposit_complete_flg 
  , tdr.created_at /*%if depositRecvDetailSortCondition.isDesc() */ DESC /*%end */
  /*%end */
  
  -- 入金額でのソート
  /*%if @jp.loioz.common.constant.SortConstant$DepositRecvDetailSortItem@DEPOSIT_AMOUNT.equalsByCode(depositRecvDetailSortCondition.sortItem.cd) */
  , tdr.deposit_amount /*%if depositRecvDetailSortCondition.isDesc() */ DESC /*%end */
  /*%end */
  
  -- 出金額でのソート
  /*%if @jp.loioz.common.constant.SortConstant$DepositRecvDetailSortItem@WITHDRAWAL_AMOUNT.equalsByCode(depositRecvDetailSortCondition.sortItem.cd) */
  , tdr.withdrawal_amount /*%if depositRecvDetailSortCondition.isDesc() */ DESC /*%end */
  /*%end */
  
/*%end */
;
