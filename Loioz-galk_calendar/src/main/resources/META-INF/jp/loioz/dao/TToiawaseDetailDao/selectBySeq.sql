SELECT
    * 
FROM
    t_toiawase_detail 
WHERE
    toiawase_detail_seq IN /* toiawaseDetailSeq */(null)
    AND sys_deleted_at IS NULL
    AND sys_deleted_by IS NULL
;
