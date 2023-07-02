-- ゴミ箱に入っているファイルの中から、対象のフォルダ配下のファイル・フォルダのファイル構成管理情報を取得する
SELECT
    tfcm_child.*
FROM
    t_file_configuration_management tfcm_child
    INNER JOIN t_file_detail_info_management tfdim_child
        ON tfdim_child.file_detail_info_management_id = tfcm_child.file_detail_info_management_id
        AND tfdim_child.folder_path LIKE /* folderPath */null
        AND tfdim_child.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
        AND tfdim_child.deleted_at IS NULL
WHERE
    tfcm_child.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
    AND tfcm_child.parent_file_configuration_management_id = /* fileConfigurationManagementId */null