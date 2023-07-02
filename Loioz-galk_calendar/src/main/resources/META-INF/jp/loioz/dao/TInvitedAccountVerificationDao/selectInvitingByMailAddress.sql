SELECT
  *
FROM
  t_invited_account_verification
WHERE
  mail_address IN /* mailAddressList */(null)
and temp_limit_date >= /* now */null
and complete_flg = 0;