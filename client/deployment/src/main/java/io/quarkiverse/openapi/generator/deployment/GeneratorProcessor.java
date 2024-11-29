package io.quarkiverse.openapi.generator.deployment;

import static io.quarkus.bootstrap.classloading.QuarkusClassLoader.isClassPresentAtRuntime;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;

import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassType;
import org.jboss.jandex.DotName;
import org.jboss.jandex.ParameterizedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkiverse.openapi.generator.AuthName;
import io.quarkiverse.openapi.generator.AuthenticationRecorder;
import io.quarkiverse.openapi.generator.OidcClient;
import io.quarkiverse.openapi.generator.OpenApiGeneratorConfig;
import io.quarkiverse.openapi.generator.OpenApiSpec;
import io.quarkiverse.openapi.generator.markers.ApiKeyAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.BasicAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.BearerAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.OauthAuthenticationMarker;
import io.quarkiverse.openapi.generator.markers.OperationMarker;
import io.quarkiverse.openapi.generator.oidc.ClassicOidcClientRequestFilterDelegate;
import io.quarkiverse.openapi.generator.oidc.OidcAuthenticationRecorder;
import io.quarkiverse.openapi.generator.oidc.ReactiveOidcClientRequestFilterDelegate;
import io.quarkiverse.openapi.generator.oidc.providers.OAuth2AuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.ApiKeyIn;
import io.quarkiverse.openapi.generator.providers.AuthProvider;
import io.quarkiverse.openapi.generator.providers.CompositeAuthenticationProvider;
import io.quarkiverse.openapi.generator.providers.OperationAuthInfo;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.arc.deployment.SyntheticBeanBuildItem;
import io.quarkus.deployment.Capabilities;
import io.quarkus.deployment.Capability;
import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;

public class GeneratorProcessor {

    private static final String FEATURE = "openapi-generator";
    private static final DotName OAUTH_AUTHENTICATION_MARKER = DotName.createSimple(OauthAuthenticationMarker.class);
    private static final DotName BASIC_AUTHENTICATION_MARKER = DotName.createSimple(BasicAuthenticationMarker.class);
    private static final DotName BEARER_AUTHENTICATION_MARKER = DotName.createSimple(BearerAuthenticationMarker.class);
    private static final DotName API_KEY_AUTHENTICATION_MARKER = DotName.createSimple(ApiKeyAuthenticationMarker.class);
    private static final DotName OPERATION_MARKER = DotName.createSimple(OperationMarker.class);

    private static final String ABSTRACT_TOKEN_PRODUCER = "io.quarkus.oidc.client.runtime.AbstractTokensProducer";

    private static final Logger LOGGER = LoggerFactory.getLogger(GeneratorProcessor.class);

    private static String sanitizeAuthName(String schemeName) {
        return OpenApiGeneratorConfig.getSanitizedSecuritySchemeName(schemeName);
    }

    private static Map<String, List<AnnotationInstance>> getOperationsBySpec(CombinedIndexBuildItem beanArchiveBuildItem) {
        return beanArchiveBuildItem.getIndex().getAnnotationsWithRepeatable(OPERATION_MARKER, beanArchiveBuildItem.getIndex())
                .stream().collect(Collectors.groupingBy(
                        marker -> marker.value("openApiSpecId").asString() + "_" + marker.value("name").asString()));
    }

