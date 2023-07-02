SELECT
  td.dengon_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  , td.body
  , null AS send_account_name
  , td.receive_account_seq AS receive_account_seq_list
  , td.draft_flg
  , td.send_trashed_flg
  , td.gyomu_history_seq
  , DATE_FORMAT(td.created_at, '%Y/%m/%d %H:%i') AS created_at
FROM
  t_dengon AS td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
WHERE
  td.dengon_seq = /* dengonSeq */0
;
