SELECT
  tkk.kaikei_kiroku_seq
  , tkk.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
  , tkk.anken_id
  , ta.anken_name
  , ta.bunya_id
  , tkk.hassei_date
  , tkk.hoshu_komoku_id
  , tkk.nyushukkin_komoku_id
  , tkk.nyushukkin_type
  , tkk.nyukin_gaku
  , tkk.shukkin_gaku
  , tkk.tax_gaku
  , tkk.time_charge_tanka
  , tkk.time_charge_start_time
  , tkk.time_charge_end_time
  , tkk.tax_rate
  , tkk.tax_flg
  , tkk.gensenchoshu_flg
  , tkk.gensenchoshu_gaku
  , tkk.tekiyo
  , tkk.seisan_date
  , tac.junin_date
  , tac.jiken_kanryo_date 
  , tac.kanryo_flg
  , tac.anken_status
FROM
  t_kaikei_kiroku tkk 
  LEFT OUTER JOIN t_anken ta 
    ON tkk.anken_id = ta.anken_id 
  LEFT OUTER JOIN t_anken_customer tac 
    ON tkk.anken_id = tac.anken_id 
    AND tkk.customer_id = tac.customer_id 
  LEFT OUTER JOIN t_person tc 
    ON tkk.customer_id = tc.customer_id 
WHERE
    tkk.anken_id = /* ankenId */null; 