SELECT 
  ta.anken_id
  -- accountSeqが担当している案件であれば1を表示
  , (
    SELECT
      /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' 
    FROM
      t_anken_tanto tat
    WHERE
      tat.anken_id = ta.anken_id
      AND tat.account_seq = /* accountSeq */'1'
    LIMIT
      1
  ) AS tanto_anken_flg
FROM
  t_anken ta
WHERE
  ta.anken_id IN /* ankenIdList */(1) 
; 