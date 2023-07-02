SELECT
    tfd.* 
FROM
    t_folder_dropbox tfd 
    INNER JOIN t_root_folder_dropbox trfd 
        ON tfd.root_folder_dropbox_seq = trfd.root_folder_dropbox_seq 
WHERE
    trfd.deleted_at IS NULL
    AND tfd.customer_id = 0
    AND tfd.anken_customer_id_from <= /* customerId */null
    AND tfd.anken_customer_id_to >= /* customerId */null
;