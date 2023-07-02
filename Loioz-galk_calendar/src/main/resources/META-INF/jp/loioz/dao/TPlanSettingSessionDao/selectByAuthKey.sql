select
  * 
from
  service_mgt.t_plan_setting_session tpss 
where
  tpss.auth_key = /* authKey */null;