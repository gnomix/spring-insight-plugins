<#ftl strip_whitespace=true>
<#import "/insight-1.0.ftl" as insight />

<@insight.group label="Model Attribute">
    <@insight.entry name="Attribute Name" value=operation.modelAttributeName />
    <@insight.entry name="Value" value=operation.value />
</@insight.group>
