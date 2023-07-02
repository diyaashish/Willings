-- 遷移先のフォルダパスを取得する
SELECT
    tfdim.folder_path                           -- フォルダパス
    , tfdim.file_name                           -- ファイル名
    , tfpim.view_limit                          -- 閲覧制限
FROM
    t_file_configuration_management tfcm        -- ファイル構成管理
    INNER JOIN t_file_detail_info_management tfdim -- ファイル詳細情報管理
        using (file_detail_info_management_id)
    INNER JOIN t_folder_permission_info_management tfpim -- フォルダ権限情報管理
        using (file_configuration_management_id)
WHERE
    tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null
    AND tfdim.deleted_at IS NULL