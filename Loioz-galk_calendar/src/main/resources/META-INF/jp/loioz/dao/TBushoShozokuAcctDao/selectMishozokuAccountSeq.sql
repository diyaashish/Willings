SELECT DISTINCT
    ma.account_seq 
FROM
    m_account ma
    LEFT JOIN t_busho_shozoku_acct tbsa
        USING (account_seq)
    LEFT JOIN m_busho mb
        ON tbsa.busho_id = mb.busho_id
        AND mb.deleted_at IS NULL
        AND mb.deleted_by IS NULL
WHERE
    ma.account_status = /* @jp.loioz.common.constant.CommonConstant$AccountStatus@ENABLED */'1' 
    AND ma.deleted_at IS NULL
    AND ma.deleted_by IS NULL
    AND mb.busho_id IS NULL
;