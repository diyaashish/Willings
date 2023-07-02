SELECT
  * 
FROM
  t_deposit_recv tdr 
WHERE
  tdr.created_accg_doc_seq = /* accgDocSeq */NULL 
  AND tdr.created_type = /* @jp.loioz.common.constant.CommonConstant$DepositRecvCreatedType@CREATED_BY_ISSUANCE.getCd() */'2';
;