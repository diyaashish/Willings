SELECT
  tny.nyushukkin_yotei_seq 
  /** 顧客情報 */
  , tc.customer_id
  , TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name 
  /** 顧客情報 end */
  /** 案件情報 */
  , ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  /** 案件情報 end */
  /** 案件顧客情報 */
  , tac.anken_status
  /** 案件顧客情報end */
  /** 支払者の口座情報 */
  , tgk.ginko_account_seq AS shiharaisha_koza_seq
  , tgk.tenant_seq AS shiharaisha_tenant_seq
  , tgk.account_seq AS shiharaisha_account_seq
  , tgk.label_name AS shiharaisha_name
  /** 支払者の口座情報 end */
  /** 支払先の口座情報 */
  , COALESCE(shiharai_saki_customer.person_id, shiharai_saki_kanyosha.person_id) AS shiharai_saki_id
  , TRIM(CONCAT(COALESCE(shiharai_saki_customer.customer_name_sei,''),' ',COALESCE(shiharai_saki_customer.customer_name_mei,''))) AS shiharai_saki_customer_name
  , TRIM(CONCAT(COALESCE(shiharai_saki_kanyosha.customer_name_sei,''),' ',COALESCE(shiharai_saki_kanyosha.customer_name_mei,''))) AS shiharai_saki_kanyosha_name
  /** 支払先の口座情報 end */
  , tny.nyushukkin_yotei_date
  , tny.nyushukkin_yotei_gaku
  , tny.nyushukkin_date
  , tny.nyushukkin_gaku
  , tsk.seisan_seq
  , tsk.seisan_id
  , tsk.seisan_gaku
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
    ON tny.shukkin_saki_koza_seq = tgk.ginko_account_seq 
  LEFT JOIN t_person shiharai_saki_customer
    ON tny.shukkin_customer_id = shiharai_saki_customer.customer_id
  LEFT JOIN t_kanyosha tk
    ON tny.shukkin_kanyosha_seq = tk.kanyosha_seq
  LEFT JOIN t_person shiharai_saki_kanyosha
    ON tk.person_id = shiharai_saki_kanyosha.person_id
  LEFT JOIN t_seisan_kiroku tsk 
    USING (seisan_seq)  
WHERE
  AND tny.nyushukkin_yotei_seq IN /* shukkinSeqList */(1)
ORDER BY
  ( 
    tny.nyushukkin_yotei_date < NOW() 
    AND tny.nyushukkin_date IS NULL
  ) DESC
  , tny.nyushukkin_yotei_date ASC