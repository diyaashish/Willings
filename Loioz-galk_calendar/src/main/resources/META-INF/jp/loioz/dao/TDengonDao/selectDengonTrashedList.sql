SELECT
  trashed_list.dengon_seq
  , trashed_list.gyomu_history_seq
  , trashed_list.saiban_seq
  , trashed_list.saiban_branch_no
  , trashed_list.title
  , trashed_list.send_account_name
  , trashed_list.receive_account_seq_list
  , trashed_list.draft_flg
  , trashed_list.dengon_status_id
  , trashed_list.open_flg
  , trashed_list.important_flg
  , DATE_FORMAT(trashed_list.created_at, '%Y/%m/%d %H:%i') AS created_at
  , DATE_FORMAT(trashed_list.updated_at, '%Y/%m/%d %H:%i') AS updated_at 
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
        , NULL AS draft_flg
        , tdas.dengon_status_id
        , tdas.open_flg
        , tdas.important_flg
        , tdas.created_at
        , tdas.updated_at 
      FROM
        t_dengon AS td 
        LEFT JOIN t_gyomu_history tgh 
          ON td.gyomu_history_seq = tgh.gyomu_history_seq 
        LEFT JOIN t_saiban ts 
          ON tgh.saiban_seq = ts.saiban_seq 
        INNER JOIN t_dengon_account_status AS tdas 
          ON td.dengon_seq = tdas.dengon_seq 
        INNER JOIN m_account AS ma 
          ON ma.account_seq = td.send_account_seq 
      WHERE
        tdas.account_seq = /* loginAccountSeq */0 
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
        , td.draft_flg
        , NULL AS dengon_status_id
        , '1' AS open_flg
        , NULL AS important_flg
        , td.created_at
        , td.updated_at 
      FROM
        t_dengon AS td 
        LEFT JOIN t_gyomu_history tgh 
          ON td.gyomu_history_seq = tgh.gyomu_history_seq 
        LEFT JOIN t_saiban ts 
          ON tgh.saiban_seq = ts.saiban_seq 
      WHERE
        td.send_account_seq = /* loginAccountSeq */0 
        AND td.send_trashed_flg = '1' 
        AND td.deleted_at IS NULL 
        AND td.deleted_by IS NULL 
      ORDER BY
        td.created_at DESC 
      LIMIT
        0, /* 50 * (page + 1) */50
    )
  ) trashed_list 
ORDER BY
  trashed_list.created_at DESC 
LIMIT
  /* 50 * page */0
  , 50;
