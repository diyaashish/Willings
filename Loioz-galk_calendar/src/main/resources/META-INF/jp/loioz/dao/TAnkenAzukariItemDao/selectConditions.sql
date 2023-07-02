SELECT
    taai.anken_item_seq 
FROM
    t_anken_azukari_item taai 
WHERE
    taai.anken_id = /* conditions.ankenId */'' 
    /*%if conditions.isNotEmpty(conditions.azukariItemStatus) */
    AND taai.azukari_status = /* conditions.azukariItemStatus */'' 
    /*%end*/
ORDER BY
    taai.azukari_date DESC
    , taai.anken_item_seq DESC
;
