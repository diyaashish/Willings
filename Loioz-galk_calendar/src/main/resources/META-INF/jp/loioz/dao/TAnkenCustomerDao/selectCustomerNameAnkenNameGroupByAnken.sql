SELECT
  ta.anken_id
  , ta.anken_name
  , group_concat( 
    TRIM( 
      CONCAT( 
        COALESCE(tc.customer_name_sei, '')
        , COALESCE(tc.customer_name_mei, '')
      )
    ) SEPARATOR '、'
  ) AS customer_name 
  , group_concat( 
    TRIM( 
      CONCAT( 
        COALESCE(tc.customer_name_sei_kana, '')
        , COALESCE(tc.customer_name_mei_kana, '')
      )
    ) SEPARATOR '、'
  ) AS customer_name_kana 
  , MIN(tac.anken_status) AS anken_status
  , CASE 
    WHEN tat.account_seq 
      THEN '1' 
    ELSE '0' 
    END AS tanto_anken 
FROM
  t_anken ta 
  INNER JOIN t_anken_customer tac 
    ON ta.anken_id = tac.anken_id 
  INNER JOIN t_person tc 
    ON tac.customer_id = tc.customer_id
  LEFT JOIN t_anken_tanto tat
    ON ta.anken_id = tat.anken_id
    AND tat.tanto_type != 1
    AND tat.account_seq = /* accountSeq */1
WHERE
  ta.anken_id IN ( 
    SELECT
      ta_2.anken_id 
    FROM
      t_anken ta_2 
      INNER JOIN t_anken_customer tac_2 
        ON ta_2.anken_id = tac_2.anken_id 
      INNER JOIN t_person tc_2 
        ON tac_2.customer_id = tc_2.customer_id 
    WHERE
      ta_2.anken_name LIKE /* @infix(searchWord) */null 
      /** 顧客名 */
      OR REPLACE ( CONCAT( COALESCE(tc_2.customer_name_sei, ''), COALESCE(tc_2.customer_name_mei, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchWord) */null 
      OR REPLACE ( CONCAT( COALESCE(tc_2.customer_name_sei, ''), COALESCE(tc_2.customer_name_mei, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchWord) */null
      /** 顧客名かな */
      OR REPLACE ( CONCAT( COALESCE(tc_2.customer_name_sei_kana, ''), COALESCE(tc_2.customer_name_mei_kana, '')) , '　', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchWord) */null 
      OR REPLACE ( CONCAT( COALESCE(tc_2.customer_name_sei_kana, ''), COALESCE(tc_2.customer_name_mei_kana, '')) , ' ', '') COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchWord) */null
    GROUP BY
      ta_2.anken_id
      , ta_2.anken_name
  ) 
GROUP BY
  ta.anken_id
  , ta.anken_name 
ORDER BY
  ta.anken_id DESC 
LIMIT
  20
;