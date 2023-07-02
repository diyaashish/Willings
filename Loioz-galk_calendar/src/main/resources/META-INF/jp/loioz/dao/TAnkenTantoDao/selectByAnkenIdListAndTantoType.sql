SELECT DISTINCT
  tat.anken_id
  , tat.account_seq
  , tat.tanto_type_branch_no
  , tat.anken_main_tanto_flg
  , tat.tanto_type
  , ma.account_color 
  , ma.account_type 
FROM
  t_anken_tanto tat 
  LEFT JOIN m_account ma 
    ON tat.account_seq = ma.account_seq 
WHERE
  anken_id IN /* ankenIdList */(NULL);
