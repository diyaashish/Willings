SELECT
    tfcm.file_configuration_management_id
    , trfrim.anken_id
    , tfdim.file_name
    , tfdim.file_extension
    , tfcm.file_kubun
    , tfdim.file_type
    , tfpim.view_limit
FROM
    t_file_configuration_management tfcm
    INNER JOIN t_root_folder_related_info_management trfrim
        ON trfrim.root_folder_related_info_management_id = tfcm.root_folder_related_info_management_id
    INNER JOIN t_file_detail_info_management tfdim
        ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id
    LEFT OUTER JOIN t_folder_permission_info_management tfpim
        ON tfcm.file_configuration_management_id = tfpim.file_configuration_management_id
WHERE
    tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null
    AND tfdim.deleted_at IS NULL
