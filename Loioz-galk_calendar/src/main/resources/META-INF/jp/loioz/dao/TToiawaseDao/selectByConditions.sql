SELECT
    tt.toiawase_seq
FROM
    t_toiawase tt 
    LEFT JOIN t_toiawase_detail ttd 
        USING (toiawase_seq) 
WHERE
    1 = 1 
    /*%if conditions.isNotEmpty(conditions.text) */
    AND ( 
        tt.subject LIKE /* @infix(conditions.text) */'%' 
        OR ttd.body LIKE /* @infix(conditions.text) */'%'
    ) 
    /*%end*/
    /*%if conditions.isNotEmpty(conditions.toiawaseStatus) */
    AND tt.toiawase_status = /* conditions.toiawaseStatus */'1'
    /*%end*/
    AND (
        ttd.sys_open_flg = '1'
        OR ttd.sys_open_flg IS NULL -- NULLの場合はユーザーが登録したデータなので考慮する
    )
    AND ttd.sys_deleted_at IS NULL
    AND ttd.sys_deleted_by IS NULL
GROUP BY
    tt.toiawase_seq
ORDER BY
    tt.last_update_at DESC;