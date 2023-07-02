SELECT
    * 
FROM
    t_toiawase 
WHERE
    toiawase_seq IN /* toiawaseSeq */(null)
ORDER BY 
    last_update_at DESC
;