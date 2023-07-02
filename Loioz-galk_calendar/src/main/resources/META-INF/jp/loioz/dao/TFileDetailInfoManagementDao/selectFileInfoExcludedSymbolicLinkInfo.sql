-- ファイル管理情報画面に表示する、シンボリックリンクを除いたファイル情報を取得する
SELECT
    tfcm.file_configuration_management_id       -- ファイル構成管理ID
    , tfcm.file_kubun                           -- ファイル区分
    , tfdim.file_detail_info_management_id      -- ファイル詳細情報管理ID
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
    t_root_folder_related_info_management trfrim -- ルートフォルダ情報管理
    INNER JOIN t_file_configuration_management root_tfcm -- ファイル構成管理(ルートフォルダレコード)
        ON root_tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
        AND root_tfcm.file_kubun =
        /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        ON tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
        AND tfcm.file_kubun !=
        /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
        AND tfcm.parent_file_configuration_management_id = root_tfcm.file_configuration_management_id -- ルートフォルダ配下
    INNER JOIN t_file_detail_info_management tfdim -- ファイル詳細情報管理
        ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
        AND tfdim.trash_box_flg =
        /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
        AND tfdim.deleted_at is NULL
    LEFT JOIN t_folder_permission_info_management tfpim -- フォルダ権限情報管理
        ON tfpim.file_configuration_management_id = tfcm.file_configuration_management_id
    INNER JOIN m_file_management_display_priority mfdp -- ファイル管理表示優先順位マスタ
        ON mfdp.file_management_display_priority_id = tfcm.file_management_display_priority_id
WHERE
    0 = 0
/*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@ANKEN.equals(axisKubun) */
    AND trfrim.anken_id = /* id */null
/*%end*/
/*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@KOKYAKU.equals(axisKubun) */
    AND trfrim.customer_id = /* id */null
/*%end*/
ORDER BY
    mfdp.display_priority
    , tfdim.file_name
