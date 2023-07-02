SELECT
  COUNT(tdac.dengon_seq)
FROM
  t_dengon_account_status tdac
WHERE
  tdac.account_seq = /* loginAccountSeq */0
  AND tdac.open_flg = 0
  AND tdac.trashed_flg = 0
  AND tdac.deleted_at IS NULL
  AND tdac.deleted_by IS NULL
  AND tdac.dengon_seq NOT IN (
    SELECT
      tdfi.dengon_seq
    FROM
      t_dengon_folder tdf
      INNER JOIN t_dengon_folder_in tdfi
        USING (dengon_folder_seq)
    WHERE
      tdf.account_seq = /* loginAccountSeq */0
      AND tdf.deleted_at IS NULL
      AND tdf.deleted_by IS NULL
  );