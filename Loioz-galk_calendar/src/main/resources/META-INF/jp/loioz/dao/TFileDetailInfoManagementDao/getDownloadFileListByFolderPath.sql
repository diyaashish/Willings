-- フォルダパスに含まれるダウンロード対象のファイル一式を取得する
SELECT
    tfdim.file_detail_info_management_id
    , tfdim.file_name
    , tfdim.file_extension
    , tfdim.folder_path
    , tfdim.s3_object_key
    , tfcm.file_kubun
FROM
    t_file_detail_info_management tfdim
    INNER JOIN t_file_configuration_management tfcm
        ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id
        AND tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
        AND tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
WHERE
    tfdim.folder_path LIKE /* folderPath */null
/*%if targetSearchPathList.size() > 0 */
    AND tfdim.folder_path IN /* targetSearchPathList */(null)
/*%end */
    AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
    AND tfdim.deleted_operation_account_seq IS NULL
    AND tfdim.deleted_by IS NULL
    AND tfdim.deleted_at IS NULL
