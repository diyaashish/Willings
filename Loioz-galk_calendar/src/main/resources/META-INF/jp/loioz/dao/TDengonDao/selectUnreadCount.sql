SELECT
  COUNT(dengon_seq)
FROM
  t_dengon_account_status
WHERE
  account_seq = /* accountSeq */NULL
  AND open_flg = 0
  AND trashed_flg = 0
  AND deleted_at IS NULL
  AND deleted_by IS NULL;