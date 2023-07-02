SELECT
  tad.anken_id
  , ta.anken_type
  , ta.anken_name
  , ta.bunya_id
  , mb.bunya_name
  , tac.anken_status
  , tp.person_id
  , tp.customer_name_sei AS person_name_sei
  , tp.customer_name_mei AS person_name_mei
  , tp.customer_name_sei_kana AS person_name_sei_kana
  , tp.customer_name_mei_kana AS person_name_mei_kana
  , tp.customer_flg
  , tp.advisor_flg
  , tp.customer_type
  , tas.statement_seq
  , tas.sales_account_seq
  , ma.account_name_sei AS sales_account_name_sei
  , ma.account_name_mei AS sales_account_name_mei
  , ma.account_name_sei_kana AS sales_account_name_sei_kana
  , ma.account_name_mei_kana AS sales_account_name_mei_kana
  , tas.statement_issue_status
  , tas.statement_refund_status
  , tas.deposit_detail_attach_flg
  , tas.statement_memo
  , tas.sales_date
  , tas.statement_title
  , tas.statement_date
  , tas.statement_no
  , tas.statement_to_name
  , tas.statement_to_name_end
  , tas.statement_to_detail
  , tas.statement_from_tenant_name
  , tas.statement_from_detail
  , tas.tenant_stamp_print_flg
  , tas.statement_sub_text
  , tas.statement_subject
  , tas.refund_date
  , tas.refund_date_print_flg
  , tas.refund_bank_detail
  , tas.statement_remarks
  , tas.refund_to_person_id
  , tpr.customer_name_sei AS refund_to_person_name_sei
  , tpr.customer_name_mei AS refund_to_person_name_mei
  , tpr.customer_name_sei_kana AS refund_to_person_name_sei_kana
  , tpr.customer_name_mei_kana AS refund_to_person_name_mei_kana 
FROM
  t_accg_statement tas  
  INNER JOIN t_accg_doc tad 
    ON tas.accg_doc_seq = tad.accg_doc_seq 
  INNER JOIN t_anken_customer tac 
    ON tad.anken_id = tac.anken_id 
    AND tad.person_id = tac.customer_id 
  INNER JOIN t_anken ta 
    ON tad.anken_id = ta.anken_id 
  LEFT JOIN m_bunya mb 
    ON mb.bunya_id = ta.bunya_id 
  INNER JOIN t_person tp 
    ON tp.person_id = tad.person_id 
  LEFT JOIN t_person tpr 
    ON tpr.person_id = tas.refund_to_person_id
  LEFT JOIN m_account ma 
    ON ma.account_seq = tas.sales_account_seq 
WHERE
  tas.accg_doc_seq = /* accgDocSeq */NULL
;