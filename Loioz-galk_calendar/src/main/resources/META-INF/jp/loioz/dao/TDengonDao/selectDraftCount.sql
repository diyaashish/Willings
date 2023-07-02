SELECT
  COUNT(*)
FROM
  t_dengon td
WHERE
  td.send_account_seq = /* loginAccountSeq */0
  AND td.draft_flg = 1
  AND td.send_trashed_flg = 0
  AND td.deleted_at IS NULL
;