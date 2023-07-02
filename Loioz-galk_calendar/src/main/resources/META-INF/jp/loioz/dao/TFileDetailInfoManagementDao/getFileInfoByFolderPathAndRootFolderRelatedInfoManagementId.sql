-- フォルダパスとルートフォルダ関連情報管理IDから遷移先のファイル一覧を取得する
SELECT
    tfcm.file_configuration_management_id       -- ファイル構成管理ID
    , mfdp.display_priority                     -- 表示優先順位
    , tfcm.file_kubun                           -- ファイル区分
    , tfdim.file_detail_info_management_id      -- ファイル詳細情報管理    ID
    , tfdim.file_name                           -- ファイル名
    , tfdim.file_extension                      -- ファイル拡張子
    , tfdim.file_type                           -- ファイルタイプ
    , tfdim.folder_path                         -- フォルダパス
    , tfdim.file_created_datetime               -- ファイル作成日時
    , tfdim.upload_datetime                     -- アップロード日時
    , tfdim.upload_user                         -- アップロードユーザー
    , tfdim.file_size                           -- ファイルサイズ
    , tfpim.view_limit                          -- 閲覧制限
FROM
    t_file_detail_info_management tfdim         -- ファイル詳細情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id
        AND tfcm.root_folder_related_info_management_id = /* rootFolderRelatedInfoManagementId */null
    INNER JOIN m_file_management_display_priority mfdp -- ファイル管理表示優先順位マスタ
        ON mfdp.file_management_display_priority_id = tfcm.file_management_display_priority_id
    LEFT JOIN t_folder_permission_info_management tfpim -- フォルダ権限情報管理
        ON tfpim.file_configuration_management_id = tfcm.file_configuration_management_id
WHERE
    tfdim.folder_path = /* folderPath */null
    AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
    AND tfdim.deleted_operation_account_seq IS NULL
    AND tfdim.deleted_at IS NULL
ORDER BY
    mfdp.display_priority
    , tfdim.file_name
