SELECT
  *
FROM
  m_group mg
WHERE
  mg.disp_order = /* dispOrder */0
/*%if groupId != null */
  AND mg.group_id <> /* groupId */0
/*%end */
  AND mg.deleted_at IS NULL
  AND mg.deleted_by IS NULL
ORDER BY
  mg.disp_order;