package io.quarkiverse.openapi.generator.deployment.template;

import java.util.Map;

import org.openapitools.codegen.api.AbstractTemplatingEngineAdapter;
import org.openapitools.codegen.api.TemplatingExecutor;

import io.quarkus.qute.Engine;
import io.quarkus.qute.ReflectionValueResolver;
import io.quarkus.qute.Template;

public class QuteTemplatingEngineAdapter extends AbstractTemplatingEngineAdapter {

    private static final String IDENTIFIER = "qute";
    private static final String[] DEFAULT_TEMPLATES = {
            "suppressWarnings.qute",
            "additionalEnumTypeAnnotations.qute",
            "additionalEnumTypeUnexpectedMember.qute",
            "additionalModelTypeAnnotations.qute",
            "beanValidation.qute",
            "beanValidationCore.qute",
            "beanValidationInlineCore.qute",
            "beanValidationHeaderParams.qute",
            "bodyParams.qute",
            "enumClass.qute",
            "enumOuterClass.qute",
            "headerParams.qute",
            "pathParams.qute",
            "cookieParams.qute",
            "pojo.qute",
            "pojoQueryParam.qute",
            "queryParams.qute",
            "auth/compositeAuthenticationProvider.qute",
            "auth/headersFactory.qute",
            "multipartFormdataPojo.qute",
            "pojoAdditionalProperties.qute",
            "operationJavaDoc.qute"
    };
    public final Engine engine;

    public QuteTemplatingEngineAdapter() {
        this.engine = Engine.builder()
                .addDefaults()
                .addValueResolver(new ReflectionValueResolver())
                .addNamespaceResolver(OpenApiNamespaceResolver.INSTANCE)
                .addNamespaceResolver(StrNamespaceResolver.INSTANCE)
                .removeStandaloneLines(true)
                .strictRendering(true)
                .build();
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public String[] getFileExtensions() {
        return new String[] { IDENTIFIER };
    }

    @Override
    public String compileTemplate(TemplatingExecutor executor, Map<String, Object> bundle, String templateFile) {
        this.cacheTemplates(executor);
        Template template = engine.getTemplate(templateFile);
        if (template == null) {
            template = engine.parse(executor.getFullTemplateContents(templateFile));
            engine.putTemplate(templateFile, template);
        }
        return template.data(bundle).render();
    }

    public void cacheTemplates(TemplatingExecutor executor) {
        for (String templateId : DEFAULT_TEMPLATES) {
            Template incTemplate = engine.getTemplate(templateId);
            if (incTemplate == null) {
                incTemplate = engine.parse(executor.getFullTemplateContents(templateId));
                engine.putTemplate(templateId, incTemplate);
            }
        }
    }
}
