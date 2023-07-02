SELECT
  td.dengon_seq
  , td.gyomu_history_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  , td.receive_account_seq AS receive_account_seq_list
  , DATE_FORMAT(td.updated_at, '%Y/%m/%d %H:%i') AS created_at
  , td.draft_flg
FROM
  t_dengon td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
WHERE
  td.send_account_seq = /* loginAccountSeq */0
  AND td.draft_flg = '1'
  AND td.send_trashed_flg = '0'
  AND td.deleted_at IS NULL
  AND td.deleted_by IS NULL
ORDER BY
  created_at DESC
LIMIT
  /* 50 * page */0, 50;