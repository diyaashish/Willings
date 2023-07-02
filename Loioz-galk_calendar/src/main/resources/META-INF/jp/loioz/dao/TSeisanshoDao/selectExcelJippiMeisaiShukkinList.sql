SELECT
  tkk.hassei_date
  , tkk.shukkin_gaku
  , tkk.tax_gaku
  , tkk.tax_rate
  , tkk.tekiyo
  , mnk.komoku_name
  , mnk.tax_flg
FROM
  t_kaikei_kiroku tkk
  INNER JOIN m_nyushukkin_komoku mnk
    USING (nyushukkin_komoku_id)
WHERE
  tkk.kaikei_kiroku_seq IN /* kaikeiSeqList */(1)
  AND tkk.nyushukkin_type = "2"
  AND tkk.hoshu_komoku_id IS NULL;