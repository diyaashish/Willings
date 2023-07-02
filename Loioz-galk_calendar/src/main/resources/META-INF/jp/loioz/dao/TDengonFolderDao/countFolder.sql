SELECT
  COUNT(*)
FROM
  t_dengon_folder
WHERE
  account_seq = /* loginAccountSeq */0
  /*%if isParentFolder */
  AND parent_dengon_folder_seq IS NULL
  /*%else*/
  AND parent_dengon_folder_seq IS NOT NULL
  /*%end*/
  AND deleted_at IS NULL
  AND deleted_by IS NULL;