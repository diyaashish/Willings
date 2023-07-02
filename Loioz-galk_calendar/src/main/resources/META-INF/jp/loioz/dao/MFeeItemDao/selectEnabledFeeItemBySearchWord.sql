SELECT
  * 
FROM
  m_fee_item 
WHERE
  1 = 1
  /*%if searchWord != null */
  AND fee_item_name COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchWord) */null
  /*%end */
ORDER BY
  disp_order ASC
; 