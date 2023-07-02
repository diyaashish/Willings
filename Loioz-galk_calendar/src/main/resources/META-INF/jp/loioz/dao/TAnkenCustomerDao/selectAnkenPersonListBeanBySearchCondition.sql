SELECT
  tp.person_id
  , tp.customer_id
  , tp.customer_name_sei
  , tp.customer_name_sei_kana
  , tp.customer_name_mei
  , tp.customer_name_mei_kana
  , tp.customer_flg
  , tp.advisor_flg
  , tp.customer_type
  , tp.customer_created_date
  , ta.anken_id
  , ta.anken_name
  , ta.anken_type
  , ta.bunya_id
  , tac.anken_status
FROM
  t_anken_customer tac 
  INNER JOIN t_anken ta 
    USING (anken_id) 
  INNER JOIN t_person tp 
    ON tac.customer_id = tp.customer_id 
WHERE
  1 = 1 
  /*%if searchConditions.isNotEmpty(searchConditions.ankenId) */
  AND ta.anken_id = /* searchConditions.ankenId */NULL
  /*%end */
ORDER BY 
  /*%if @jp.loioz.common.constant.SortConstant$CaseAccgCustomerListItem@CREATED_AT_ASC.equalsByCode(searchConditions.item.cd) */
  tp.customer_created_date ASC
  /*%end */

  /*%if @jp.loioz.common.constant.SortConstant$CaseAccgCustomerListItem@CREATED_AT_DESC.equalsByCode(searchConditions.item.cd) */
  tp.customer_created_date DESC
  /*%end */

  /*%if @jp.loioz.common.constant.SortConstant$CaseAccgCustomerListItem@ANKEN_CUSTOMER_STATUS.equalsByCode(searchConditions.item.cd) */
  tac.anken_status
  /*%end */
;