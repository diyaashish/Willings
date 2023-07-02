SELECT
  ts.saiban_seq
  , ts.anken_id
  , ts.saiban_branch_no
  , ts.saiban_status
  , ts.saibansho_id
  , ms.saibansho_name 
FROM
  t_saiban ts 
  LEFT OUTER JOIN m_saibansho ms 
    ON ts.saibansho_id = ms.saibansho_id 
WHERE
  ts.anken_id = /* ankenId */NULL 
ORDER BY
  ts.saiban_seq;
