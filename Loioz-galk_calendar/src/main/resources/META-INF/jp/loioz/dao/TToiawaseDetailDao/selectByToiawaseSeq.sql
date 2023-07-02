SELECT
    * 
FROM
    t_toiawase_detail 
WHERE
    toiawase_seq = /* toiawaseSeq */'null'
    AND (
        sys_open_flg = '1'
        -- NULLの場合はユーザーが登録したデータなので考慮する
        OR sys_open_flg IS NULL
    ) 
    AND sys_deleted_at IS NULL
    AND sys_deleted_by IS NULL
;