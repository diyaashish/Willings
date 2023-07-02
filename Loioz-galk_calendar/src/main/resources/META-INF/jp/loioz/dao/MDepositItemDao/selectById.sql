SELECT
  * 
FROM
  m_deposit_item 
WHERE
  deposit_item_seq IN /* depositItemSeqList */(null)
ORDER BY
  disp_order ASC
; 