SELECT
  td.dengon_seq
  , td.receive_account_seq AS receive_account_seq_list
  , td.title
  , td.body
  , td.draft_flg
FROM
  t_dengon AS td
WHERE
  td.dengon_seq = /* dengonSeq */null
  AND td.send_account_seq = /* loginAccountSeq */0
;