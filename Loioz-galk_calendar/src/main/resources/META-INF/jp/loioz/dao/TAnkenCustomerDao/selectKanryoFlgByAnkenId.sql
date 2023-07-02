SELECT
  tac.anken_id
  -- 完了フラグがNULLのレコードを数をカウントし0なら MIN(tac.kanryo_flg) を実行。そうでなければ null。
  , CASE 
      WHEN COUNT(CASE WHEN tac.kanryo_flg IS NULL THEN 1 END) = 0 
      THEN MIN(tac.kanryo_flg) 
    END kanryo_flg
FROM
  t_anken_customer tac
WHERE
  tac.anken_id IN /* ankenIdList */(1)
GROUP BY
  tac.anken_id
;