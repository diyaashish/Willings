-- 対象フォルダ配下の全てのファイル・フォルダのファイル詳細情報を取得する
SELECT
    tfdim_child.*
FROM
    t_file_detail_info_management tfdim_child
    INNER JOIN t_file_configuration_management tfcm
        ON tfcm.file_detail_info_management_id = tfdim_child.file_detail_info_management_id
    INNER JOIN t_root_folder_related_info_management trfrim
        ON trfrim.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
        AND tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
WHERE
    tfdim_child.folder_path like /* currentFolderPath */null
    AND tfdim_child.trash_box_flg = /* trashBoxFlg */null
/*%if @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() == trashBoxFlg */
    AND tfdim_child.deleted_operation_account_seq is not null
/*%end*/
    AND tfdim_child.deleted_at IS NULL
;