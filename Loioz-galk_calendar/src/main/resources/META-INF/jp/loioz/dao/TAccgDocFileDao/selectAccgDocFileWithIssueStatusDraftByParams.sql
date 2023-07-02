SELECT
  tadf.*
FROM
  t_accg_doc_file tadf
  LEFT JOIN t_accg_invoice tai
    ON tadf.accg_doc_seq = tai.accg_doc_seq
  LEFT JOIN t_accg_statement tas
    ON tadf.accg_doc_seq = tas.accg_doc_seq
WHERE
  tadf.accg_doc_seq IN /* accgDocSeqList */(1)
  AND tadf.accg_doc_file_type = /* accgDocFileTypeCd */'1'
  AND COALESCE(tai.invoice_issue_status, tas.statement_issue_status) = /* @jp.loioz.common.constant.CommonConstant$IssueStatus@DRAFT */'1'
;
