SELECT
  tt.task_seq
FROM
  t_task tt 
  LEFT JOIN t_anken ta
    USING (anken_id)
  INNER JOIN t_task_worker ttw 
    USING (task_seq) 
WHERE
  ttw.worker_account_seq = /* accountId */'1' 
  AND ( 
    tt.title LIKE /* @infix(searchWord) */'' 
    OR tt.content LIKE /* @infix(searchWord) */'' 
    OR ta.anken_name LIKE /* @infix(searchWord) */'' 
    -- 検索ワードがチェックリスト名と部分一致するか 
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_task_check_item ttci 
      WHERE
        ttci.task_seq = tt.task_seq 
        AND ttci.item_name LIKE /* @infix(searchWord) */''
    ) 
    -- 検索ワードがタスクに紐づけられた案件の顧客姓名かなと部分一致するか 
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_anken_customer tac 
        INNER JOIN t_person tc 
          USING (customer_id) 
      WHERE
        tac.anken_id = ta.anken_id 
        AND CONCAT( 
          COALESCE(tc.customer_name_sei_kana, '')
          , COALESCE(tc.customer_name_mei_kana, '')
        ) LIKE /* @infix(searchWord) */''
    ) 
    -- 検索ワードがタスクに紐づけられた案件の顧客姓名と部分一致するか 
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_anken_customer tac 
        INNER JOIN t_person tc 
          USING (customer_id) 
      WHERE
        tac.anken_id = ta.anken_id 
        AND CONCAT( 
          COALESCE(tc.customer_name_sei, '')
          , COALESCE(tc.customer_name_mei, '')
        ) LIKE /* @infix(searchWord) */''
    ) 
    -- 検索ワードがタスク担当者姓名と部分一致するか 
    OR EXISTS ( 
      SELECT
        * 
      FROM
        m_account ma 
      WHERE
        ma.account_seq IN ( 
          SELECT
            worker_account_seq 
          FROM
            t_task_worker 
          WHERE
            task_seq = tt.task_seq 
            AND entrust_flg = 0
        ) 
        AND CONCAT( 
          COALESCE(ma.account_name_sei, '')
          , COALESCE(ma.account_name_mei, '')
        ) LIKE /* @infix(searchWord) */''
    ) 
    -- 検索ワードがタスク担当者姓名かなと部分一致するか 
    OR EXISTS ( 
      SELECT
        * 
      FROM
        m_account ma 
      WHERE
        ma.account_seq IN ( 
          SELECT
            worker_account_seq 
          FROM
            t_task_worker 
          WHERE
            task_seq = tt.task_seq 
            AND entrust_flg = 0
        ) 
        AND CONCAT( 
          COALESCE(ma.account_name_sei_kana, '')
          , COALESCE(ma.account_name_mei_kana, '')
        ) LIKE /* @infix(searchWord) */''
    )
    -- 検索ワードがタスクのコメントと部分一致するか 
    OR EXISTS ( 
      SELECT
        * 
      FROM
        t_task_history tth 
      WHERE
        tth.task_history_type = /* @jp.loioz.common.constant.CommonConstant$TaskHistoryType@COMMENT */'1'
        AND tth.task_seq = tt.task_seq
        AND tth.comment LIKE /* @infix(searchWord) */''
    )
  )
ORDER BY
  tt.created_at DESC
;