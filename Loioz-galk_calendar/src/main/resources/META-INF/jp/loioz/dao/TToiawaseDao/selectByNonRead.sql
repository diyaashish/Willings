SELECT DISTINCT
    toiawase_seq
FROM
    t_toiawase_detail 
WHERE
    tenant_read_flg = '0'
    AND (
        sys_open_flg = '1' 
        OR sys_open_flg IS NULL
    ) 
    AND sys_deleted_at IS NULL 
    AND sys_deleted_by IS NULL
;