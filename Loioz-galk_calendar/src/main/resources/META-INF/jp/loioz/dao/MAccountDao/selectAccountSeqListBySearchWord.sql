SELECT
  ma.account_seq
FROM
  m_account AS ma
WHERE
  CONCAT(ma.account_name_sei, ma.account_name_mei) LIKE /* @infix(searchMailText) */null;