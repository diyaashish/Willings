select
  * 
from
  service_mgt.t_plan_setting_session tpss 
where
  tpss.tenant_seq = /* tenantSeq */null
  and tpss.account_seq = /* accountSeq */null;