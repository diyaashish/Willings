SELECT
  ms.saibansho_id
  , ms.todofuken_id
  , ms.saibansho_name
  , msb.keizoku_bu_id
  , msb.keizoku_bu_name
  , msb.keizoku_bu_tel_no
  , msb.keizoku_bu_fax_no
  , msb.created_at
  , msb.updated_at
  , msb.deleted_at
  , msb.version_no
FROM
  m_saibansho ms
  INNER JOIN m_saibansho_bu msb
    ON ms.saibansho_id = msb.saibansho_id
WHERE
  1 = 1
  AND ms.deleted_at IS NULL
  AND msb.deleted_at IS NULL
  AND ms.saibansho_id = /* saibanshoId */null
;