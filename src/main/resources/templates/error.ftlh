<#macro header title>
    <html>
    <head>
        <title>${title}</title>
    </head>
    <body>
    <h1>${title}</h1>
    </body>
    </html>
</#macro>

<#macro showError error>
    <#if error??>
        <h2>${error}</h2>
    <#else>
        <h2>Erro desconhecido</h2>
    </#if>
</#macro>

<#include "lib/template.ftlh">

