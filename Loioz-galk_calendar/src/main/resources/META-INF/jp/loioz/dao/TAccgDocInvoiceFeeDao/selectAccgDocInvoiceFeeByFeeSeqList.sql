SELECT
  *
FROM
  t_accg_doc_invoice_fee tadif
WHERE
  tadif.fee_seq IN /* feeSeqList */(null)
;