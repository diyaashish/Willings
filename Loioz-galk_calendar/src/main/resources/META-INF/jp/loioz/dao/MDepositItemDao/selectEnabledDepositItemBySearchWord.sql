SELECT
  * 
FROM
  m_deposit_item 
WHERE
  deposit_type = /* depositType */null
  /*%if searchWord != null */
  AND deposit_item_name COLLATE utf8mb4_unicode_ci LIKE /* @infix(searchWord) */null
  /*%end */
ORDER BY
  disp_order ASC
;
