SELECT
    TI.info_mgt_seq AS infoMgtSeq
    , TI.info_type AS infoType
    , TI.info_subject AS infoSubject
    , TI.info_body AS infoBody
    , TI.info_start_at AS updatedAt
    , TIRH.account_seq AS accountSeq
FROM
    service_mgt.t_info_mgt AS TI
    LEFT OUTER JOIN t_info_read_history AS TIRH
        ON TI.info_mgt_seq = TIRH.info_mgt_seq
        AND TIRH.account_seq = /* accountSeq */NULL
WHERE
    TIRH.info_mgt_seq IS NULL
    AND TI.deleted_at IS NULL
    AND TI.deleted_by IS NULL
    AND TI.info_open_flg = '1'
    AND TI.info_start_at <= /* now */NULL
    AND TI.info_end_at >= /* now */NULL
ORDER BY
    TI.info_start_at;
