SELECT
  *
FROM
  m_sosakikan
WHERE
  sosakikan_id = /* id */0
  AND deleted_at IS NULL
  AND deleted_by IS NULL;