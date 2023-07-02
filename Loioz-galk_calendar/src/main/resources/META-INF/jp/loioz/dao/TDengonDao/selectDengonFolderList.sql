SELECT
  td.dengon_seq
  , td.gyomu_history_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS send_account_name
  , td.receive_account_seq AS receive_account_seq_list
  , tdas.dengon_status_id
  , tdas.open_flg
  , tdas.important_flg
  , DATE_FORMAT(tdas.created_at, '%Y/%m/%d %H:%i') AS created_at
FROM
  t_dengon td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
  INNER JOIN m_account AS ma
    ON ma.account_seq = td.send_account_seq
  INNER JOIN t_dengon_account_status AS tdas
    ON tdas.dengon_seq = td.dengon_seq
  INNER JOIN t_dengon_folder_in AS tdfi
    ON tdfi.dengon_seq = td.dengon_seq
WHERE
  tdfi.dengon_folder_seq = /* dengonFolderSeq */null
  AND tdas.account_seq = /* loginAccountSeq */0
  AND td.draft_flg = '0'
  AND td.deleted_at IS NULL
  AND td.deleted_by IS NULL
ORDER BY
  tdas.created_at DESC
LIMIT
  /* 50 * page */0, 50;