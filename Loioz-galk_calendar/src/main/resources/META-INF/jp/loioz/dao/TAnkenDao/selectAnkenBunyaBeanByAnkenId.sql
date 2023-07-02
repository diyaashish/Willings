SELECT
  ta.anken_id
  , ta.anken_name
  , ta.bunya_id
  , ta.anken_created_date
  , ta.anken_type
  , ta.jian_summary
  , mb.bunya_type
  , mb.bunya_name 
FROM
  t_anken ta 
  INNER JOIN m_bunya mb 
    ON ta.bunya_id = mb.bunya_id 
WHERE
  ta.anken_id IN /* ankenIdList */(NULL)
;
