SELECT
  ta.anken_id
  -- 顧客の氏名を1人分取得
  , ( 
    SELECT
      CONCAT( 
        COALESCE(tp.customer_name_sei, '')
        , ' '
        , COALESCE(tp.customer_name_mei, '')
      ) 
    FROM
      t_person tp 
    WHERE
      tp.customer_id = ( 
        SELECT
          MIN(tac.customer_id) customer_id 
        FROM
          t_anken_customer tac 
        WHERE
          tac.anken_id = ta.anken_id 
        GROUP BY
          tac.anken_id
      )
  ) AS customer_name
  -- 顧客の合計人数を取得
  , ( 
    SELECT
      COUNT(*) 
    FROM
      t_anken_customer tac 
    WHERE
      tac.anken_id = ta.anken_id
  ) AS number_of_customer
FROM
  t_anken ta 
WHERE
  ta.anken_id IN /* ankenIdList */(1);
