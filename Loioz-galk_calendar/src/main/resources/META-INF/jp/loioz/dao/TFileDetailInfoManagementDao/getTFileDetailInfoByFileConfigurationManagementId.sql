-- ファイル構成管理IDからファイル詳細情報を取得する
SELECT
    tfdim.*
FROM
    t_file_configuration_management tfcm
    INNER JOIN t_file_detail_info_management tfdim
        USING(file_detail_info_management_id)
where
    tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null
    AND tfdim.deleted_at IS NULL
