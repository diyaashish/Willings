-- ゴミ箱内の全てのファイル構成管理情報を取得する
SELECT
    tfcm.*
FROM
    t_file_configuration_management tfcm
    INNER JOIN t_file_detail_info_management tfdim
        ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
        AND tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
/*%if isSystemManager */
        AND tfdim.deleted_operation_account_seq IS NOT NULL
/*%else */
        AND tfdim.deleted_operation_account_seq = /* loginAccountSeq */null
/*%end */
        AND tfdim.deleted_at IS NULL
WHERE
    tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
