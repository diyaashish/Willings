SELECT 
  tadr.accg_doc_seq
  , SUM(tadr.repay_amount) AS total_repay_amount
FROM
  t_accg_doc_repay tadr
WHERE
  tadr.accg_doc_seq = /* accgDocSeq */null
GROUP BY
  tadr.accg_doc_seq
;
