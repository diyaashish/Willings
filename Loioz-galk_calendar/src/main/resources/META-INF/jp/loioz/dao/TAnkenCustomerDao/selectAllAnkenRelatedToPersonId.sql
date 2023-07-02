SELECT
  * 
FROM
  ( 
    -- パラメータの人が顧客になっている案件取得
    SELECT
      ta.anken_id
      , ta.bunya_id
      , ta.anken_name
      , ta.anken_type
      , tac.anken_status AS anken_status
      -- ソート番号を作成。未完了：1、完了：2、不受任：3
      , CASE tac.anken_status 
        WHEN '6' THEN 2 
        WHEN '9' THEN 3 
        ELSE 1 
        END AS orderby_column 
    FROM
      t_anken ta 
      INNER JOIN t_anken_customer tac 
        ON ta.anken_id = tac.anken_id 
    WHERE
      tac.customer_id = /* personId */1
    UNION ALL
    -- パラメータの人が関与者になっている案件取得
    SELECT
      ta.anken_id
      , ta.bunya_id
      , ta.anken_name
      , ta.anken_type
      -- 案件ステータスを作成。案件顧客全員の完了フラグが完了になっているか判定する。完了していれば：6、完了していなければ：空
      , CASE 
        WHEN ( 
          SELECT
            -- 完了フラグがNULLの数をカウントし0個なら MIN(tac.kanryo_flg) を実行。そうでなければ 0（未完了）。
            CASE 
              WHEN COUNT(CASE WHEN tac.kanryo_flg IS NULL THEN 1 END) = 0 
                THEN MIN(tac.kanryo_flg) 
              ELSE 0 
              END kanryo_flg 
          FROM
            t_anken_customer tac 
          WHERE
            tac.anken_id = ta.anken_id 
          GROUP BY
            tac.anken_id
        ) = 0 
          THEN '' 
        ELSE 6 
        END AS anken_status
        -- ソート番号を作成。案件顧客全員の完了フラグが完了になっているか判定する。 未完了：4、完了：5
      , CASE 
        WHEN ( 
          SELECT
            -- 完了フラグがNULLの数をカウントし0個なら MIN(tac.kanryo_flg) を実行。そうでなければ 0（未完了）。
            CASE 
              WHEN COUNT(CASE WHEN tac.kanryo_flg IS NULL THEN 1 END) = 0 
                THEN MIN(tac.kanryo_flg) 
              ELSE 0 
              END kanryo_flg 
          FROM
            t_anken_customer tac 
          WHERE
            tac.anken_id = ta.anken_id 
          GROUP BY
            tac.anken_id
        ) = 0 
          THEN 4 
        ELSE 5 
        END AS orderby_column 
    FROM
      t_anken ta 
      INNER JOIN t_kanyosha tk 
        ON ta.anken_id = tk.anken_id 
    WHERE
      tk.person_id = /* personId */1
  ) AS anken_data 
ORDER BY
  orderby_column
  , anken_id
;
