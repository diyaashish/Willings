SELECT
  * 
FROM
  t_saiban ts
WHERE
  ts.anken_id IN /* ankenIdList */(null)
ORDER BY
  ts.anken_id
  , ts.saiban_branch_no DESC;