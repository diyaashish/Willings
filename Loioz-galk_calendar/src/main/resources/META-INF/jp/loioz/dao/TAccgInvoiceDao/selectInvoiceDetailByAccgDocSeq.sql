SELECT
  tad.anken_id
  , ta.bunya_id
  , mb.bunya_name
  , ta.anken_type
  , ta.anken_name
  , tac.anken_status
  , tp.person_id
  , COALESCE(tp.customer_name_sei, '') person_name_sei
  , COALESCE(tp.customer_name_mei, '') person_name_mei
  , tp.customer_flg
  , tp.advisor_flg
  , tp.customer_type
  , tai.sales_account_seq
  , COALESCE(ma.account_name_sei, '') sales_account_name_sei
  , COALESCE(ma.account_name_mei, '') sales_account_name_mei
  , tai.invoice_issue_status
  , tai.deposit_detail_attach_flg
  , tai.payment_plan_attach_flg
  , tai.invoice_type
  , tai.invoice_memo
  , tai.sales_date
  , tai.invoice_title
  , tai.invoice_date
  , tai.invoice_no
  , tai.invoice_to_name
  , tai.invoice_to_name_end
  , tai.invoice_to_detail
  , tai.invoice_from_tenant_name
  , tai.invoice_from_detail
  , tai.tenant_stamp_print_flg
  , tai.invoice_sub_text
  , tai.invoice_subject
  , tai.due_date
  , tai.due_date_print_flg
  , tai.tenant_bank_detail
  , tai.invoice_remarks
  , tai.bill_to_person_id
  , COALESCE(tpb.customer_name_sei, '') bill_to_person_name_sei
  , COALESCE(tpb.customer_name_mei, '') bill_to_person_name_mei
FROM
  t_accg_invoice tai
  INNER JOIN t_accg_doc tad
    ON tai.accg_doc_seq = tad.accg_doc_seq
  INNER JOIN t_anken_customer tac
    ON tad.anken_id = tac.anken_id AND tad.person_id = tac.customer_id
  INNER JOIN t_anken ta
    ON tad.anken_id = ta.anken_id
  INNER JOIN t_person tp
    ON tp.person_id = tad.person_id
  LEFT JOIN t_person tpb
    ON tpb.person_id = tai.bill_to_person_id
  LEFT JOIN m_bunya mb
    ON mb.bunya_id = ta.bunya_id
  LEFT JOIN m_account ma
    ON ma.account_seq = tai.sales_account_seq
WHERE
  tai.accg_doc_seq = /* accgDocSeq */''
;