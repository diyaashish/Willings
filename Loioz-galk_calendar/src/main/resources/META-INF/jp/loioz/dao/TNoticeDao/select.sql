SELECT
  tn.notice_seq
  , tn.notice_type
  , tn.notice_contents
  , 1 as notice_kidoku_flg
  , tn.updated_at 
FROM
  t_notice tn 
WHERE
  notice_account_seq_to = /* accountSeq */1 
ORDER BY
  notice_seq DESC; 
