SELECT
  *
FROM
  t_dengon_account_status AS tdas
WHERE
  tdas.account_seq = /* accountSeq */null
  AND tdas.dengon_seq IN (
    SELECT
      tdfi.dengon_seq
    FROM
      t_dengon_folder_in AS tdfi
    WHERE
      tdfi.dengon_folder_seq = /* dengonFolderSeq */null
  );