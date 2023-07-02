-- ファイル構成管理IDからルートフォルダ情報を取得する
SELECT
    tfcm.root_folder_related_info_management_id -- ルートフォルダ関連情報管理ID
    , trfrim.anken_id
    , ta.anken_name
    , trfrim.customer_id
    , TRIM(
        CONCAT(
            COALESCE(tc.customer_name_sei, '')
            , ' '
            , COALESCE(tc.customer_name_mei, '')
        )
    ) as customer_name
    , tfdim.file_name                           -- ファイル名
FROM
    t_file_configuration_management tfcm        -- ファイル構成管理
    INNER JOIN t_file_configuration_management root_tfcm -- ファイル構成管理（ルートフォルダ）
        ON root_tfcm.root_folder_related_info_management_id = tfcm.root_folder_related_info_management_id
        AND root_tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
    INNER JOIN t_file_detail_info_management tfdim -- ファイル詳細情報管理
        ON tfdim.file_detail_info_management_id = root_tfcm.file_detail_info_management_id
    INNER JOIN t_root_folder_related_info_management trfrim
        ON root_tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
    LEFT OUTER JOIN t_anken ta                  -- 案件情報
        ON trfrim.anken_id = ta.anken_id
    LEFT OUTER JOIN t_person tc               -- 名簿情報
        ON trfrim.customer_id = tc.customer_id
WHERE
    tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null
    AND tfdim.deleted_at IS NULL