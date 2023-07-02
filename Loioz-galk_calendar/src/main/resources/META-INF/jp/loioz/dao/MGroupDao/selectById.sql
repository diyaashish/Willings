SELECT
  *
FROM
  m_group
WHERE
  m_group.group_id IN /* groupIdList */(null)
  AND m_group.deleted_at IS NULL
  AND m_group.deleted_by IS NULL
ORDER BY
  m_group.disp_order;