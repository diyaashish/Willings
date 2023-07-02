SELECT
  *
FROM
  m_room
WHERE
  deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY
  disp_order;