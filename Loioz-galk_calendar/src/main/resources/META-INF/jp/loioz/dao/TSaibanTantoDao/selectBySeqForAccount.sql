SELECT
  tst.saiban_seq
  , tst.account_seq
  , CONCAT(
    COALESCE(ma.account_name_sei, '')
    , COALESCE(ma.account_name_mei, '')
  ) AS account_name
  , ma.account_name_sei
  , ma.account_name_mei
  , ma.account_type
  , tst.tanto_type
  , tst.tanto_type_branch_no
  , tst.saiban_main_tanto_flg
FROM
  t_saiban_tanto tst
  LEFT OUTER JOIN m_account ma
    USING (account_seq)
WHERE
  tst.saiban_seq = /* saibanSeq */1
ORDER BY
  tst.tanto_type,
  tst.tanto_type_branch_no
;