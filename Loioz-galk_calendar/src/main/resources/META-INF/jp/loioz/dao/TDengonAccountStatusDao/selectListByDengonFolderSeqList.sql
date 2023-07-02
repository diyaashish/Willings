SELECT
  *
FROM
  t_dengon_account_status tdas
WHERE
  tdas.account_seq = /* loginAccountSeq */0
  AND tdas.dengon_seq IN (
    SELECT
      tdfi.dengon_seq
    FROM
      t_dengon_folder_in AS tdfi
    WHERE
      tdfi.dengon_folder_seq IN /* dengonFolderSeqList */(null)
  );