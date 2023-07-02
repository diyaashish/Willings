SELECT
  * 
FROM
  service_mgt.m_holiday 
WHERE
  holiday_date >= /* from */'2000-01-01' 
  AND holiday_date <= /* to */'2020-12-31'
