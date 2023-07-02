SELECT
  tst.saiban_tanto_seq
  , tst.saiban_seq
  , tst.tanto_type
  , tst.tanto_type_branch_no
  , tst.account_seq
  , ma.account_name_sei
  , ma.account_name_mei
  , tst.saiban_main_tanto_flg
  , ma.account_color
FROM
  t_saiban_tanto tst 
  INNER JOIN m_account ma 
    on tst.account_seq = ma.account_seq
WHERE
  tst.saiban_seq = /* saibanSeq */1
ORDER BY
  tst.tanto_type,
  tst.tanto_type_branch_no
;