    private static List<OperationAuthInfo> getOperations(Map<String, List<AnnotationInstance>> operationsBySpec,
            String openApiSpecId, String name) {
        return operationsBySpec.getOrDefault(openApiSpecId + "_" + name, List.of()).stream()
                .map(op -> OperationAuthInfo.builder().withPath(op.value("path").asString())
                        .withId(op.value("operationId").asString()).withMethod(op.value("method").asString()).build())
                .collect(Collectors.toList());
    }

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    void additionalBean(Capabilities capabilities, BuildProducer<AdditionalBeanBuildItem> producer) {

        if (!isClassPresentAtRuntime(ABSTRACT_TOKEN_PRODUCER)) {
            LOGGER.debug("{} class not found in runtime, skipping OidcClientRequestFilterDelegate bean generation",
                    ABSTRACT_TOKEN_PRODUCER);
            return;
        }
        LOGGER.debug("{} class found in runtime, producing OidcClientRequestFilterDelegate bean generation",
                ABSTRACT_TOKEN_PRODUCER);

        if (capabilities.isPresent(Capability.REST_CLIENT_REACTIVE)) {
            producer.produce(AdditionalBeanBuildItem.builder().addBeanClass(ReactiveOidcClientRequestFilterDelegate.class)
                    .setDefaultScope(DotName.createSimple(Dependent.class)).setUnremovable().build());
        } else {
            producer.produce(AdditionalBeanBuildItem.builder().addBeanClass(ClassicOidcClientRequestFilterDelegate.class)
                    .setDefaultScope(DotName.createSimple(Dependent.class)).setUnremovable().build());
        }

    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void produceCompositeProviders(AuthenticationRecorder recorder, List<AuthProviderBuildItem> authProviders,
            BuildProducer<SyntheticBeanBuildItem> beanProducer) {
        Map<String, List<AuthProviderBuildItem>> providersBySpec = authProviders.stream()
                .collect(Collectors.groupingBy(AuthProviderBuildItem::getOpenApiSpecId));
        providersBySpec.forEach((openApiSpecId, providers) -> {
            beanProducer.produce(SyntheticBeanBuildItem.configure(CompositeAuthenticationProvider.class).scope(Dependent.class)
                    .addQualifier().annotation(OpenApiSpec.class).addValue("openApiSpecId", openApiSpecId).done()
                    .addInjectionPoint(ParameterizedType.create(Instance.class, ClassType.create(AuthProvider.class)),
                            AnnotationInstance.builder(OpenApiSpec.class).add("openApiSpecId", openApiSpecId).build())
                    .createWith(recorder.recordCompositeProvider(openApiSpecId)).done());

        });
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void produceOauthAuthentication(CombinedIndexBuildItem beanArchiveBuildItem,
            BuildProducer<AuthProviderBuildItem> authenticationProviders, BuildProducer<SyntheticBeanBuildItem> beanProducer,
            OidcAuthenticationRecorder oidcRecorder) {

        if (!isClassPresentAtRuntime(ABSTRACT_TOKEN_PRODUCER)) {
            LOGGER.debug("{} class not found in runtime, skipping OAuth bean generation", ABSTRACT_TOKEN_PRODUCER);
            return;
        }
        LOGGER.debug("{} class found in runtime, producing OAuth bean generation", ABSTRACT_TOKEN_PRODUCER);
        Collection<AnnotationInstance> authenticationMarkers = beanArchiveBuildItem.getIndex()
                .getAnnotationsWithRepeatable(OAUTH_AUTHENTICATION_MARKER, beanArchiveBuildItem.getIndex());

        Map<String, List<AnnotationInstance>> operationsBySpec = getOperationsBySpec(beanArchiveBuildItem);

        for (AnnotationInstance authenticationMarker : authenticationMarkers) {
            String name = authenticationMarker.value("name").asString();
            String openApiSpecId = authenticationMarker.value("openApiSpecId").asString();
            List<OperationAuthInfo> operations = getOperations(operationsBySpec, openApiSpecId, name);
            authenticationProviders.produce(new AuthProviderBuildItem(openApiSpecId, name));
            beanProducer.produce(SyntheticBeanBuildItem.configure(AuthProvider.class).scope(Dependent.class).addQualifier()
                    .annotation(AuthName.class).addValue("name", name).done().addQualifier().annotation(OpenApiSpec.class)
                    .addValue("openApiSpecId", openApiSpecId).done()
                    .addInjectionPoint(ClassType.create(OAuth2AuthenticationProvider.OidcClientRequestFilterDelegate.class),
                            AnnotationInstance.builder(OidcClient.class).add("name", sanitizeAuthName(name)).build())
                    .createWith(oidcRecorder.recordOauthAuthProvider(sanitizeAuthName(name), openApiSpecId, operations))
                    .unremovable().done());
        }
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void produceBasicAuthentication(CombinedIndexBuildItem beanArchiveBuildItem,
            BuildProducer<AuthProviderBuildItem> authenticationProviders, BuildProducer<SyntheticBeanBuildItem> beanProducer,
            AuthenticationRecorder recorder) {

        Collection<AnnotationInstance> authenticationMarkers = beanArchiveBuildItem.getIndex()
                .getAnnotationsWithRepeatable(BASIC_AUTHENTICATION_MARKER, beanArchiveBuildItem.getIndex());

        Map<String, List<AnnotationInstance>> operationsBySpec = getOperationsBySpec(beanArchiveBuildItem);
        for (AnnotationInstance authenticationMarker : authenticationMarkers) {
            String name = authenticationMarker.value("name").asString();
            String openApiSpecId = authenticationMarker.value("openApiSpecId").asString();

            List<OperationAuthInfo> operations = getOperations(operationsBySpec, openApiSpecId, name);

            authenticationProviders.produce(new AuthProviderBuildItem(openApiSpecId, name));

            beanProducer.produce(SyntheticBeanBuildItem.configure(AuthProvider.class).scope(Dependent.class).addQualifier()
                    .annotation(AuthName.class).addValue("name", name).done().addQualifier().annotation(OpenApiSpec.class)
                    .addValue("openApiSpecId", openApiSpecId).done()
                    .createWith(recorder.recordBasicAuthProvider(sanitizeAuthName(name), openApiSpecId, operations))
                    .unremovable().done());
        }
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void produceBearerAuthentication(CombinedIndexBuildItem beanArchiveBuildItem,
            BuildProducer<AuthProviderBuildItem> authenticationProviders, BuildProducer<SyntheticBeanBuildItem> beanProducer,
            AuthenticationRecorder recorder) {

        Collection<AnnotationInstance> authenticationMarkers = beanArchiveBuildItem.getIndex()
                .getAnnotationsWithRepeatable(BEARER_AUTHENTICATION_MARKER, beanArchiveBuildItem.getIndex());

        Map<String, List<AnnotationInstance>> operationsBySpec = getOperationsBySpec(beanArchiveBuildItem);
        for (AnnotationInstance authenticationMarker : authenticationMarkers) {
            String name = authenticationMarker.value("name").asString();
            String scheme = authenticationMarker.value("scheme").asString();
            String openApiSpecId = authenticationMarker.value("openApiSpecId").asString();

            List<OperationAuthInfo> operations = getOperations(operationsBySpec, openApiSpecId, name);
            authenticationProviders.produce(new AuthProviderBuildItem(openApiSpecId, name));
            beanProducer.produce(SyntheticBeanBuildItem.configure(AuthProvider.class).scope(Dependent.class).addQualifier()
                    .annotation(AuthName.class).addValue("name", name).done().addQualifier().annotation(OpenApiSpec.class)
                    .addValue("openApiSpecId", openApiSpecId).done()
                    .createWith(recorder.recordBearerAuthProvider(sanitizeAuthName(name), scheme, openApiSpecId, operations))
                    .unremovable().done());

        }
    }

    @BuildStep
    @Record(ExecutionTime.STATIC_INIT)
    void produceApiKeyAuthentication(CombinedIndexBuildItem beanArchiveBuildItem,
            BuildProducer<AuthProviderBuildItem> authenticationProviders, BuildProducer<SyntheticBeanBuildItem> beanProducer,
            AuthenticationRecorder recorder) {

        Collection<AnnotationInstance> authenticationMarkers = beanArchiveBuildItem.getIndex()
                .getAnnotationsWithRepeatable(API_KEY_AUTHENTICATION_MARKER, beanArchiveBuildItem.getIndex());
        Map<String, List<AnnotationInstance>> operationsBySpec = getOperationsBySpec(beanArchiveBuildItem);
        for (AnnotationInstance authenticationMarker : authenticationMarkers) {
            String name = authenticationMarker.value("name").asString();
            String openApiSpecId = authenticationMarker.value("openApiSpecId").asString();
            String apiKeyName = authenticationMarker.value("apiKeyName").asString();
            ApiKeyIn apiKeyIn = ApiKeyIn.valueOf(authenticationMarker.value("apiKeyIn").asEnum());

            List<OperationAuthInfo> operations = getOperations(operationsBySpec, openApiSpecId, name);

            authenticationProviders.produce(new AuthProviderBuildItem(openApiSpecId, name));

            beanProducer.produce(SyntheticBeanBuildItem.configure(AuthProvider.class).scope(Dependent.class).addQualifier()
                    .annotation(AuthName.class).addValue("name", name).done().addQualifier().annotation(OpenApiSpec.class)
                    .addValue("openApiSpecId", openApiSpecId).done().createWith(recorder
                            .recordApiKeyAuthProvider(sanitizeAuthName(name), openApiSpecId, apiKeyIn, apiKeyName, operations))
                    .unremovable().done());
        }

    }
}
