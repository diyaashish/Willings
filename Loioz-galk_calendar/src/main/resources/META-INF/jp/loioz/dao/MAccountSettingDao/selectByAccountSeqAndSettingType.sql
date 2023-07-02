select
  * 
from
  m_account_setting mas 
where
  mas.account_seq = /* accountSeq */null
  and mas.setting_type = /* settingType.getCd() */'';