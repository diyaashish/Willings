-- ゴミ箱内のすべてのファイル詳細情報を取得する
SELECT
    tfdim.*
FROM
    t_file_detail_info_management tfdim
    INNER JOIN t_file_configuration_management tfcm
        ON tfcm.file_detail_info_management_id = tfdim.file_detail_info_management_id
        AND tfcm.file_kubun != /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
WHERE
    tfdim.trash_box_flg = /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_TRASH.getCd() */'1'
/*%if isSystemManager */
    AND tfdim.deleted_operation_account_seq IS NOT NULL
/*%else */
    AND tfdim.deleted_operation_account_seq = /* loginAccountSeq */null
/*%end */
    AND tfdim.deleted_at IS NULL
