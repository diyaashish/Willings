SELECT
  *
FROM
  m_account ma
WHERE
  ma.deleted_at IS NULL
  /*%if searchForm != null */
  AND 1=1 /** 検索条件が追加された場合、ここに追加 */
  /*%end */
ORDER BY
  ma.account_seq desc
;