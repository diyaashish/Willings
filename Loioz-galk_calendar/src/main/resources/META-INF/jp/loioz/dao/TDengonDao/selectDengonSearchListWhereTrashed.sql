SELECT
  search_trashed_list.dengon_seq
  , search_trashed_list.gyomu_history_seq
  , search_trashed_list.saiban_seq
  , search_trashed_list.saiban_branch_no
  , search_trashed_list.title
  , search_trashed_list.send_account_name
  , search_trashed_list.receive_account_seq_list
  , search_trashed_list.dengon_status_id
  , search_trashed_list.open_flg
  , search_trashed_list.important_flg
  , DATE_FORMAT( search_trashed_list.created_at, '%Y/%m/%d %H:%i') AS created_at 
FROM
  ( 
    ( 
      SELECT
        td.dengon_seq
        , td.gyomu_history_seq
        , tgh.saiban_seq
        , ts.saiban_branch_no
        , td.title
        , CONCAT( 
          COALESCE(ma.account_name_sei, '')
          , ' '
          , COALESCE(ma.account_name_mei, '')
        ) AS send_account_name
        , td.receive_account_seq AS receive_account_seq_list
        , tdas.dengon_status_id
        , tdas.open_flg
        , tdas.important_flg
        , tdas.created_at
      FROM
        t_dengon AS td 
        LEFT JOIN t_gyomu_history tgh 
          ON td.gyomu_history_seq = tgh.gyomu_history_seq 
        LEFT JOIN t_saiban ts 
          ON tgh.saiban_seq = ts.saiban_seq 
        INNER JOIN m_account AS ma 
          ON ma.account_seq = td.send_account_seq 
        INNER JOIN t_dengon_account_status AS tdas 
          ON td.dengon_seq = tdas.dengon_seq 
          AND tdas.account_seq = /* loginAccountSeq */0 
      WHERE
        ( 
          td.title LIKE /* @infix(searchMailText) */'null' 
          OR td.body LIKE /* @infix(searchMailText) */'null' 
          OR td.send_account_seq IN ( 
            SELECT
              ma.account_seq 
            FROM
              m_account AS ma 
            WHERE
              CONCAT( 
                COALESCE(ma.account_name_sei, '')
                , COALESCE(ma.account_name_mei, '')
              ) LIKE /* @infix(searchMailText) */'null'
          )
         ) 
        AND tdas.trashed_flg = '1' 
        AND tdas.deleted_at IS NULL 
        AND tdas.deleted_by IS NULL 
      ORDER BY
        tdas.created_at DESC
      LIMIT
        0, /* 50 * (page + 1) */50
    ) 
    UNION ALL
    ( 
      SELECT
        td.dengon_seq
        , td.gyomu_history_seq
        , tgh.saiban_seq
        , ts.saiban_branch_no
        , td.title
        , NULL AS send_account_name
        , td.receive_account_seq AS receive_account_seq_list
        , NULL AS dengon_status_id
        , NULL AS open_flg
        , NULL AS important_flg
        , td.created_at
      FROM
        t_dengon AS td 
        LEFT JOIN t_gyomu_history tgh 
          ON td.gyomu_history_seq = tgh.gyomu_history_seq 
        LEFT JOIN t_saiban ts 
          ON tgh.saiban_seq = ts.saiban_seq 
      WHERE
        ( 
          td.title LIKE /* @infix(searchMailText) */'null' 
          OR td.body LIKE /* @infix(searchMailText) */'null' 
          OR td.send_account_seq IN ( 
            SELECT
              ma.account_seq 
            FROM
              m_account AS ma 
            WHERE
              CONCAT( 
                COALESCE(ma.account_name_sei, '')
                , COALESCE(ma.account_name_mei, '')
              ) LIKE /* @infix(searchMailText) */'null'
          ) 
        /*%if searchAccountSeqList != null && searchAccountSeqList.size() > 0 */
          OR ( 
          /*%for searchAccountSeq : searchAccountSeqList */
            FIND_IN_SET(/* searchAccountSeq */0, td.receive_account_seq) 
            /*%if searchAccountSeq_has_next */
            /*# "OR" */
            /*%end */
          /*%end*/
          ) 
        /*%end*/
        ) 
        AND td.send_account_seq = /* loginAccountSeq */0 
        AND td.send_trashed_flg = '1' 
        AND td.deleted_at IS NULL 
        AND td.deleted_by IS NULL 
      ORDER BY
        td.created_at DESC
      LIMIT
        0, /* 50 * (page + 1) */50
    )
  ) search_trashed_list 
ORDER BY
  search_trashed_list.created_at DESC 
LIMIT
  /* 50 * page */0
  , 50;