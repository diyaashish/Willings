-- フォルダ毎ゴミ箱入れたファイルを取得する
SELECT
    tfcm.file_configuration_management_id
FROM
    t_file_detail_info_management tfdim                           -- ファイル詳細情報管理
    INNER JOIN t_file_configuration_management tfcm -- ファイル構成管理
        using (file_detail_info_management_id)
WHERE
    tfdim.trash_box_flg = '1'
    AND tfdim.deleted_at IS NULL
    AND tfdim.deleted_by IS NULL
    AND tfdim.file_type IS NULL
