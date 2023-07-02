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
  , tas.statement_seq
  , tas.statement_issue_status
  , tas.statement_refund_status
  , tas.statement_no
  , tas.statement_date
  , tas.refund_date
  , tas.statement_amount
FROM
  t_accg_doc tad 
  LEFT JOIN t_accg_invoice tai 
    ON tad.accg_doc_seq = tai.accg_doc_seq
  LEFT JOIN t_accg_statement tas 
    ON tad.accg_doc_seq = tas.accg_doc_seq
WHERE
  tad.accg_doc_seq = /* accgDocSeq */NULL 
;
