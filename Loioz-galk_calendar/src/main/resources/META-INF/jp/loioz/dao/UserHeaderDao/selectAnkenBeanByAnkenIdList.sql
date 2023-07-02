SELECT
  ta.anken_id
  , ta.bunya_id
  , mb.bunya_name
  , ta.anken_name 
  , ta.anken_type 
FROM
  t_anken ta 
  LEFT JOIN m_bunya mb 
    ON ta.bunya_id = mb.bunya_id 
WHERE
  ta.anken_id IN /* ankenIdList */(NULL)
ORDER BY 
  ta.anken_id DESC , ta.created_at DESC
;
