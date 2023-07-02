-- ファイル名とフォルダパスから、ファイル構成管理IDを取得する
SELECT
    tfcm.file_configuration_management_id
FROM
    t_file_configuration_management tfcm
    INNER JOIN t_file_detail_info_management tfdim
        ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
        AND tfdim.file_name = /* fileName */null
        /*%if fileExtension == null */
        AND tfdim.file_extension IS NULL
        /*%else */
        AND tfdim.file_extension = /* fileExtension */null
        /*%end */
        AND tfdim.folder_path = /* folderPath */null
        AND tfdim.trash_box_flg = '0'
        AND tfdim.deleted_at IS NULL
WHERE
    tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
