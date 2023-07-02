SELECT
  ta.anken_id
  , tc.person_id
  , tc.customer_flg
  , tc.advisor_flg
  , tc.customer_type
  , TRIM( 
      CONCAT( 
        COALESCE(tc.customer_name_sei, '')
        , ' '
        , COALESCE(tc.customer_name_mei, '')
      )
    ) AS customer_name
  , tac.anken_status 
FROM
  t_anken ta 
  INNER JOIN t_anken_customer tac 
    ON ta.anken_id = tac.anken_id 
  INNER JOIN t_person tc 
    ON tac.customer_id = tc.customer_id 
WHERE
  ta.anken_id IN /* ankenId */(null);
;