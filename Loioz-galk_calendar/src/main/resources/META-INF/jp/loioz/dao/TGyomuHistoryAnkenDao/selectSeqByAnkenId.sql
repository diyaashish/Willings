SELECT DISTINCT
  tgh.gyomu_history_seq 
FROM
  t_gyomu_history_anken tgha 
  INNER JOIN t_gyomu_history tgh 
    ON tgha.gyomu_history_seq = tgh.gyomu_history_seq 
WHERE
  tgha.anken_id = /* ankenId */NULL 
  AND tgh.transition_type = '2';