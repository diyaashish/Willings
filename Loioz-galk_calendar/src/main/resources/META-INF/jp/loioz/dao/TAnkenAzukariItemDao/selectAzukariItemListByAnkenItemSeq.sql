SELECT
    taai.anken_item_seq
    , taai.hinmoku
    , taai.azukari_count
    , taai.hokan_place
    , taai.azukari_from_type
    , taai.azukari_from_customer_id
    , taai.azukari_from_kanyosha_seq
    , taai.azukari_from
    , DATE_FORMAT(taai.azukari_date, '%Y/%m/%d') AS azukari_date
    , DATE_FORMAT(taai.return_limit_date, '%Y/%m/%d') AS return_limit_date
    , DATE_FORMAT(taai.return_date, '%Y/%m/%d') AS return_date
    , taai.return_to
    , taai.return_to_customer_id
    , taai.return_to_kanyosha_seq
    , taai.return_to_type
    , taai.remarks
    , taai.azukari_status
    , taai.version_no 
FROM
    t_anken_azukari_item AS taai 
    INNER JOIN t_anken AS ta 
        ON taai.anken_id = ta.anken_id 
WHERE
    taai.anken_item_seq IN /* ankenItemSeqList */(null) 
ORDER BY
    taai.azukari_date DESC
    , taai.anken_item_seq DESC
;