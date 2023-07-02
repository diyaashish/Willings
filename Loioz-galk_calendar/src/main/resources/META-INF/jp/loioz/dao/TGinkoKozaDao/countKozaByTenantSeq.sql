SELECT
  COUNT(TGK.ginko_account_seq)
FROM
  t_ginko_koza TGK
  INNER JOIN m_account MA
  ON MA.tenant_seq = TGK.tenant_seq
  AND MA.account_seq = /* accountSeq */null
WHERE
  TGK.tenant_seq IS NOT NULL
  AND TGK.deleted_at IS NULL
  AND TGK.deleted_by IS NULL