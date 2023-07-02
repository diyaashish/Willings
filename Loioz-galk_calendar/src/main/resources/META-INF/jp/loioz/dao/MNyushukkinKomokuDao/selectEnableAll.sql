SELECT
  *
FROM
  m_nyushukkin_komoku
WHERE
  undeletable_flg = 0
  AND deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY
  disp_order;