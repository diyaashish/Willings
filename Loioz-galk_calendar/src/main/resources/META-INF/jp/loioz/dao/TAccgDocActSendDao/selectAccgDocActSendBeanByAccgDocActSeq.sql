SELECT
  tadas.accg_doc_act_send_seq
  , tadas.accg_doc_act_seq
  , tadas.send_type
  , tada.accg_doc_seq
  , tada.act_type
  , tada.act_by
  , tada.act_at
FROM
  t_accg_doc_act_send tadas
  LEFT JOIN t_accg_doc_act tada
    ON tadas.accg_doc_act_seq = tada.accg_doc_act_seq
WHERE
  tadas.accg_doc_act_seq IN /* accgDocActSeqList */(null)
ORDER BY
  tada.act_at
  , tada.act_type
;