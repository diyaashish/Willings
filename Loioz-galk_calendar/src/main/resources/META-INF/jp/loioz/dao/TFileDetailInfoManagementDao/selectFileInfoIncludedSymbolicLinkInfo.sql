-- ファイル管理画面に表示する、シンボリックリンクを含めたファイル情報を表示する
select
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
from
    (
        (
            -- 案件・顧客に紐づくファイル情報
            select
                tfcm.file_configuration_management_id -- ファイル構成管理ID
                , mfdp.display_priority as display_priority -- 表示優先順位
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
                , NULL as anken_id              -- 案件ID
                , NULL as anken_name            -- 案件名
                , NULL as customer_id           -- 顧客ID
                , NULL as customer_name         -- 顧客名
            from
                t_root_folder_related_info_management trfrim -- ルートフォルダ情報管理
                inner join t_file_configuration_management root_tfcm -- ファイル構成管理(ルートフォルダレコード)
                    on root_tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
                    and root_tfcm.file_kubun = /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
                inner join t_file_configuration_management tfcm -- ファイル構成管理
                    on tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
                    and tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
                    and tfcm.parent_file_configuration_management_id = root_tfcm.file_configuration_management_id -- ルートフォルダ配下
                inner join t_file_detail_info_management tfdim -- ファイル詳細情報管理
                    on tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
                    and tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
                left join t_folder_permission_info_management tfpim -- フォルダ権限情報管理
                    on tfpim.file_configuration_management_id = tfcm.file_configuration_management_id
                inner join m_file_management_display_priority mfdp -- ファイル管理表示優先順位マスタ
                    on mfdp.file_management_display_priority_id = tfcm.file_management_display_priority_id
            where
        /*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@ANKEN.equals(axisKubun) */
                trfrim.anken_id = /* id */null
        /*%end*/
        /*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@KOKYAKU.equals(axisKubun) */
                trfrim.customer_id = /* id */null
        /*%end*/
        )
    ) fileInfo
order by
    fileInfo.display_priority
    , fileInfo.file_name
