SELECT
  ts.saiban_seq
  , ts.anken_id
  , ts.saiban_branch_no
  , mb.bunya_id
  , mb.bunya_name
  , mb.bunya_type
  , ta.anken_type
FROM
  t_saiban ts 
  INNER JOIN t_anken ta 
    ON ts.anken_id = ta.anken_id 
  INNER JOIN m_bunya mb
    ON ta.bunya_id = mb.bunya_id 
WHERE
  ts.saiban_seq IN /* saibanSeqList */(NULL) 
ORDER BY
  ts.created_at DESC;
