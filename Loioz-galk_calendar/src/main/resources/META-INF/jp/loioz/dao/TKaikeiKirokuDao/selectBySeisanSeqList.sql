SELECT DISTINCT
  tkk.kaikei_kiroku_seq
  , tkk.nyushukkin_yotei_seq
  , tkk.seisan_seq
  , tkk.customer_id
  , tkk.anken_id
  , tkk.nyukin_gaku 
  , tkk.uncollectable_flg
FROM
  t_kaikei_kiroku tkk 
  LEFT OUTER JOIN t_nyushukkin_yotei tny 
    ON tkk.nyushukkin_yotei_seq = tny.nyushukkin_yotei_seq 
  INNER JOIN t_seisan_kiroku tsk 
    ON tkk.seisan_seq = tkk.seisan_seq 
WHERE
  ( 
    tkk.nyushukkin_yotei_seq IN ( 
      SELECT
        tny2.nyushukkin_yotei_seq 
      FROM
        t_nyushukkin_yotei tny2 
      WHERE
        tny2.seisan_seq IN /* seisanSeqList */(NULL)
    ) 
    AND (
    tkk.uncollectable_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
    OR tkk.uncollectable_flg IS NULL
    )
  ) 
  OR ( 
    tkk.seisan_seq IN /* seisanSeqList */(NULL) 
    AND tkk.uncollectable_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
  );
