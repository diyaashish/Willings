SELECT
    * 
FROM
    t_root_folder_google 
WHERE
    deleted_at IS NULL
    AND deleted_by IS NULL
;