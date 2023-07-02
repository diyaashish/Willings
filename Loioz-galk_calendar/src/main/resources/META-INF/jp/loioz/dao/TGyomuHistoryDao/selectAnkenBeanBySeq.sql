SELECT
  tgha.anken_id
  , ta.anken_name
  , ta.bunya_id
FROM
  t_gyomu_history_anken tgha
  LEFT JOIN t_anken ta
    ON tgha.anken_id = ta.anken_id
WHERE
  tgha.gyomu_history_seq = /* seq */5;