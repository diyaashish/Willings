SELECT
  *
FROM
  t_accg_doc_act tada
WHERE
  tada.accg_doc_seq = /* accgDocSeq */'1'
ORDER BY
  tada.act_at
  , tada.act_type
;