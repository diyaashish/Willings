SELECT
  tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tac.anken_status
  , tac.junin_date
  , tkk.kaikei_kiroku_seq
  , tkk.hoshu_komoku_id
  , tkk.nyushukkin_type
  , tkk.nyukin_gaku
  , tkk.shukkin_gaku
  , tkk.tax_gaku
  , tkk.tax_rate
  , tkk.tax_flg
  , tkk.gensenchoshu_flg
  , tkk.gensenchoshu_gaku 
  /** 最終入金日 */
  , ( 
    SELECT
      MAX(hassei_date) 
    FROM
      t_kaikei_kiroku 
    WHERE
      anken_id = ta.anken_id 
      AND customer_id = tc.customer_id 
      AND nyushukkin_type = '1' 
    GROUP BY
      customer_id
      , anken_id
  ) AS last_nyukin_date 
FROM
  t_person tc 
  INNER JOIN t_anken_customer tac 
    USING (customer_id) 
  INNER JOIN t_anken ta 
    USING (anken_id) 
  LEFT JOIN t_kaikei_kiroku tkk 
    ON ta.anken_id = tkk.anken_id 
    AND tc.customer_id = tkk.customer_id 
 WHERE
  tc.customer_id IN /*customerIdList*/(1) 
ORDER BY
  tc.customer_id DESC
  , ta.anken_id
  , tkk.kaikei_kiroku_seq