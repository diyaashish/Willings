#!/bin/sh

cd `dirname $0`

#設定ファイル読み込み
. ./application.conf

tenant_seq=$1
if [ -z "${tenant_seq}" ]; then
    echo "テナント連番を指定して下さい。"
    exit 1
fi

tenant_db_name="${tenant_db_name_prefix}${tenant_seq}"
tenant_user="${tenant_user_prefix}${tenant_seq}"

echo "${tenant_db_name} を削除します。よろしいですか？[yes/no]"
read line
if [ "${line}" != "yes" ]; then
    echo "処理を中断します。"
    exit 0
fi

result=0
#ユーザを削除
mysql --defaults-extra-file=login_my.conf -h ${db_host} -P ${db_port} <<EOS
DROP USER '${tenant_user}'@'${tenant_user_host}';
exit
EOS
result=$?
if [ $result != 0 ]; then
    echo "ユーザの削除に失敗しました。(user=${tenant_user})"
    result=1
fi
#テナントDBを削除
mysql --defaults-extra-file=login_my.conf -h ${db_host} -P ${db_port} <<EOS
DROP DATABASE ${tenant_db_name};
exit
EOS
result=$?
if [ $result != 0 ]; then
    echo "テナントDBの削除に失敗しました。(db_name=${tenant_db_name})"
    result=1
fi

if [ $result == 0 ]; then
    echo "${tenant_db_name} を削除しました。"
fi
exit $result