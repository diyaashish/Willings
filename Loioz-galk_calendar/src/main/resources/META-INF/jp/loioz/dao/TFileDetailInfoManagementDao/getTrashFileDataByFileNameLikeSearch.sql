-- ファイル名のLike検索に該当する、ゴミ箱画面に表示するファイル一覧を取得する
SELECT
    tfcm.file_configuration_management_id  -- ファイル構成管理ID
    , tfcm.file_kubun                                       -- ファイル区分
    , tfdim.folder_path                                    -- フォルダパス
    , tfdim.file_name                                       -- ファイル名
    , tfdim.file_extension                                 -- ファイル拡張子
    , CONCAT(
        COALESCE(ma.account_name_sei, '')
        , ' '
        , COALESCE(ma.account_name_mei, '')
    ) as deleted_user_name                           -- 削除ユーザー名
    , tfdim.updated_at as deleted_at             -- 削除日時
    , tfdim.file_size                                          -- ファイルサイズ
FROM
    t_file_detail_info_management tfdim       -- ファイル詳細情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        using (file_detail_info_management_id)
    INNER JOIN m_account ma                     -- アカウント
        ON ma.account_seq = tfdim.deleted_operation_account_seq
WHERE
    tfdim.file_name LIKE /* fileName */null
    AND tfdim.trash_box_flg =
    /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
/*%if isSystemManager */
    AND tfdim.deleted_operation_account_seq is not null
/*%else */
    AND tfdim.deleted_operation_account_seq = /* loginAccountSeq */null
/*%end */
    AND tfdim.deleted_at IS NULL
  /*%if parentFileConfigurationManagementIdList != null  && parentFileConfigurationManagementIdList.size() > 0 */
    AND tfcm.parent_file_configuration_management_id NOT IN /*parentFileConfigurationManagementIdList*/(null)
  /*%end*/
ORDER BY
    tfdim.folder_path
