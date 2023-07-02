SELECT
  ts.saiban_seq
  , ts.anken_id
  , ts.saiban_branch_no
  , CASE 
    WHEN tsj.jiken_seq IS NOT NULL 
      THEN tsj.jiken_gengo 
    ELSE tsj2.jiken_gengo 
    END AS jiken_gengo
  , CASE 
    WHEN tsj.jiken_seq IS NOT NULL 
      THEN tsj.jiken_year 
    ELSE tsj2.jiken_year 
    END AS jiken_year
  , CASE 
    WHEN tsj.jiken_seq IS NOT NULL 
      THEN tsj.jiken_mark 
    ELSE tsj2.jiken_mark 
    END AS jiken_mark
  , CASE 
    WHEN tsj.jiken_seq IS NOT NULL 
      THEN tsj.jiken_no 
    ELSE tsj2.jiken_no 
    END AS jiken_no
  , CASE 
    WHEN tsj.jiken_seq IS NOT NULL 
      THEN tsj.jiken_name 
    ELSE tsj2.jiken_name 
    END AS jiken_name 
FROM
  t_saiban ts 
  LEFT JOIN t_saiban_jiken tsj 
    ON ts.saiban_seq = tsj.saiban_seq 
    AND tsj.jiken_seq IN ( 
      SELECT
        jiken_seq 
      FROM
        t_saiban 
        INNER JOIN t_saiban_jiken 
          ON t_saiban.saiban_seq = t_saiban_jiken.saiban_seq 
          AND EXISTS ( 
            SELECT
              * 
            FROM
              t_saiban_add_keiji 
            WHERE
              t_saiban_add_keiji.main_jiken_seq = t_saiban_jiken.jiken_seq 
              AND t_saiban.anken_id = /* ankenId */null
          )
        WHERE
          t_saiban.anken_id  = /* ankenId */null
    ) 
  LEFT JOIN t_saiban_jiken tsj2 
    ON ts.saiban_seq = tsj2.saiban_seq 
    AND tsj2.jiken_seq IN ( 
      SELECT
        jiken_seq 
      FROM
        t_saiban 
        INNER JOIN t_saiban_jiken 
          ON t_saiban.saiban_seq = t_saiban_jiken.saiban_seq 
        WHERE
          t_saiban.anken_id  = /* ankenId */null
    ) 
WHERE
  ts.anken_id = /*ankenId*/null
  AND ( 
    tsj.jiken_seq = tsj2.jiken_seq 
    OR tsj.jiken_seq IS NULL
  )
