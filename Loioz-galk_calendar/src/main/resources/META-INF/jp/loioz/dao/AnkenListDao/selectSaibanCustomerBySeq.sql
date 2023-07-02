SELECT
  ts.saiban_seq
  , tc.person_id
  , tc.customer_id
  , COALESCE(tc.customer_name_sei, '') AS customer_name_sei
  , COALESCE(tc.customer_name_sei_kana, '') AS customer_name_sei_kana
  , COALESCE(tc.customer_name_mei, '') AS customer_name_mei
  , COALESCE(tc.customer_name_mei_kana, '') AS customer_name_mei_kana
    -- 顧客の合計人数を取得
  , ( 
    SELECT
      COUNT(*) 
    FROM
      t_saiban_customer
    WHERE
      saiban_seq = ts.saiban_seq
  ) AS number_of_customer 
FROM
  t_saiban ts 
    -- １人分の裁判顧客の名簿ID。優先順位：筆頭顧客、顧客の順。
  LEFT JOIN ( 
    SELECT
      tsc.saiban_seq
      , SUBSTRING_INDEX(GROUP_CONCAT(tc.person_id ORDER BY tsc.main_flg DESC, tc.person_id ASC SEPARATOR ','), ',', 1) person_id
    FROM
      t_saiban_customer tsc
      INNER JOIN t_person tc
        ON tsc.customer_id = tc.customer_id
    GROUP BY
      tsc.saiban_seq
    ) tsc 
    ON ts.saiban_seq = tsc.saiban_seq 
  LEFT JOIN t_person tc
    ON tc.person_id = tsc.person_id
WHERE
  ts.saiban_seq IN /* saibanSeqList */(1)
;
