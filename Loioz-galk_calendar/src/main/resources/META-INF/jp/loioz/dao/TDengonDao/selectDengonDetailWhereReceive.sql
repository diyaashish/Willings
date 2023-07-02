SELECT
  td.dengon_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  , td.body
  , CONCAT(
    COALESCE(ma.account_name_sei, '')
    , ' '
    , COALESCE(ma.account_name_mei, '')
  ) AS send_account_name
  , td.receive_account_seq AS receive_account_seq_list
  , td.draft_flg
  , td.send_trashed_flg
  , tdas.dengon_status_id
  , tdas.important_flg
  , tdas.open_flg
  , tdas.trashed_flg
  , td.gyomu_history_seq
  , DATE_FORMAT(tdas.created_at, '%Y/%m/%d %H:%i') AS created_at
FROM
  t_dengon td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
  INNER JOIN t_dengon_account_status AS tdas
    ON tdas.dengon_seq = td.dengon_seq
  INNER JOIN m_account AS ma
    ON ma.account_seq = td.send_account_seq
WHERE
  td.dengon_seq = /* dengonSeq */0
  AND tdas.account_seq = /* loginAccountSeq */0
;
