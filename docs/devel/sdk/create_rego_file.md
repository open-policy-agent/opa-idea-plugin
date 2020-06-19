# Create rego file
## Description
This action allows creation of a new `rego file` from a template.

See action usage [here](../../user/features/create_rego_file.md)

## Implementation
The action is implemented by the class [RegoCreateFileAction](../../../src/main/kotlin/org/openpolicyagent/ideaplugin/ide/actions/RegoCreateFileAction.kt)
and registered under the `actions` section in the `plugin.xml`

The default template used to create the file is defined by 2 files:
*[resources/fileTemplates/internal/Rego File.rego.ft](../../../src/main/resources/fileTemplates/internal/Rego%20File.rego.ft) the template file used to create the rego file
*[resources/fileTemplates/internal/Rego File.rego.html](../../../src/main/resources/fileTemplates/internal/Rego%20File.rego.html) the description that appears in the template editor (`Settings` ->`Editor` -> `File and Code Templates`)

The template must also be registered in the `plugin.xml`
```xml
<extensions defaultExtensionNs="com.intellij">
    <internalFileTemplate name="Rego File"/>
</extensions>
```
