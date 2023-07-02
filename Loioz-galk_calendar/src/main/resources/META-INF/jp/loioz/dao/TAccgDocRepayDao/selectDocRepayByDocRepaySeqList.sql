SELECT 
  tadr.*
FROM
  t_accg_doc_repay tadr
WHERE
  tadr.doc_repay_seq IN /* docRepaySeqList */(null)
ORDER BY
  tadr.doc_repay_order
;
