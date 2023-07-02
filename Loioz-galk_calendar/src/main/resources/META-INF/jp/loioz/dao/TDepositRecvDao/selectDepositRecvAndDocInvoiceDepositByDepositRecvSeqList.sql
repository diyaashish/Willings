SELECT
  tdr.deposit_recv_seq
  , tdr.using_accg_doc_seq
  , tdr.created_type
  , tdr.expense_invoice_flg
  , tdr.deposit_date
  , tdr.deposit_item_name
  , tdr.deposit_amount
  , tdr.withdrawal_amount
  , tdr.deposit_type
  , tdr.sum_text
  , tdr.deposit_recv_memo
  , tdr.tenant_bear_flg
  , mapping.doc_invoice_deposit_seq
  , tadid.doc_invoice_seq
FROM
  t_deposit_recv tdr
  LEFT JOIN t_accg_doc_invoice_deposit_t_deposit_recv_mapping mapping
    ON tdr.deposit_recv_seq = mapping.deposit_recv_seq
  LEFT JOIN t_accg_doc_invoice_deposit tadid
    ON mapping.doc_invoice_deposit_seq = tadid.doc_invoice_deposit_seq
WHERE
  tdr.deposit_recv_seq IN /* depositRecvSeqList */(null)
;