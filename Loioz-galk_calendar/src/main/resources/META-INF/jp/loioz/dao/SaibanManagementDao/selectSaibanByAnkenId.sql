SELECT
  ts.anken_id
  , ts.saiban_seq
  , ts.saiban_branch_no
  , ts.saiban_status
  , ts.saiban_start_date
  , ts.saibansho_id
  , ts.saibansho_name
  , ts_tree.parent_seq
  , ts_tree.connect_type
  , ts_tree.honso_flg
  , ts_tree.kihon_flg
  , tsj.jiken_gengo
  , tsj.jiken_year
  , tsj.jiken_mark
  , tsj.jiken_no
  , tsj.jiken_name
FROM
  t_saiban ts 
  LEFT JOIN t_saiban_jiken tsj 
    ON tsj.saiban_seq = ts.saiban_seq 
  INNER JOIN t_saiban_tree ts_tree 
    ON ts_tree.saiban_seq = ts.saiban_seq 
WHERE
  ts.anken_id = /* ankenId */1 
  AND NOT EXISTS ( 
    SELECT
      1 
    FROM
      t_saiban_jiken tsj2 
    WHERE
      tsj2.saiban_seq = tsj.saiban_seq 
      AND tsj2.jiken_seq < tsj.jiken_seq
  ) 
GROUP BY
  ts.saiban_seq
  , ts_tree.saiban_tree_seq
  , tsj.jiken_seq;
