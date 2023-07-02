SELECT
  tat.account_seq 
FROM
  t_anken_tanto tat
WHERE
  tat.anken_id = /* ankenId */null 
  and tat.tanto_type = /* tantoType */null 
ORDER BY
  tat.tanto_type_branch_no;
