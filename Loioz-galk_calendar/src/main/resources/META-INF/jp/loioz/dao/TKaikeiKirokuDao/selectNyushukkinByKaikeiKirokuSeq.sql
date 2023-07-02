SELECT
  tkk.kaikei_kiroku_seq
  , tkk.customer_id
  , tkk.anken_id
  , tkk.hassei_date
  , tkk.nyushukkin_type
  , tkk.nyushukkin_komoku_id
  , tkk.nyukin_gaku
  , tkk.shukkin_gaku
  , tkk.tekiyo
  , tkk.pool_type
  , tkk.seisan_seq
  , CASE 
      WHEN tkk.nyushukkin_komoku_id IS NOT NULL 
          THEN mnk.tax_flg 
      ELSE tkk.tax_flg 
      END AS tax_flg
  , tkk.nyushukkin_yotei_seq
  , tny.seisan_seq AS yotei_seisan_seq
  , tkk.last_edit_at
  , tkk.last_edit_by
  , tkk.created_at
  , tkk.created_by
FROM
  t_kaikei_kiroku tkk 
  LEFT OUTER JOIN m_nyushukkin_komoku mnk 
    ON tkk.nyushukkin_komoku_id = mnk.nyushukkin_komoku_id
  LEFT OUTER JOIN t_nyushukkin_yotei tny
    ON tkk.nyushukkin_yotei_seq = tny.nyushukkin_yotei_seq
WHERE
  tkk.kaikei_kiroku_seq = /* kaikeiKirokuSeq */null;