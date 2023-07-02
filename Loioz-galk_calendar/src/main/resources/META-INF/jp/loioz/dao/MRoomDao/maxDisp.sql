SELECT
  MAX(disp_order)
FROM
  m_room 
WHERE
  deleted_at IS NULL 
  AND deleted_by IS NULL
;