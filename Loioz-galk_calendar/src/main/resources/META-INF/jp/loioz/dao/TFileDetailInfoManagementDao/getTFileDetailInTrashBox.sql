-- ファイル名、フォルダパスに合致する、ゴミ箱内のファイル詳細情報を取得する
SELECT
    tfdim.*
FROM
    t_file_detail_info_management tfdim
    INNER JOIN t_file_configuration_management tfcm
        ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id
        AND tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
        AND tfcm.parent_file_configuration_management_id = /* parentFileConfigurationManagementId */null
WHERE
    tfdim.file_name = /* fileName */null
    AND tfdim.folder_path = /* folderPath */null
    AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
    AND tfdim.deleted_at IS NULL
