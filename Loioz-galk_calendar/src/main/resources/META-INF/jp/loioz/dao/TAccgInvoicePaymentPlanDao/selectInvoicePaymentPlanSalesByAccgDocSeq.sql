SELECT
  tai.invoice_seq
  , tai.sales_detail_seq
  , COALESCE(ts.sales_amount_expect, 0) sales_amount_expect
  , COALESCE(ts.sales_amount_result, 0) sales_amount_result
  , taipp.payment_schedule_date
  , COALESCE(taipp.payment_schedule_amount, 0) payment_schedule_amount
  , taipp.sum_text
  , tad.person_id
  , tad.anken_id
FROM
  t_accg_invoice tai
  INNER JOIN t_accg_invoice_payment_plan taipp
    ON tai.invoice_seq = taipp.invoice_seq
  INNER JOIN t_accg_doc tad
    ON tad.accg_doc_seq = tai.accg_doc_seq
  LEFT JOIN t_sales_detail tsd
    ON tsd.sales_detail_seq = tai.sales_detail_seq
  LEFT JOIN t_sales ts
    ON ts.sales_seq = tsd.sales_seq
WHERE
  tai.accg_doc_seq = /* accgDocSeq */null
;