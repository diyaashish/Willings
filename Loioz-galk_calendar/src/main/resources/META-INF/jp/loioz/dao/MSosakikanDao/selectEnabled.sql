SELECT
	*
FROM
	m_sosakikan
WHERE 
  deleted_at IS NULL
  AND deleted_by IS NULL;