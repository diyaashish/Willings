SELECT
  * 
FROM
  t_kaikei_kiroku 
WHERE
  nyushukkin_yotei_seq IN /* nyushukkinYoteiSeqList */(NULL)
  AND uncollectable_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1';
