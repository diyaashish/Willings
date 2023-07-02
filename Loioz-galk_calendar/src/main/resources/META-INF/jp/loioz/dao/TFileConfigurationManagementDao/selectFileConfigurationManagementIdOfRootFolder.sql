-- ルートフォルダのファイル構成管理IDを取得する
SELECT
    tfcm.file_configuration_management_id
FROM
    t_file_detail_info_management tfdim         -- ファイル詳細情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id
        AND tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
        AND tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
WHERE
    tfdim.folder_path IS NULL
    AND tfdim.deleted_at IS NULL
