SELECT
  td.dengon_seq
  , td.gyomu_history_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  , CONCAT(
    COALESCE(ma.account_name_sei, '')
    , ' '
    , COALESCE(ma.account_name_mei, '')
  ) AS send_account_name
  , td.receive_account_seq AS receive_account_seq_list
  , null AS draft_flg
  , tdas.dengon_status_id
  , tdas.open_flg
  , tdas.important_flg
  , DATE_FORMAT(tdas.created_at, '%Y/%m/%d %H:%i') AS created_at
  , DATE_FORMAT(tdas.updated_at, '%Y/%m/%d %H:%i') AS updated_at
FROM
  t_dengon AS td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
  INNER JOIN t_dengon_account_status AS tdas
    ON td.dengon_seq = tdas.dengon_seq
  INNER JOIN m_account AS ma
    ON ma.account_seq = td.send_account_seq
  INNER JOIN t_dengon_folder_in AS tdfi
    ON tdfi.dengon_seq = td.dengon_seq
WHERE
  tdas.account_seq = /* loginAccountSeq */0
  AND tdfi.dengon_folder_seq = /* dengonFolderSeq */0
  AND tdas.trashed_flg = '1'
  AND tdas.deleted_at IS NULL
  AND tdas.deleted_by IS NULL
ORDER BY 
  tdas.created_at DESC
LIMIT
  /* 50 * page */0 
  ,50;
