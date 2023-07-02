SELECT
  tdf.dengon_folder_seq
  , tdf.dengon_folder_name
  , tdf.parent_dengon_folder_seq
  , tdf.trashed_flg
  , tdf.version_no
FROM
  t_dengon_folder AS tdf
WHERE
  tdf.account_seq = /* loginAccountSeq */0
  AND tdf.dengon_folder_seq = /* dengonFolderSeq */null
  AND tdf.deleted_at IS NULL
  AND tdf.deleted_by IS NULL;