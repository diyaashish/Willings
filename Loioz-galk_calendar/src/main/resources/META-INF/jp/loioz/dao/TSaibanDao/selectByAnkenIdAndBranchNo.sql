SELECT
  ts.saiban_seq
  , ta.bunya_id 
FROM
  t_saiban ts 
  INNER JOIN t_anken ta 
    USING(anken_id) 
WHERE
  ts.anken_id = /*ankenId*/1
  AND ts.saiban_branch_no = /*branchNo*/1
;