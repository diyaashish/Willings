-- ファイル管理画面に表示する、シンボリックリンクを含めないファイル情報を表示する
SELECT
    file_configuration_management_id            -- ファイル構成管理ID
    , display_priority as display_priority      -- 表示優先順位
    , file_kubun                                -- ファイル区分
    , file_detail_info_management_id            -- ファイル詳細情報管理ID
    , file_name                                 -- ファイル名
    , file_extension                            -- ファイル拡張子
    , file_type                                 -- ファイルタイプ
    , folder_path                               -- フォルダパス
    , file_created_datetime                     -- ファイル作成日時
    , upload_datetime                           -- アップロード日時
    , upload_user                               -- アップロードユーザー
    , file_size                                 -- ファイルサイズ
    , view_limit                                -- 閲覧制限
    , anken_id                                  -- 案件ID
    , anken_name                                -- 案件名
    , customer_id                               -- 顧客ID
    , customer_name                             -- 顧客名
FROM
    (
        -- 案件・顧客に紐づくファイル情報
        SELECT
            tfcm.file_configuration_management_id -- ファイル構成管理ID
            , mfmdp.display_priority as display_priority -- 表示優先順位
            , tfcm.file_kubun                   -- ファイル区分
            , tfdim.file_detail_info_management_id -- ファイル詳細情報管理ID
            , tfdim.file_name                   -- ファイル名
            , tfdim.file_extension              -- ファイル拡張子
            , tfdim.file_type                   -- ファイルタイプ
            , tfdim.folder_path                 -- フォルダパス
            , tfdim.file_created_datetime       -- ファイル作成日時
            , tfdim.upload_datetime             -- アップロード日時
            , tfdim.upload_user                 -- アップロードユーザー
            , tfdim.file_size                   -- ファイルサイズ
            , tfpim.view_limit                  -- 閲覧制限
            , trfrim.anken_id as anken_id       -- 案件ID
            , NULL as anken_name                -- 案件名
            , trfrim.anken_id as customer_id    -- 顧客ID
            , NULL as customer_name             -- 顧客名
        FROM
            t_root_folder_related_info_management trfrim
            INNER JOIN t_file_configuration_management tfcm
                ON tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
                AND tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_FOLDER.getCd() */'1'
            INNER JOIN t_file_detail_info_management tfdim
                ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
                AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
                AND tfdim.deleted_operation_account_seq is null
                AND tfdim.folder_path = '/'
                AND tfdim.deleted_at IS NULL
            INNER JOIN m_file_management_display_priority mfmdp
                ON mfmdp.file_management_display_priority_id = tfcm.file_management_display_priority_id
            INNER JOIN t_folder_permission_info_management tfpim
                ON tfpim.file_configuration_management_id = tfcm.file_configuration_management_id
        WHERE
            trfrim.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
    ) fileInfo
ORDER BY
    fileInfo.display_priority
    , fileInfo.file_name
