SELECT
  td.dengon_seq
  , td.gyomu_history_seq
  , tgh.saiban_seq
  , ts.saiban_branch_no
  , td.title
  , CONCAT(COALESCE(ma.account_name_sei,''),' ',COALESCE(ma.account_name_mei,'')) AS send_account_name
  , td.receive_account_seq AS receive_account_seq_list
  , tdas.dengon_status_id
  , tdas.open_flg
  , tdas.important_flg
  , DATE_FORMAT(tdas.created_at, '%Y/%m/%d %H:%i') AS created_at
FROM
  t_dengon AS td
  LEFT JOIN t_gyomu_history tgh
    ON td.gyomu_history_seq = tgh.gyomu_history_seq
  LEFT JOIN t_saiban ts
    ON tgh.saiban_seq = ts.saiban_seq
  INNER JOIN m_account AS ma
    ON ma.account_seq = td.send_account_seq
  INNER JOIN t_dengon_account_status AS tdas
    ON tdas.dengon_seq = td.dengon_seq
  LEFT OUTER JOIN t_dengon_folder_in AS tdfi
    ON tdfi.dengon_seq = td.dengon_seq
WHERE
  tdas.account_seq = /* loginAccountSeq */0
  AND tdas.trashed_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0'
  /*%if @jp.loioz.common.constant.CommonConstant$MailFilterType@UNREAD.equalsByCode(mailFilterType) */
  AND tdas.open_flg = '0'
  /*%end*/
  /*%if @jp.loioz.common.constant.CommonConstant$MailFilterType@REQUIRED_REPLY.equalsByCode(mailFilterType) */
  AND tdas.dengon_status_id = /* @jp.loioz.common.constant.CommonConstant$DengonStatus@YOUHENSHIN */'1'
  /*%end*/
  /*%if @jp.loioz.common.constant.CommonConstant$MailFilterType@IMPORTANT.equalsByCode(mailFilterType) */
  AND tdas.important_flg = '1'
  /*%end*/
  /*%if @jp.loioz.common.utility.StringUtils@isNotEmpty(searchMailText) */
  AND (
    td.title LIKE /* @infix(searchMailText) */null
    OR td.body LIKE /* @infix(searchMailText) */null
    OR td.send_account_seq IN (
      SELECT
        ma.account_seq
      FROM
        m_account AS ma
      WHERE
        CONCAT(COALESCE(ma.account_name_sei,''),COALESCE(ma.account_name_mei,'')) LIKE /* @infix(searchMailText) */null
    )
  )
  /*%end*/
  /*%if dengonFolderSeq != null */
  AND tdfi.dengon_folder_seq = /* dengonFolderSeq */null
  /*%else*/
  AND td.dengon_seq NOT IN (
    SELECT
      td.dengon_seq
    FROM
      t_dengon AS td
      INNER JOIN t_dengon_folder_in AS tdfi
        ON td.dengon_seq = tdfi.dengon_seq
      INNER JOIN t_dengon_folder AS tdf
        ON tdfi.dengon_folder_seq = tdf.dengon_folder_seq
        AND tdf.account_seq = /* loginAccountSeq */null
  )
  /*%end*/
  AND td.draft_flg = '0'
  AND td.deleted_at IS NULL
  AND td.deleted_by IS NULL
ORDER BY
  tdas.created_at DESC
LIMIT
  /* 50 * page */0, 50;