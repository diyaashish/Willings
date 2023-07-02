SELECT
  * 
FROM
  m_saibansho_bu 
WHERE
  saibansho_id = /* saibanshoId */1 
  AND deleted_at IS NULL 
  AND deleted_by IS NULL;