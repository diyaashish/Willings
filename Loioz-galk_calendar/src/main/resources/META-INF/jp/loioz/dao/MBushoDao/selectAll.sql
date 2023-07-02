SELECT
  * 
FROM
  m_busho 
WHERE
  deleted_at IS NULL 
  AND deleted_by IS NULL 
ORDER BY
  disp_order ASC;