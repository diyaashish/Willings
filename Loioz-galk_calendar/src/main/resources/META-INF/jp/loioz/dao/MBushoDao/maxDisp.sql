SELECT
  MAX(disp_order)
FROM
  m_busho 
WHERE
  deleted_at IS NULL 
  AND deleted_by IS NULL
;