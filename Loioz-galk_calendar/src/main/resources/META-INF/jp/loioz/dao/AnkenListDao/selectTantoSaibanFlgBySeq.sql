SELECT 
  ts.saiban_seq
  -- accountSeqが担当している裁判であれば1を表示
  , (
    SELECT
      /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' 
    FROM
      t_saiban_tanto tst
    WHERE
      tst.saiban_seq = ts.saiban_seq
      AND tst.account_seq = /* accountSeq */'1'
    LIMIT
      1
  ) AS tanto_saiban_flg
FROM
  t_saiban ts
WHERE
  ts.saiban_seq IN /* saibanSeqList */(1) 
;