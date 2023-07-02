-- シンボリックリンクは含めずにファイル名検索を行い、該当するファイル一覧を取得する
SELECT
    file_configuration_management_id            -- ファイル構成管理ID
    , display_priority AS display_priority      -- 表示優先順位
    , file_kubun                                -- ファイル区分
    , file_detail_info_management_id            -- ファイル詳細情報管理ID
    , file_name                                 -- ファイル名
    , file_type                                 -- ファイルタイプ
    , file_extension                            -- ファイル拡張子
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
            , mfdp.display_priority AS display_priority -- 表示優先順位
            , tfcm.file_kubun               -- ファイル区分
            , tfdim.file_detail_info_management_id -- ファイル詳細情報管理ID
            , tfdim.file_name               -- ファイル名
            , tfdim.file_extension          -- ファイル拡張子
            , tfdim.file_type               -- ファイルタイプ
            , tfdim.folder_path             -- フォルダパス
            , tfdim.file_created_datetime   -- ファイル作成日時
            , tfdim.upload_datetime         -- アップロード日時
            , tfdim.upload_user             -- アップロードユーザー
            , tfdim.file_size               -- ファイルサイズ
            , tfpim.view_limit              -- 閲覧制限
            , trfrim.anken_id AS anken_id   -- 案件ID
            , NULL as anken_name            -- 案件名
            , trfrim.anken_id AS customer_id -- 顧客ID
            , NULL as customer_name         -- 顧客名
        FROM
            t_root_folder_related_info_management trfrim -- ルートフォルダ情報管理
            INNER JOIN t_file_configuration_management root_tfcm -- ファイル構成管理(ルートフォルダレコード)
                ON root_tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
                AND root_tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
            INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
                ON tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
                AND tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
            INNER JOIN t_file_detail_info_management tfdim -- ファイル詳細情報管理
                ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
                AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
                AND tfdim.deleted_operation_account_seq IS null
                AND TRIM(
                    CONCAT(
                        COALESCE(tfdim.file_name, '')
                        , COALESCE(tfdim.file_extension, '')
                    )
                ) like /* fileName */null
        /*%if targetSearchPathList.size() > 0 */
                AND tfdim.folder_path IN /* targetSearchPathList */(null)
        /*%end */
                AND tfdim.deleted_by IS NULL
                AND tfdim.deleted_at IS NULL
            left join t_folder_permission_info_management tfpim -- フォルダ権限情報管理
                ON tfpim.file_configuration_management_id = tfcm.file_configuration_management_id
            INNER JOIN m_file_management_display_priority mfdp -- ファイル管理表示優先順位マスタ
                ON mfdp.file_management_display_priority_id = tfcm.file_management_display_priority_id
        WHERE
        /*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@ANKEN.equals(axisKubun) */
            trfrim.anken_id = /* id */null
        /*%end*/
        /*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@KOKYAKU.equals(axisKubun) */
            trfrim.customer_id = /* id */null
        /*%end*/
    ) fileInfo
ORDER BY
    fileInfo.display_priority
    , fileInfo.file_name
