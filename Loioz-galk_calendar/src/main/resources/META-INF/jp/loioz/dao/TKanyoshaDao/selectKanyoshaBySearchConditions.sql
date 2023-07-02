SELECT
  tk.kanyosha_seq
  , tk.person_id
  , tc.customer_flg
  , tc.advisor_flg
  , tc.customer_type
  , tc.customer_name_sei
  , tc.customer_name_sei_kana
  , tc.customer_name_mei
  , tc.customer_name_mei_kana
  , tc.zip_code
  , tc.address1
  , tc.address2
  , tc.address_remarks
  , tc.ginko_name
  , tc.shiten_name
  , tc.shiten_no
  , tc.koza_type
  , tc.koza_no
  , tc.koza_name
  , tc.koza_name_kana
  , tc.koza_remarks
  , tk.anken_id
  , tk.disp_order
  , tk.kankei
  , tk.remarks AS kanyosha_remarks
  , tpal.jimusho_name
FROM
  t_kanyosha tk 
  INNER JOIN t_person tc 
    USING (person_id) 
  LEFT OUTER JOIN t_person_add_lawyer tpal
    ON tc.person_id = tpal.person_id
    AND tc.customer_type = /* @jp.loioz.common.constant.CommonConstant$CustomerType@LAWYER */'2'
WHERE
  1 = 1 
  /** ankenIdは検索に必須なので、NULLの場合検索結果はNULLにする */
  AND tk.anken_id = /* conditions.ankenId */NULL 
ORDER BY
  disp_order
;
