package org.gradle.accessors.dm;

import org.gradle.api.NonNullApi;
import org.gradle.api.artifacts.MinimalExternalModuleDependency;
import org.gradle.plugin.use.PluginDependency;
import org.gradle.api.artifacts.ExternalModuleDependencyBundle;
import org.gradle.api.artifacts.MutableVersionConstraint;
import org.gradle.api.provider.Provider;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.internal.catalog.AbstractExternalDependencyFactory;
import org.gradle.api.internal.catalog.DefaultVersionCatalog;
import java.util.Map;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.artifacts.dsl.CapabilityNotationParser;
import javax.inject.Inject;

/**
 * A catalog of dependencies accessible via the {@code libs} extension.
 */
@NonNullApi
public class LibrariesForLibs extends AbstractExternalDependencyFactory {

    private final AbstractExternalDependencyFactory owner = this;
    private final OrgLibraryAccessors laccForOrgLibraryAccessors = new OrgLibraryAccessors(owner);
    private final VersionAccessors vaccForVersionAccessors = new VersionAccessors(providers, config);
    private final BundleAccessors baccForBundleAccessors = new BundleAccessors(objects, providers, config, attributesFactory, capabilityNotationParser);
    private final PluginAccessors paccForPluginAccessors = new PluginAccessors(providers, config);

    @Inject
    public LibrariesForLibs(DefaultVersionCatalog config, ProviderFactory providers, ObjectFactory objects, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) {
        super(config, providers, objects, attributesFactory, capabilityNotationParser);
    }

    /**
     * Group of libraries at <b>org</b>
     */
    public OrgLibraryAccessors getOrg() {
        return laccForOrgLibraryAccessors;
    }

    /**
     * Group of versions at <b>versions</b>
     */
    public VersionAccessors getVersions() {
        return vaccForVersionAccessors;
    }

    /**
     * Group of bundles at <b>bundles</b>
     */
    public BundleAccessors getBundles() {
        return baccForBundleAccessors;
    }

    /**
     * Group of plugins at <b>plugins</b>
     */
    public PluginAccessors getPlugins() {
        return paccForPluginAccessors;
    }

    public static class OrgLibraryAccessors extends SubDependencyFactory {
        private final OrgProjectlombokLibraryAccessors laccForOrgProjectlombokLibraryAccessors = new OrgProjectlombokLibraryAccessors(owner);
        private final OrgSpringframeworkLibraryAccessors laccForOrgSpringframeworkLibraryAccessors = new OrgSpringframeworkLibraryAccessors(owner);

        public OrgLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.projectlombok</b>
         */
        public OrgProjectlombokLibraryAccessors getProjectlombok() {
            return laccForOrgProjectlombokLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework</b>
         */
        public OrgSpringframeworkLibraryAccessors getSpringframework() {
            return laccForOrgSpringframeworkLibraryAccessors;
        }

    }

    public static class OrgProjectlombokLibraryAccessors extends SubDependencyFactory {

        public OrgProjectlombokLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>lombok</b> with <b>org.projectlombok:lombok</b> coordinates and
         * with version reference <b>org.projectlombok.lombok</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getLombok() {
            return create("org.projectlombok.lombok");
        }

    }

    public static class OrgSpringframeworkLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootLibraryAccessors laccForOrgSpringframeworkBootLibraryAccessors = new OrgSpringframeworkBootLibraryAccessors(owner);
        private final OrgSpringframeworkCloudLibraryAccessors laccForOrgSpringframeworkCloudLibraryAccessors = new OrgSpringframeworkCloudLibraryAccessors(owner);

        public OrgSpringframeworkLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot</b>
         */
        public OrgSpringframeworkBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootLibraryAccessors;
        }

