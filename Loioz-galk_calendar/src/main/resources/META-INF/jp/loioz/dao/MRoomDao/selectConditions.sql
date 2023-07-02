SELECT
  *
FROM
  m_room mr
WHERE
  1 = 1
  AND mr.deleted_at IS NULL
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.roomName) */
  AND mr.room_name = /* searchForm.roomName */null
/*%end */
ORDER BY
  mr.disp_order,
  mr.updated_at desc
;