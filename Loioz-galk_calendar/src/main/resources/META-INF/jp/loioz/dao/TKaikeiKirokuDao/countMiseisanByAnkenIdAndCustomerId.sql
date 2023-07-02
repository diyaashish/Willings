SELECT
  SUM(count) 
FROM
  ( 
    SELECT
      COUNT(*) AS count 
    FROM
      t_kaikei_kiroku tkk 
    WHERE
      tkk.seisan_seq IS NULL 
      AND tkk.anken_id = /* ankenId */1 
      AND tkk.customer_id = /* customerId */1 
    UNION ALL 
    SELECT
      COUNT(*) AS count 
    FROM
      t_nyushukkin_yotei 
    WHERE
      nyushukkin_date IS NULL 
      AND seisan_seq IN ( 
        select
          seisan_seq 
        from
          t_kaikei_kiroku 
        WHERE
          seisan_seq IS NOT NULL 
          AND anken_id = /* ankenId */1 
          AND customer_id = /* customerId */1
      )
  ) list;