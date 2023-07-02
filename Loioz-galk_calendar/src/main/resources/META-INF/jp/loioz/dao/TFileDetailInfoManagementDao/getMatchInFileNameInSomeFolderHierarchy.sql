-- 同フォルダ階層内でアップロード対象のファイル名・フォルダ名に一致するレコード件数を取得する
SELECT
  tfcm.file_configuration_management_id
  , tfdim.file_detail_info_management_id
  , tfcm.root_folder_related_info_management_id
  , tfcm.file_kubun
  , tfdim.file_name
  , tfdim.file_extension
  , tfdim.file_type 
FROM
  t_file_detail_info_management tfdim 
  INNER JOIN t_file_configuration_management tfcm 
    ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id 
    AND tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */NULL 
WHERE
  1 = 1 
-- ファイル名（フォルダ名）+拡張子が同じものを抽出。フォルダ名は拡張子をテキストファイルのように別カラムにしないため 〇〇.txt のようなフォルダ名にも該当するように結合する
  AND CONCAT( 
    COALESCE(tfdim.file_name, '')
    , COALESCE(tfdim.file_extension, '')
  ) = CONCAT( 
    COALESCE(/* fileName */NULL, '')
    , COALESCE(/* fileExtension */NULL, '')
  ) 
  AND tfdim.folder_path = /* folderPath */NULL 
  AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0' 
  AND tfdim.deleted_operation_account_seq IS NULL 
  AND tfdim.deleted_at IS NULL
;
