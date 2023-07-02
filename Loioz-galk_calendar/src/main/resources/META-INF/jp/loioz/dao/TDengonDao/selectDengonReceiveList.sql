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
  INNER JOIN m_account ma
    ON ma.account_seq = td.send_account_seq
  INNER JOIN t_dengon_account_status tdas
    ON td.dengon_seq = tdas.dengon_seq
WHERE
  td.dengon_seq NOT IN (
    SELECT
      td.dengon_seq
    FROM
      t_dengon AS td
      INNER JOIN t_dengon_folder_in AS tdfi
        ON td.dengon_seq = tdfi.dengon_seq
      INNER JOIN t_dengon_folder AS tdf
        ON tdfi.dengon_folder_seq = tdf.dengon_folder_seq
        AND tdf.account_seq =
        /* loginAccountSeq */0
  )
  AND tdas.deleted_at IS NULL
  AND tdas.deleted_by IS NULL
  AND tdas.account_seq = /* loginAccountSeq */0
  AND tdas.trashed_flg = '0'
ORDER BY
  tdas.created_at DESC
LIMIT
  /* 50 * page */0, 50;
