SELECT
  tac.customer_id
  , tac.anken_id
  , ts.sales_seq
  , ts.sales_amount_expect
  , ts.sales_amount_result
  , sum(tsd.sales_amount) as sales_total_amount
  , sum(tsd.sales_tax_amount) as sales_tax_total_amount
  , sum(tsd.sales_discount_amount) as sales_discount_total_amount
  , sum(tsd.sales_discount_tax_amount) as sales_discount_tax_total_amount
  , sum(tsd.sales_withholding_amount) sales_withholding_total_amount 
FROM
  t_anken_customer tac 
  LEFT JOIN t_sales ts 
    ON tac.customer_id = ts.person_id 
    AND tac.anken_id = ts.anken_id 
  LEFT JOIN t_sales_detail tsd 
    ON tsd.sales_seq = ts.sales_seq 
WHERE
  tac.customer_id = /* personId */NULL 
  AND tac.anken_id = /* ankenId */NULL
GROUP BY
  tac.customer_id
  , tac.anken_id
  , ts.sales_seq
;
