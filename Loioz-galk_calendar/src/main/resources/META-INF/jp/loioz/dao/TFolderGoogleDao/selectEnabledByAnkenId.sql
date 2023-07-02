SELECT
    tfg.* 
FROM
    t_folder_google tfg 
    INNER JOIN t_root_folder_google trfg 
        ON tfg.root_folder_google_seq = trfg.root_folder_google_seq 
WHERE
    trfg.deleted_at IS NULL
    AND anken_id = /* ankenId */null