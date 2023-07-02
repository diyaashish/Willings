SELECT
  m_account.account_seq
  , CONCAT(COALESCE(m_account.account_name_sei,''),' ',COALESCE(m_account.account_name_mei,'')) AS account_name
  , m_account.account_type
  , IF (
    (
      SELECT
        t_group_shozoku_acct.account_seq
      FROM
        t_group_shozoku_acct
      WHERE
        m_account.account_seq = t_group_shozoku_acct.account_seq
        AND t_group_shozoku_acct.group_id = /* groupId */null
    ) IS NOT NULL
    , TRUE
    , FALSE
  ) AS flg_regist
FROM
  m_account;