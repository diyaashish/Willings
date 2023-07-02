SELECT
  ta.anken_id
  ,ta.anken_name
  , group_concat( 
    TRIM( 
      CONCAT( 
        COALESCE(tc.customer_name_sei, '')
        , ' '
        , COALESCE(tc.customer_name_mei, '')
      )
    ) SEPARATOR '、'
  ) AS customer_name 
FROM
  t_anken ta 
  INNER JOIN t_anken_customer tac 
    ON ta.anken_id = tac.anken_id 
  INNER JOIN t_person tc 
    ON tac.customer_id = tc.customer_id 
WHERE
  ta.anken_id = /* ankenId */1
GROUP BY
  ta.anken_id
  , ta.anken_name 
;