<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
    <div>
        hello <#if name??>${name}</#if>
        <table>
            <tr>
                <td>序号</td>
                <td>姓名</td>
                <td>年龄</td>
                <td>钱包</td>
                <td>生日</td>
            </tr>
            <#list stus as stu>
                <tr>
                    <td>${stu_index+1}</td>
                    <td>${stu.name}</td>
                    <td>${stu.age}</td>
                    <td>${stu.money}</td>
                    <td>${stu.birthday?string("yyyy-MM-dd")}</td>
                </tr>
            </#list>
            <#list stuMap?keys as k>
                <tr>
                    <td>${k_index+1}</td>
                    <td>${stuMap[k].name}</td>
                    <td>${stuMap[k].age}</td>
                    <td>${stuMap[k].money?c}</td>
                    <td>${stuMap[k].birthday?datetime}</td>
                </tr>
            </#list>
        </table>

        <#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
        <#assign data=text?eval />
        开户行：${data.bank}  账号：${data.account}
    </div>

</body>
</html>