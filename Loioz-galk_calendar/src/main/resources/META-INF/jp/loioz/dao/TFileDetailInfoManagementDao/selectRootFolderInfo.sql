-- ルートフォルダの情報を取得する
SELECT
    trfrim.root_folder_related_info_management_id -- ルートフォルダ関連情報管理ID
    , tfcm.file_configuration_management_id     -- ファイル構成管理ID
    , trfrim.anken_id
    , ta.anken_name
    , trfrim.customer_id
    , TRIM(
        CONCAT(
            COALESCE(tc.customer_name_sei, '')
            , ' '
            , COALESCE(tc.customer_name_mei, '')
        )
    ) as customer_name
    , tfdim.file_name                           -- ファイル名
FROM
    t_root_folder_related_info_management trfrim -- ルートフォルダ関連情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        ON tfcm.root_folder_related_info_management_id = trfrim.root_folder_related_info_management_id
        AND tfcm.file_kubun =
        /* @jp.loioz.common.constant.CommonConstant$FileKubun@IS_ROOT_FOLDER.getCd() */'0'
    INNER JOIN t_file_detail_info_management tfdim -- ファイル詳細情報管理
        ON tfdim.file_detail_info_management_id = tfcm.file_detail_info_management_id
        AND tfdim.trash_box_flg =
        /* @jp.loioz.common.constant.CommonConstant$TrashBoxFlg@IS_NOT_TRASH.getCd() */'0'
    LEFT OUTER JOIN t_anken ta                  -- 案件情報
        ON trfrim.anken_id = ta.anken_id
    LEFT OUTER JOIN t_person tc               -- 名簿情報
        ON trfrim.customer_id = tc.customer_id
WHERE
    0 = 0
/*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@ANKEN.equals(axisKubun) */
    AND trfrim.anken_id = /* id */null
/*%end*/
/*%if @jp.loioz.common.constant.CommonConstant$AxisKubun@KOKYAKU.equals(axisKubun) */
    AND trfrim.customer_id = /* id */null
/*%end*/
