SELECT
  *
FROM
  m_account
WHERE
  account_seq IN /* accountSeqList */(null)
  AND deleted_by IS NULL;