SELECT
  tny.nyushukkin_yotei_seq 
  /** 顧客の情報 */
  , tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name 
  /** 顧客の情報 end */
  /** 案件の情報 */
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  /** 案件の情報 end */
  /** 案件顧客情報 */
  , tac.anken_status
  /** 案件顧客情報end */
  /** 支払者の情報*/
  , COALESCE(shiharaisha_customer.person_id, shiharaisha_kanyosha.person_id) AS shiharaisha_id
  , TRIM(CONCAT(COALESCE(shiharaisha_customer.customer_name_sei,''),' ',COALESCE(shiharaisha_customer.customer_name_mei,''))) AS shiharai_customer_name
  , TRIM(CONCAT(COALESCE(shiharaisha_kanyosha.customer_name_sei,''),' ',COALESCE(shiharaisha_kanyosha.customer_name_mei,''))) AS shiharai_kanyosha_name
  /** 支払者の情報 end */
  /** 支払先の情報 */
  , tny.nyukin_saki_koza_seq
  , tgk.tenant_seq AS shiharai_saki_tenant_seq
  , tgk.account_seq AS shiharai_saki_account_seq
  , tgk.label_name AS shiharai_saki_name
  /** 支払先の情報end */
  , tny.nyushukkin_yotei_date
  , tny.nyushukkin_yotei_gaku
  , tny.nyushukkin_date
  , tny.nyushukkin_gaku
  , tsk.seisan_seq
  , tsk.seisan_id
  , tsk.seisan_gaku
  , ( 
    SELECT
      COALESCE(SUM(tny1.nyushukkin_gaku), 0) 
    FROM
      t_nyushukkin_yotei tny1 
    WHERE
      nyushukkin_type = /* @jp.loioz.common.constant.CommonConstant$NyushukkinType@NYUKIN */'1' 
      AND tny.seisan_seq = tny1.seisan_seq 
      AND tny1.nyushukkin_date IS NOT NULL
  ) AS nyukin_total
FROM
  t_nyushukkin_yotei tny 
  INNER JOIN t_person tc 
    USING (customer_id) 
  INNER JOIN t_anken ta 
    USING (anken_id) 
  INNER JOIN t_anken_customer tac
    ON tny.anken_id = tac.anken_id
    AND tny.customer_id = tac.customer_id
  LEFT JOIN t_ginko_koza tgk 
    ON tny.nyukin_saki_koza_seq = tgk.ginko_account_seq 
  LEFT JOIN t_person shiharaisha_customer 
    ON tny.nyukin_customer_id = shiharaisha_customer.customer_id 
  LEFT JOIN t_kanyosha tk 
    ON tny.nyukin_kanyosha_seq = tk.kanyosha_seq 
  LEFT JOIN t_person shiharaisha_kanyosha 
    ON tk.person_id = shiharaisha_kanyosha.person_id 
  LEFT JOIN t_seisan_kiroku tsk 
    USING (seisan_seq)
WHERE
  1 = 1 
  AND tny.nyushukkin_yotei_seq IN /* nyukinSeqList */(1) 
ORDER BY
  ( 
    tny.nyushukkin_yotei_date < NOW() 
    AND tny.nyushukkin_date IS NULL
  ) DESC
  , tny.nyushukkin_yotei_date ASC