        /**
         * Group of libraries at <b>org.springframework.cloud</b>
         */
        public OrgSpringframeworkCloudLibraryAccessors getCloud() {
            return laccForOrgSpringframeworkCloudLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringLibraryAccessors laccForOrgSpringframeworkBootSpringLibraryAccessors = new OrgSpringframeworkBootSpringLibraryAccessors(owner);

        public OrgSpringframeworkBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkBootSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootLibraryAccessors laccForOrgSpringframeworkBootSpringBootLibraryAccessors = new OrgSpringframeworkBootSpringBootLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootLibraryAccessors getBoot() {
            return laccForOrgSpringframeworkBootSpringBootLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkBootSpringBootStarterLibraryAccessors laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors = new OrgSpringframeworkBootSpringBootStarterLibraryAccessors(owner);

        public OrgSpringframeworkBootSpringBootLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors getStarter() {
            return laccForOrgSpringframeworkBootSpringBootStarterLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkBootSpringBootStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>test</b> with <b>org.springframework.boot:spring-boot-starter-test</b> coordinates and
         * with version reference <b>org.springframework.boot.spring.boot.starter.test</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getTest() {
            return create("org.springframework.boot.spring.boot.starter.test");
        }

    }

    public static class OrgSpringframeworkCloudLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringLibraryAccessors laccForOrgSpringframeworkCloudSpringLibraryAccessors = new OrgSpringframeworkCloudSpringLibraryAccessors(owner);

        public OrgSpringframeworkCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring</b>
         */
        public OrgSpringframeworkCloudSpringLibraryAccessors getSpring() {
            return laccForOrgSpringframeworkCloudSpringLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudLibraryAccessors = new OrgSpringframeworkCloudSpringCloudLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud</b>
         */
        public OrgSpringframeworkCloudSpringCloudLibraryAccessors getCloud() {
            return laccForOrgSpringframeworkCloudSpringCloudLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors getStarter() {
            return laccForOrgSpringframeworkCloudSpringCloudStarterLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudStarterLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>gateway</b> with <b>org.springframework.cloud:spring-cloud-starter-gateway</b> coordinates and
         * with version reference <b>org.springframework.cloud.spring.cloud.starter.gateway</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getGateway() {
            return create("org.springframework.cloud.spring.cloud.starter.gateway");
        }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter.netflix</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors getNetflix() {
            return laccForOrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors extends SubDependencyFactory {
        private final OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors laccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors(owner);

        public OrgSpringframeworkCloudSpringCloudStarterNetflixLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Group of libraries at <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors getEureka() {
            return laccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors extends SubDependencyFactory {

        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaLibraryAccessors(AbstractExternalDependencyFactory owner) { super(owner); }

        /**
         * Dependency provider for <b>client</b> with <b>org.springframework.cloud:spring-cloud-starter-netflix-eureka-client</b> coordinates and
         * with version reference <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka.client</b>
         * <p>
         * This dependency was declared in catalog libs.versions.toml
         */
        public Provider<MinimalExternalModuleDependency> getClient() {
            return create("org.springframework.cloud.spring.cloud.starter.netflix.eureka.client");
        }

    }

    public static class VersionAccessors extends VersionFactory  {

        private final OrgVersionAccessors vaccForOrgVersionAccessors = new OrgVersionAccessors(providers, config);
        public VersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org</b>
         */
        public OrgVersionAccessors getOrg() {
            return vaccForOrgVersionAccessors;
        }

    }

    public static class OrgVersionAccessors extends VersionFactory  {

        private final OrgProjectlombokVersionAccessors vaccForOrgProjectlombokVersionAccessors = new OrgProjectlombokVersionAccessors(providers, config);
        private final OrgSpringframeworkVersionAccessors vaccForOrgSpringframeworkVersionAccessors = new OrgSpringframeworkVersionAccessors(providers, config);
        public OrgVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.projectlombok</b>
         */
        public OrgProjectlombokVersionAccessors getProjectlombok() {
            return vaccForOrgProjectlombokVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework</b>
         */
        public OrgSpringframeworkVersionAccessors getSpringframework() {
            return vaccForOrgSpringframeworkVersionAccessors;
        }

    }

    public static class OrgProjectlombokVersionAccessors extends VersionFactory  {

        public OrgProjectlombokVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.projectlombok.lombok</b> with value <b>1.18.32</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getLombok() { return getVersion("org.projectlombok.lombok"); }

    }

    public static class OrgSpringframeworkVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootVersionAccessors vaccForOrgSpringframeworkBootVersionAccessors = new OrgSpringframeworkBootVersionAccessors(providers, config);
        private final OrgSpringframeworkCloudVersionAccessors vaccForOrgSpringframeworkCloudVersionAccessors = new OrgSpringframeworkCloudVersionAccessors(providers, config);
        public OrgSpringframeworkVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot</b>
         */
        public OrgSpringframeworkBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootVersionAccessors;
        }

        /**
         * Group of versions at <b>versions.org.springframework.cloud</b>
         */
        public OrgSpringframeworkCloudVersionAccessors getCloud() {
            return vaccForOrgSpringframeworkCloudVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringVersionAccessors vaccForOrgSpringframeworkBootSpringVersionAccessors = new OrgSpringframeworkBootSpringVersionAccessors(providers, config);
        public OrgSpringframeworkBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring</b>
         */
        public OrgSpringframeworkBootSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkBootSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootVersionAccessors vaccForOrgSpringframeworkBootSpringBootVersionAccessors = new OrgSpringframeworkBootSpringBootVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot</b>
         */
        public OrgSpringframeworkBootSpringBootVersionAccessors getBoot() {
            return vaccForOrgSpringframeworkBootSpringBootVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkBootSpringBootStarterVersionAccessors vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors = new OrgSpringframeworkBootSpringBootStarterVersionAccessors(providers, config);
        public OrgSpringframeworkBootSpringBootVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.boot.spring.boot.starter</b>
         */
        public OrgSpringframeworkBootSpringBootStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkBootSpringBootStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkBootSpringBootStarterVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkBootSpringBootStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.boot.spring.boot.starter.test</b> with value <b>3.3.0</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getTest() { return getVersion("org.springframework.boot.spring.boot.starter.test"); }

    }

    public static class OrgSpringframeworkCloudVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringVersionAccessors vaccForOrgSpringframeworkCloudSpringVersionAccessors = new OrgSpringframeworkCloudSpringVersionAccessors(providers, config);
        public OrgSpringframeworkCloudVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring</b>
         */
        public OrgSpringframeworkCloudSpringVersionAccessors getSpring() {
            return vaccForOrgSpringframeworkCloudSpringVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudVersionAccessors = new OrgSpringframeworkCloudSpringCloudVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud</b>
         */
        public OrgSpringframeworkCloudSpringCloudVersionAccessors getCloud() {
            return vaccForOrgSpringframeworkCloudSpringCloudVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterVersionAccessors getStarter() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudStarterVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.cloud.spring.cloud.starter.gateway</b> with value <b>4.1.2</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getGateway() { return getVersion("org.springframework.cloud.spring.cloud.starter.gateway"); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter.netflix</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors getNetflix() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors extends VersionFactory  {

        private final OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors = new OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors(providers, config);
        public OrgSpringframeworkCloudSpringCloudStarterNetflixVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Group of versions at <b>versions.org.springframework.cloud.spring.cloud.starter.netflix.eureka</b>
         */
        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors getEureka() {
            return vaccForOrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors;
        }

    }

    public static class OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors extends VersionFactory  {

        public OrgSpringframeworkCloudSpringCloudStarterNetflixEurekaVersionAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

        /**
         * Version alias <b>org.springframework.cloud.spring.cloud.starter.netflix.eureka.client</b> with value <b>4.1.1</b>
         * <p>
         * If the version is a rich version and cannot be represented as a
         * single version string, an empty string is returned.
         * <p>
         * This version was declared in catalog libs.versions.toml
         */
        public Provider<String> getClient() { return getVersion("org.springframework.cloud.spring.cloud.starter.netflix.eureka.client"); }

    }

    public static class BundleAccessors extends BundleFactory {

        public BundleAccessors(ObjectFactory objects, ProviderFactory providers, DefaultVersionCatalog config, ImmutableAttributesFactory attributesFactory, CapabilityNotationParser capabilityNotationParser) { super(objects, providers, config, attributesFactory, capabilityNotationParser); }

    }

    public static class PluginAccessors extends PluginFactory {

        public PluginAccessors(ProviderFactory providers, DefaultVersionCatalog config) { super(providers, config); }

    }

}
