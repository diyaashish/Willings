-- ファイル構成管理IDから、ゴミ箱に入っている削除対象のファイル詳細情報を取得する
SELECT
    tfdim.*
FROM
    t_file_configuration_management tfcm
    INNER JOIN t_file_detail_info_management tfdim
        ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
        AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
        AND tfdim.deleted_operation_account_seq IS NOT NULL
        AND tfdim.deleted_at IS NULL
WHERE
    tfcm.file_configuration_management_id = /* fileConfigurationManagementId */null