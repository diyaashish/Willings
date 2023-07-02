SELECT
  *
FROM
  m_room
WHERE
  room_id IN /* roomIdList */(null)
  AND deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY
  disp_order;
