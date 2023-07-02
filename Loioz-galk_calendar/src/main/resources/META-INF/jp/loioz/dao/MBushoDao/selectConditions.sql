SELECT
    mb.busho_id
    , mb.busho_name
    , GROUP_CONCAT(
        CONCAT(ma.account_name_sei, ' ', ma.account_name_mei) separator ', '
    ) as shozoku_account_name
    , mb.disp_order
    , mb.version_no
FROM
    m_busho mb
    LEFT JOIN t_busho_shozoku_acct tbs
        using (busho_id)
    LEFT JOIN m_account ma
        ON tbs.account_seq = ma.account_seq
WHERE
    1 = 1
    AND mb.deleted_at IS NULL
/*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchForm.bushoName) */
    AND mb.busho_name = /* searchForm.bushoName */null
/*%end */
GROUP BY
    mb.busho_id
order by
    mb.disp_order;