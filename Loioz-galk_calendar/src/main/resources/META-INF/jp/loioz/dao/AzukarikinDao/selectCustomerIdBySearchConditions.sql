SELECT
  tc.customer_id 
FROM
  t_person tc 
  INNER JOIN t_anken_customer tac 
    USING (customer_id) 
  INNER JOIN t_anken ta 
    USING (anken_id) 
WHERE
  1 = 1 
  /** -- 案件情報の条件 */
/*%if condition.isNotEmpty(condition.status.ankenStatus) */
  AND tac.anken_status IN /*condition.status.ankenStatus*/(1) 
/*%end */
  /** -- 売上計上先の検索 */
/*%if condition.isNotEmpty(condition.tantosha.salesOwnerSeqList) */
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_anken_tanto tat_sales 
    WHERE
      tat_sales.anken_id = ta.anken_id 
      AND tat_sales.tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@SALES_OWNER */'1' 
      AND tat_sales.account_seq IN /* condition.tantosha.salesOwnerSeqList */(1)
  ) 
/*%end */
  /** -- 担当弁護士の検索 */
/*%if condition.isNotEmpty(condition.tantosha.tantoLawyerSeqList) */
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_anken_tanto tat_lawyer 
    WHERE
      tat_lawyer.anken_id = ta.anken_id 
      AND tat_lawyer.tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@LAWYER */'2' 
      AND tat_lawyer.account_seq IN /* condition.tantosha.tantoLawyerSeqList */(1)
  ) 
/*%end */
  /** -- 担当事務の検索 */
/*%if condition.isNotEmpty(condition.tantosha.tantoJimuSeqList) */
  AND EXISTS ( 
    SELECT
      * 
    FROM
      t_anken_tanto tat_jimu 
    WHERE
      tat_jimu.anken_id = ta.anken_id 
      AND tat_jimu.tanto_type = /* @jp.loioz.common.constant.CommonConstant$TantoType@JIMU */'3' 
      AND tat_jimu.account_seq IN /* condition.tantosha.tantoJimuSeqList */(1)
  ) 
/*%end */
  /** -- 案件情報の条件 end */
  /** -- その他検索条件 */
/*%if condition.isNotEmpty(condition.lastNyukinDateFrom) */
  AND ( 
    SELECT
      MAX(DATE_FORMAT(hassei_date, '%Y-%m-%d')) 
    FROM
      t_kaikei_kiroku 
    WHERE
      1 = 1 
      AND anken_id = ta.anken_id 
      AND customer_id = tc.customer_id 
      AND nyushukkin_type = '1' 
    GROUP BY
      customer_id
      , anken_id
  ) >= /* condition.lastNyukinDateFrom */'2000-01-01' 
/*%end */
/*%if condition.isNotEmpty(condition.lastNyukinDateTo) */
  AND ( 
    SELECT
      MAX(DATE_FORMAT(hassei_date, '%Y-%m-%d')) 
    FROM
      t_kaikei_kiroku 
    WHERE
      1 = 1 
      AND anken_id = ta.anken_id 
      AND customer_id = tc.customer_id 
      AND nyushukkin_type = '1' 
    GROUP BY
      customer_id
      , anken_id
  ) <= /* condition.lastNyukinDateTo */'2000-01-01' 
/*%end */
  /** -- 顧客計の検索  */
  /** --「すべて && 0円表示」 の場合は、検索条件の絞り込みを行わない */
/*%if  !( @jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType@ALL.equals(condition.totalSearchType) && !condition.excludeZero) */
  AND ( 
    SELECT
      /** 
      -- 計算式 入金計 - (出金計 + 報酬(源泉差引後) + 消費税 + 源泉徴収計)
      */
      COALESCE(
      SUM(COALESCE(total.nyukin, 0)) - ( 
        SUM(COALESCE(total.shukkin, 0)) + ( 
          SUM(COALESCE(total.hoshu, 0)) - SUM(COALESCE(total.gensenchoshu, 0))
        )
      ) , 0)
    FROM
      ( 
        SELECT
          SUM( 
            CASE 
              WHEN nyukin_gaku IS NOT NULL 
                THEN nyukin_gaku 
              ELSE 0 
              END
          ) AS nyukin
          , SUM( 
            CASE 
              WHEN ( 
                tax_flg IS NULL 
                /** --非課税と内税の場合は等倍 */
                OR tax_flg = /* @jp.loioz.common.constant.CommonConstant$TaxFlg@FREE_TAX */'0' 
                OR tax_flg = /* @jp.loioz.common.constant.CommonConstant$TaxFlg@INTERNAL_TAX */'1'
              ) 
              AND hoshu_komoku_id IS NULL 
                THEN shukkin_gaku 
              WHEN tax_rate IS NULL 
              AND hoshu_komoku_id IS NULL 
                THEN shukkin_gaku 
              WHEN tax_rate IS NOT NULL 
              AND hoshu_komoku_id IS NULL 
                THEN shukkin_gaku + tax_gaku 
              ELSE 0 
              END
          ) AS shukkin
          , SUM( 
            CASE 
              WHEN ( 
                tax_flg IS NULL 
                /** --非課税と内税の場合は等倍 */
                OR tax_flg = /* @jp.loioz.common.constant.CommonConstant$TaxFlg@FREE_TAX */'0' 
                OR tax_flg = /* @jp.loioz.common.constant.CommonConstant$TaxFlg@INTERNAL_TAX */'1'
              ) 
              AND hoshu_komoku_id IS NOT NULL 
                THEN shukkin_gaku 
              WHEN tax_rate IS NULL 
              AND hoshu_komoku_id IS NOT NULL 
                THEN shukkin_gaku 
              WHEN tax_rate IS NOT NULL 
              AND hoshu_komoku_id IS NOT NULL 
                THEN shukkin_gaku + tax_gaku 
              ELSE 0 
              END
          ) AS hoshu
          , SUM( 
            CASE 
              WHEN gensenchoshu_gaku IS NOT NULL 
                THEN gensenchoshu_gaku 
              ELSE 0 
              END
          ) AS gensenchoshu 
        FROM
          t_kaikei_kiroku 
        WHERE
          customer_id = tc.customer_id 
        GROUP BY
          kaikei_kiroku_seq
      ) AS total
  ) 
/*%if @jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType@ALL.equals(condition.totalSearchType) && condition.excludeZero */
  != 0 
  /*%elseif @jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType@PLUS.equals(condition.totalSearchType) && !condition.excludeZero */
  >= 0 
  /*%elseif @jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType@PLUS.equals(condition.totalSearchType) && condition.excludeZero */
  > 0 
  /*%elseif @jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType@MINUS.equals(condition.totalSearchType) && !condition.excludeZero */
  <= 0 
  /*%elseif @jp.loioz.app.user.azukarikinManagement.Enums.CustomerTotalSearchType@MINUS.equals(condition.totalSearchType) && condition.excludeZero */
  < 0 
/*%end */
/*%end */
  /** -- 顧客計の検索 end  */
GROUP BY
  customer_id 
ORDER BY
  customer_id DESC
