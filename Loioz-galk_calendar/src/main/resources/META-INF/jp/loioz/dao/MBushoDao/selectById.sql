SELECT
  *
FROM
  m_busho mb
WHERE
  mb.busho_id IN /* bushoIdList */(null)
  AND mb.deleted_at IS NULL
  AND mb.deleted_by IS NULL
ORDER BY
  mb.disp_order
;