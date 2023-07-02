SELECT
    tfd.* 
FROM
    t_folder_dropbox tfd 
    INNER JOIN t_root_folder_dropbox trfd 
        ON tfd.root_folder_dropbox_seq = trfd.root_folder_dropbox_seq 
WHERE
    trfd.deleted_at IS NULL
    AND tfd.anken_id >= /* idFrom */null
    AND tfd.anken_id <= /* idTo */null
;