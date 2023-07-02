SELECT
	tkk.kaikei_kiroku_seq
	, tkk.hassei_date
	, tkk.hoshu_komoku_id
	, tkk.shukkin_gaku
	, tkk.tax_gaku
	, tkk.time_charge_tanka
	, tkk.time_charge_time_shitei
	, tkk.time_charge_start_time
	, tkk.time_charge_end_time
	, tkk.time_charge_time
	, tkk.tax_rate
	, tkk.tax_flg
	, tkk.gensenchoshu_flg
	, tkk.gensenchoshu_gaku
	, tkk.tekiyo
	, tkk.seisan_date
	, tkk.seisan_seq
	, tc.customer_id
	, TRIM(CONCAT(COALESCE(tc.customer_name_sei,''),' ',COALESCE(tc.customer_name_mei,''))) AS customer_name
	, tsk.seisan_id
	, tac.anken_status
FROM
	t_kaikei_kiroku tkk
	LEFT OUTER JOIN t_seisan_kiroku tsk
	ON tkk.seisan_seq = tsk.seisan_seq
	INNER JOIN t_anken ta
	ON tkk.anken_id = ta.anken_id
	INNER JOIN t_anken_customer tac
	ON ta.anken_id = tac.anken_id
	AND tkk.customer_id = tac.customer_id
	INNER JOIN t_person tc
	ON tac.customer_id = tc.customer_id
WHERE
	tkk.anken_id = /* ankenId */NULL
	/*%if customerId != null*/
	AND tkk.customer_id = /* customerId */NULL
	/*%end */
	AND tkk.hoshu_komoku_id IS NOT NULL
ORDER BY
	tkk.hassei_date ASC ,
	tkk.created_at ASC
;