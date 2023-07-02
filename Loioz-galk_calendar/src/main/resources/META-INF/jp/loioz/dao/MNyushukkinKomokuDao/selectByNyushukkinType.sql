SELECT
  * 
FROM
  m_nyushukkin_komoku 
WHERE
  nyushukkin_type = /* nyushukkinType */null 
  AND deleted_at IS NULL 
  AND deleted_by IS NULL 
ORDER BY
  disp_order ASC;
