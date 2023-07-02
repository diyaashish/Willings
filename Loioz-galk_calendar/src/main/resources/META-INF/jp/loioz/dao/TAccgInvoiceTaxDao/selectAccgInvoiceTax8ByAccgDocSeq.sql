SELECT
  *
FROM
  t_accg_invoice_tax tait
WHERE
  tait.accg_doc_seq = /* accgDocSeq */null
  AND tait.tax_rate_type = /* @jp.loioz.common.constant.CommonConstant$TaxRate@EIGHT_PERCENT */'1'
;