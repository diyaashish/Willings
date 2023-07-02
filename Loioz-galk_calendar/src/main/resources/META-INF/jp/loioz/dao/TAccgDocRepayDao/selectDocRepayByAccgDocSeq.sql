SELECT 
  tadr.*
FROM
  t_accg_doc_repay tadr
WHERE
  tadr.accg_doc_seq = /* accgDocSeq */null
ORDER BY
  tadr.doc_repay_order
;
