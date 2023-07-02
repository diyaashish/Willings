SELECT
  * 
FROM
  m_fee_item 
WHERE
  fee_item_seq IN /* feeItemSeqList */(null)
ORDER BY
  disp_order ASC
; 