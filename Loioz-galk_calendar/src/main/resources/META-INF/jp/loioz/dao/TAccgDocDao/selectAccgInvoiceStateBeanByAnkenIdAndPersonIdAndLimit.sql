SELECT
  tad.accg_doc_seq
  , tad.person_id
  , tad.anken_id
  , tad.accg_doc_type
  , tai.invoice_seq
  , tai.invoice_issue_status
  , tai.invoice_payment_status
  , tai.invoice_no
  , tai.invoice_date
  , tai.due_date
  , tai.invoice_amount
  , tai.invoice_type
  , tai.invoice_memo
  , tas.statement_seq
  , tas.statement_issue_status
  , tas.statement_refund_status
  , tas.statement_no
  , tas.statement_date
  , tas.refund_date
  , tas.statement_amount
  , tas.statement_memo
  , tar.accg_record_seq
FROM
  t_accg_doc tad 
  LEFT JOIN t_accg_invoice tai 
    ON tad.accg_doc_seq = tai.accg_doc_seq
  LEFT JOIN t_accg_statement tas 
    ON tad.accg_doc_seq = tas.accg_doc_seq
  LEFT JOIN t_accg_record tar 
    ON tad.accg_doc_seq = tar.accg_doc_seq
WHERE
  tad.anken_id = /* ankenId */NULL 
  AND tad.person_id = /* personId */NULL
ORDER BY
  CASE 
    WHEN (tad.accg_doc_type = /* @jp.loioz.common.constant.CommonConstant$AccgDocType@STATEMENT.cd */'1') THEN tas.statement_date
    WHEN (tad.accg_doc_type = /* @jp.loioz.common.constant.CommonConstant$AccgDocType@INVOICE.cd */'2') THEN  tai.invoice_date
    ELSE NULL
  END DESC
/*%if limitCount != null */
LIMIT /* limitCount */0
/*%end */
;
