SELECT
  *
FROM
  t_ginko_koza
WHERE
  account_seq IN /* accountSeqList */(null)
  AND deleted_at IS NULL
  AND deleted_by IS NULL
ORDER BY
  ginko_account_seq;