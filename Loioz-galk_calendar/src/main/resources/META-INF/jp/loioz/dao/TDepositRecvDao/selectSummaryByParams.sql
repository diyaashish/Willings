SELECT
  tac.customer_id AS person_id
  , tac.anken_id
  , ta.anken_name
  , COALESCE(tp.customer_name_sei, '') AS person_name_sei
  , COALESCE(tp.customer_name_mei, '') AS person_name_mei
  , SUM( 
    CASE tdr.deposit_complete_flg 
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(tdr.deposit_amount, 0)
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
      ELSE 0
      END
  ) AS total_deposit_amount
  , SUM( 
    CASE tdr.deposit_complete_flg 
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(tdr.withdrawal_amount, 0)
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
      ELSE 0 
      END
  ) AS total_withdrawal_amount
  , SUM( 
    CASE tdr.deposit_complete_flg 
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(tdr.deposit_amount, 0)
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
      ELSE 0 
      END
  ) + SUM( 
    CASE tdr.deposit_complete_flg 
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE( 
        CASE 
          WHEN tdr.withdrawal_amount > 0 
            THEN tdr.withdrawal_amount * - 1 
          ELSE tdr.withdrawal_amount 
          END
        , 0
      ) 
      WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
      ELSE 0 
      END
  ) total_deposit_balance_amount
  , (
    SELECT
      COALESCE(SUM( 
        CASE tdr.deposit_complete_flg 
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1' THEN COALESCE(tdr.withdrawal_amount, 0)
          WHEN /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_OFF */'0' THEN 0 
        ELSE 0 
        END
      ), 0) AS withdrawal_amount
    FROM
      t_deposit_recv tdr
    WHERE
      tdr.anken_id = /* ankenId */1 
      AND tdr.person_id = /* personId */1 
      AND tdr.tenant_bear_flg = /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1'
  ) AS total_tenant_bear_amount
FROM
  t_anken_customer tac 
  INNER JOIN t_anken ta 
    ON tac.anken_id = ta.anken_id 
  INNER JOIN t_person tp 
    ON tp.customer_id = tac.customer_id 
  LEFT JOIN t_deposit_recv tdr 
    ON tdr.anken_id = tac.anken_id 
    -- 事務所負担フラグが立っているものは、出金額に含めない
    AND tac.customer_id = tdr.person_id 
    AND (tdr.tenant_bear_flg IS NULL OR tdr.tenant_bear_flg != /* @jp.loioz.common.constant.CommonConstant$SystemFlg@FLG_ON */'1')
WHERE
  tac.anken_id = /* ankenId */1 
  AND tac.customer_id = /* personId */1 
GROUP BY
  tac.anken_id
  , tac.customer_id;
