SELECT 
  tsd.*
FROM
  t_sales ts
  INNER JOIN t_sales_detail tsd
    ON ts.sales_seq = tsd.sales_seq
WHERE
  ts.person_id = /* personId */null
  AND ts.anken_id = /* ankenId */null
;
