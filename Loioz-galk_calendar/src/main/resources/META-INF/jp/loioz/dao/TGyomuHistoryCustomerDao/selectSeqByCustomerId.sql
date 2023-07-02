SELECT DISTINCT
  tgh.gyomu_history_seq 
FROM
  t_gyomu_history_customer tghc 
  INNER JOIN t_gyomu_history tgh 
    ON tghc.gyomu_history_seq = tgh.gyomu_history_seq 
WHERE
  tghc.customer_id = /* customerId */NULL 
  AND tgh.transition_type = '1';