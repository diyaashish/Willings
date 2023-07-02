SELECT
  tkk.kaikei_kiroku_seq
  , tkk.hassei_date
  , tkk.hoshu_komoku_id
  , tkk.nyushukkin_komoku_id
  , tkk.nyushukkin_type
  , tkk.nyukin_gaku
  , tkk.shukkin_gaku
  , tkk.tax_gaku
  , tkk.time_charge_time_shitei
  , tkk.time_charge_start_time
  , tkk.tax_rate
  , tkk.gensenchoshu_flg
  , tkk.gensenchoshu_gaku
  , tkk.tekiyo
  , tkk.created_by
  , tkk.seisan_date
  , tkk.nyushukkin_yotei_seq
  , tkk.seisan_seq
  , tkk.created_by
  , mnk.komoku_name
  , CASE 
    WHEN tkk.nyushukkin_komoku_id IS NOT NULL 
        THEN mnk.tax_flg 
    ELSE tkk.tax_flg 
    END AS tax_flg
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS account_name
  , tsk.seisan_id 
FROM
  t_kaikei_kiroku tkk 
  LEFT OUTER JOIN t_anken ta 
    ON tkk.anken_id = ta.anken_id 
  LEFT OUTER JOIN t_person tc 
    ON tkk.customer_id = tc.customer_id 
  LEFT OUTER JOIN m_nyushukkin_komoku mnk 
    ON tkk.nyushukkin_komoku_id = mnk.nyushukkin_komoku_id 
  LEFT OUTER JOIN m_account ma 
    ON tkk.created_by = ma.account_seq 
  LEFT OUTER JOIN t_seisan_kiroku tsk 
    ON tkk.seisan_seq = tsk.seisan_seq 
WHERE
  1 = 1
  AND tkk.customer_id = /*customerId */null 
  AND tkk.anken_id = /* ankenId */null 
  AND ( 
    tkk.pool_type = /* @jp.loioz.common.constant.CommonConstant$PoolType@POOL_SAKI */'2' 
    OR tkk.pool_type IS NULL
  ) 
ORDER BY
  tkk.hassei_date
  , tkk.kaikei_kiroku_seq ASC;
