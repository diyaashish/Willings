SELECT
  count(*)
FROM
  t_saiban ts 
  LEFT JOIN t_saiban_jiken tsj 
    USING (saiban_seq) 
WHERE
  ts.anken_id = /* ankenId */null
ORDER BY
  ts.saiban_branch_no;
