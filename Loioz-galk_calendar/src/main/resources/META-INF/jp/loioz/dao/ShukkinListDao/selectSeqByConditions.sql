SELECT
  tny.nyushukkin_yotei_seq 
FROM
  t_nyushukkin_yotei tny 
  INNER JOIN t_person tc 
    USING (customer_id) 
  INNER JOIN t_anken ta 
    USING (anken_id) 
  LEFT JOIN t_seisan_kiroku tsk 
    USING (seisan_seq) 
WHERE
  1 = 1 
  AND tny.nyushukkin_type = /* @jp.loioz.common.constant.CommonConstant$NyushukkinType@SHUKKIN */'2' 
  /** -- 口座情報の条件 */
/*%if conditions.isNotEmpty(conditions.kozaSeq) */
  AND tny.shukkin_saki_koza_seq = /*conditions.kozaSeq*/'%' 
/*%end */
  /** -- 口座情報の条件 end */
  /** -- 顧客情報の条件 */
/*%if conditions.isNotEmpty(conditions.customerId) */
  AND tc.customer_id = /*conditions.customerId*/'%' 
/*%end */
/*%if conditions.isNotEmpty(conditions.customerName) */
  AND CONCAT(COALESCE(tc.customer_name_sei,''),COALESCE(tc.customer_name_mei,'')) LIKE /*@infix(conditions.customerName)*/'%' 
/*%end */
  /** -- 顧客情報の条件 end */
  /** -- 案件情報の条件 */
  /** -- 担当弁護士の検索 */
/*%if conditions.isNotEmpty(conditions.tantosha.tantoLawyerSeqList) */
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_anken_tanto tat_lawyer 
    WHERE
      tat_lawyer.anken_id = ta.anken_id 
      AND tat_lawyer.tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND tat_lawyer.account_seq IN /* conditions.tantosha.tantoLawyerSeqList */(1)
  ) 
/*%end */
  /** -- 担当事務の検索 */
/*%if conditions.isNotEmpty(conditions.tantosha.tantoJimuSeqList) */
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_anken_tanto tat_jimu 
    WHERE
      tat_jimu.anken_id = ta.anken_id 
      AND tat_jimu.tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND tat_jimu.account_seq IN /* conditions.tantosha.tantoJimuSeqList */(1)
  ) 
/*%end */
  /** -- 案件情報の条件 end */
  /** -- その他検索条件 */
/*%if conditions.shukkinSearchRifineType == @jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType@PROCESSED */
  /** -- 処理済みのデータのみ表示 */
  AND tny.nyushukkin_date IS NOT NULL 
/*%elseif conditions.shukkinSearchRifineType == @jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchRifineType@UNPROCESSED */
  /** -- 未処理のデータのみ表示 */
  AND tny.nyushukkin_date IS NULL 
/*%else */
  /** --何もしない */
/*%end */
  AND ( 
  /*%if conditions.shukkinSearchYoteiDateType == @jp.loioz.app.user.nyushukkinYotei.shukkin.enums.ShukkinSearchYoteiDateType@SPECIFICATION */
    tny.nyushukkin_yotei_date >= /*conditions.searchDateFrom*/'2019-01-01' 
    AND tny.nyushukkin_yotei_date <= /*conditions.searchDateTo*/'2019-01-01' 
    /*%else */
    DATE_FORMAT(tny.nyushukkin_yotei_date, '%y%M') = DATE_FORMAT(/*conditions.yearMonth*/'2019-01-01', '%y%M')
  /*%end */
  ) 
  /** -- その他検索条件 end */
GROUP BY
  nyushukkin_yotei_seq 
ORDER BY
  ( 
    tny.nyushukkin_yotei_date < NOW() 
    AND tny.nyushukkin_date IS NULL
  ) DESC
  , tny.nyushukkin_yotei_date ASC