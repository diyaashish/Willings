SELECT
    tfb.* 
FROM
    t_folder_box tfb 
    INNER JOIN t_root_folder_box trfb 
        ON tfb.root_folder_box_seq = trfb.root_folder_box_seq 
WHERE
    trfb.deleted_at IS NULL
    AND tfb.anken_id >= /* idFrom */null
    AND tfb.anken_id <= /* idTo */null
;