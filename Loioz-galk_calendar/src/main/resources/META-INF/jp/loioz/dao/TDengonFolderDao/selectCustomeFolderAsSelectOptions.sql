SELECT
  tdf.dengon_folder_seq
  , tdf.dengon_folder_name
  , tdf.parent_dengon_folder_seq
  , tdf.trashed_flg
FROM
  t_dengon_folder AS tdf
WHERE
  tdf.account_seq = /* loginAccountSeq */0
  AND tdf.deleted_at IS NULL
  AND tdf.deleted_by IS NULL
  /*%if isParentFolder != null */
    /*%if isParentFolder */
  AND tdf.parent_dengon_folder_seq IS NULL
    /*%else*/
  AND tdf.parent_dengon_folder_seq IS NOT NULL
    /*%end*/
  /*%end*/
  /*%if isTrashed != null */
    /*%if isTrashed */
  AND tdf.trashed_flg = '1'
    /*%else*/
  AND tdf.trashed_flg = '0'
    /*%end*/
  /*%end*/
ORDER BY
  CAST(tdf.dengon_folder_name AS SIGNED)
  , CHAR_LENGTH(tdf.dengon_folder_name)
  , tdf.dengon_folder_name;