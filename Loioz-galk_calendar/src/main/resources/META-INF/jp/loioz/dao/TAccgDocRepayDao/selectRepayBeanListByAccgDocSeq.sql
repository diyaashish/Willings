SELECT
  tadr.doc_repay_seq
  , tadr.accg_doc_seq
  , tadr.repay_transaction_date
  , tadr.repay_item_name
  , tadr.repay_amount
  , tadr.sum_text
  , tadr.doc_repay_order
  , tadrtdrm.deposit_recv_seq
  , tadrtdrm.created_accg_doc_seq
FROM
  t_accg_doc_repay tadr 
  LEFT JOIN ( 
    SELECT
      mapping.doc_repay_seq
      , GROUP_CONCAT(tdr.deposit_recv_seq) AS deposit_recv_seq
      , GROUP_CONCAT(tdr.created_accg_doc_seq) AS created_accg_doc_seq
    FROM
      t_accg_doc_repay_t_deposit_recv_mapping mapping
      LEFT JOIN t_deposit_recv tdr
        ON tdr.deposit_recv_seq = mapping.deposit_recv_seq
    GROUP BY
      mapping.doc_repay_seq
  ) AS tadrtdrm 
    ON tadr.doc_repay_seq = tadrtdrm.doc_repay_seq 
WHERE
  tadr.accg_doc_seq = /* accgDocSeq */NULL 
ORDER BY
  tadr.doc_repay_order
;