SELECT
    COUNT(tirh.info_mgt_seq)
FROM
    t_info_read_history tirh
WHERE
    tirh.info_mgt_seq = /* infoSeq */NULL
    AND tirh.account_seq = /* accountSeq */NULL;
