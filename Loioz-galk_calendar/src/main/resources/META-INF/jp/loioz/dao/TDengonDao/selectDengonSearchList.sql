SELECT
  td.dengon_seq
  , td.gyomu_history_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  /*%if @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_CUSTOM.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_SUB_CUSTOM.equalsByCode(mailBoxType) */
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS send_account_name
  , tdas.dengon_status_id
  , tdas.open_flg
  , tdas.important_flg
  /*%end*/
  , td.receive_account_seq AS receive_account_seq_list
  /*%if @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_CUSTOM.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_SUB_CUSTOM.equalsByCode(mailBoxType) */
  , DATE_FORMAT(tdas.created_at, '%Y/%m/%d %H:%i') AS created_at
  /*%else*/
  , DATE_FORMAT(td.created_at, '%Y/%m/%d %H:%i') AS created_at
  /*%end*/
FROM
  t_dengon AS td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
  /*%if @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE.equalsByCode(mailBoxType)
  	|| @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_CUSTOM.equalsByCode(mailBoxType)
  	|| @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_SUB_CUSTOM.equalsByCode(mailBoxType) */
  INNER JOIN m_account AS ma
    ON ma.account_seq = td.send_account_seq
  INNER JOIN t_dengon_account_status AS tdas
    ON td.dengon_seq = tdas.dengon_seq
    AND tdas.account_seq = /* loginAccountSeq */null
  /*%end*/
  /*%if dengonFolderSeq != null */
  INNER JOIN t_dengon_folder_in AS tdfi
    ON tdfi.dengon_seq = td.dengon_seq
  /*%end*/
WHERE
  (
    td.title LIKE /* @infix(searchMailText) */null
    OR td.body LIKE /* @infix(searchMailText) */null
    OR td.send_account_seq IN (
      SELECT
        ma.account_seq
      FROM
        m_account AS ma
      WHERE
        CONCAT(COALESCE(ma.account_name_sei,''),COALESCE(ma.account_name_mei,'')) LIKE /* @infix(searchMailText) */null
      OR CONCAT(COALESCE(ma.account_name_sei_kana,''),COALESCE(ma.account_name_mei_kana,'')) LIKE /* @infix(searchMailText) */null
    )
    /*%if searchAccountSeqList != null && searchAccountSeqList.size() > 0 */
    OR (
      /*%for searchAccountSeq : searchAccountSeqList */
      FIND_IN_SET(/* searchAccountSeq */null, td.receive_account_seq)
        /*%if searchAccountSeq_has_next */
          /*# "OR" */
        /*%end */
      /*%end*/
    )
    /*%end*/
  )
  /*%if @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE.equalsByCode(mailBoxType) */
  AND tdas.deleted_at IS NULL
  AND tdas.deleted_by IS NULL
  AND tdas.trashed_flg = '0'
  /*%elseif @jp.loioz.common.constant.CommonConstant$MailBoxType@SEND.equalsByCode(mailBoxType) */
  AND td.send_account_seq = /* loginAccountSeq */0
  AND td.draft_flg = '0'
  AND td.send_trashed_flg = '0'
  AND td.deleted_at IS NULL
  AND td.deleted_by IS NULL
  /*%elseif @jp.loioz.common.constant.CommonConstant$MailBoxType@DRAFT.equalsByCode(mailBoxType) */
  AND td.send_account_seq = /* loginAccountSeq */0
  AND td.draft_flg = '1'
  AND td.send_trashed_flg = '0'
  AND td.deleted_at IS NULL
  AND td.deleted_by IS NULL
  /*%elseif @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_CUSTOM.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_SUB_CUSTOM.equalsByCode(mailBoxType) */
  /*%if dengonFolderSeq != null */
  AND tdfi.dengon_folder_seq = /* dengonFolderSeq */null
  /*%end*/
  AND tdas.trashed_flg = '0'
  AND tdas.account_seq = /* loginAccountSeq */0
  AND tdas.deleted_at IS NULL
  AND tdas.deleted_by IS NULL
  /*%end*/
ORDER BY
  /*%if @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_CUSTOM.equalsByCode(mailBoxType)
   || @jp.loioz.common.constant.CommonConstant$MailBoxType@RECEIVE_SUB_CUSTOM.equalsByCode(mailBoxType) */
  tdas.created_at DESC
  /*%else*/
  td.created_at DESC
  /*%end*/
LIMIT
  /* 50 * page */0, 50;