SELECT
  tst.saiban_seq
  , tst.tanto_type
  -- 担当者名をカンマ区切りで取得：並び順は主担当、担当の順
  , GROUP_CONCAT(TRIM(CONCAT(COALESCE(ma.account_name_sei, ''), ' ', COALESCE(ma.account_name_mei, ''))) ORDER BY tst.saiban_main_tanto_flg DESC, tst.account_seq ASC SEPARATOR ', ') tanto_name
FROM
  t_saiban_tanto tst
  LEFT JOIN m_account ma
    ON tst.account_seq = ma.account_seq
WHERE
  tst.tanto_type IN (/* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2', /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3')
  AND tst.saiban_seq IN /* saibanSeqList */(1) 
GROUP BY
  tst.saiban_seq
  , tst.tanto_type
;