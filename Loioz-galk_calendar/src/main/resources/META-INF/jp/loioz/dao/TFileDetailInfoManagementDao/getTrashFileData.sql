-- ゴミ箱画面に表示するファイル一覧を取得する
SELECT
    tfcm.file_configuration_management_id       -- ファイル構成管理ID
    , tfcm.file_kubun                           -- ファイル区分
    , tfdim.folder_path                         -- フォルダパス
    , tfdim.file_name                           -- ファイル名
    , tfdim.file_extension                      -- ファイル拡張子
    , tfdim.file_type                           -- ファイルタイプ
    , CONCAT(
        COALESCE(ma.account_name_sei, '')
        , ' '
        , COALESCE(ma.account_name_mei, '')
    ) as deleted_user_name                      -- 削除ユーザー名
    , tfdim.updated_at as deleted_at            -- 削除日時
    , tfdim.file_size                           -- ファイルサイズ
    , trfrim.anken_id                           -- 案件ID
    , ta.anken_name                             -- 案件名
    , trfrim.customer_id                        -- 顧客ID
    , TRIM(
        CONCAT(
            COALESCE(tc.customer_name_sei, '')
            , ' '
            , COALESCE(tc.customer_name_mei, '')
        )
    ) AS customer_name                          -- 顧客名
    , root_folder_tfdim.file_name as root_folder_name -- ルートフォルダ名
FROM
    t_file_detail_info_management tfdim         -- ファイル詳細情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        using (file_detail_info_management_id)
    INNER JOIN t_root_folder_related_info_management trfrim -- ルートフォルダ関連情報管理
        using (root_folder_related_info_management_id)
    INNER JOIN t_file_configuration_management root_folder_tfcm -- ルートフォルダのファイル構成管理
        ON root_folder_tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
        AND root_folder_tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
    INNER JOIN t_file_detail_info_management root_folder_tfdim -- ルートフォルダのファイル詳細情報管理
        ON root_folder_tfdim.file_detail_info_management_id = root_folder_tfcm.file_detail_info_management_id
    LEFT OUTER JOIN t_anken ta                  -- 案件情報
        ON trfrim.anken_id = ta.anken_id
    LEFT OUTER JOIN t_person tc               -- 名簿情報
        ON trfrim.customer_id = tc.customer_id
    INNER JOIN m_account ma                     -- アカウント
        ON ma.account_seq = tfdim.deleted_operation_account_seq
WHERE
    tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
    AND tfdim.deleted_at IS NULL
    AND tfdim.deleted_by IS NULL
  /*%if parentFileConfigurationManagementIdList != null  && parentFileConfigurationManagementIdList.size() > 0 */
    AND tfcm.parent_file_configuration_management_id NOT IN /*parentFileConfigurationManagementIdList*/(null)
  /*%end*/
ORDER BY
    tfdim.updated_at DESC
