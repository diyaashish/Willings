-- ルートフォルダ関連情報管理IDからルートフォルダ情報を取得する
SELECT
    trfrim.root_folder_related_info_management_id -- ルートフォルダ関連情報管理ID
    , tfcm.file_configuration_management_id     -- ファイル構成管理ID
    , trfrim.anken_id                           -- 案件ID
    , ta.anken_name                             -- 案件名
    , trfrim.customer_id                        -- 顧客ID
    , TRIM(
        CONCAT(
            COALESCE(tc.customer_name_sei, '')
            , ' '
            , COALESCE(tc.customer_name_mei, '')
        )
    ) as customer_name                          -- 顧客名
    , tfdim.file_name                           -- ファイル名
FROM
    t_root_folder_related_info_management trfrim -- ルートフォルダ関連情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        ON tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
        AND tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
    INNER JOIN t_file_detail_info_management tfdim -- ファイル詳細情報管理
        using (file_detail_info_management_id)
    LEFT OUTER JOIN t_anken ta                  -- 案件情報
        ON trfrim.anken_id = ta.anken_id
    LEFT OUTER JOIN t_person tc               -- 名簿情報
        ON trfrim.customer_id = tc.customer_id
WHERE
    trfrim.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
    AND tfdim.deleted_at IS NULL