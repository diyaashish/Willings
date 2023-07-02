SELECT
  ta.* 
FROM
  t_anken ta 
  INNER JOIN t_kanyosha tk
    ON ta.anken_id = tk.anken_id
WHERE
  tk.person_id = /* personId */1;