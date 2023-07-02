SELECT
  td.receive_account_seq AS receive_account_seq_list
  , td.send_account_seq
  , CONCAT('Re:', td.title) AS title
  , td.body
  , td.draft_flg
FROM
  t_dengon AS td
WHERE
  td.dengon_seq = /* dengonSeq */null;