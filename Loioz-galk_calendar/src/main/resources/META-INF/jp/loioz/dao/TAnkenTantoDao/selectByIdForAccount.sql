SELECT
  tat.anken_id
  , tat.account_seq
  , CONCAT(
    COALESCE(ma.account_name_sei, '')
    , COALESCE(ma.account_name_mei, '')
  ) AS account_name
  , ma.account_name_sei
  , ma.account_name_mei
  , ma.account_type
  , tat.tanto_type
  , tat.tanto_type_branch_no
  , tat.anken_main_tanto_flg
FROM
  t_anken_tanto tat
  LEFT OUTER JOIN m_account ma
    USING (account_seq)
WHERE
  tat.anken_id = /* ankenId */1
ORDER BY
  tat.tanto_type,
  tat.tanto_type_branch_no
;
