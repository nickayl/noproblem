package org.javando.http.problem;

public abstract class Problem implements ProblemKt {

    //private static final Map<Class<? extends Property<?>>, Property<?>> properties = new HashMap<>();

//    static {
//        properties.put(DateFormatProperty.class, new DateFormatProperty("dd/MM/yyyy hh:mm:ss"));
//        properties.put(DateIdentifierProperty.class, new DateIdentifierProperty("date"));
//    }
//
//    public static Map<Class<? extends Property<?>>, Property<?>> getProperties() {
//        return Collections.unmodifiableMap(properties);
//    }

//    private static final PropertiesConfigurer config = PropertiesConfigurer
//            .newInstance()
//            .setDateFormat("dd/MM/yyyy hh:mm:ss")
//            .setDateIdentifier("date")
//            .setJsonProvider()
//
//    public static final class PropertiesConfigurer {
//        //public static final PropertiesConfigurer instance = new PropertiesConfigurer();
//        private final Map<Class<? extends Property<?>>, Property<?>> properties = new HashMap<>();
//
//        private PropertiesConfigurer() { }
//
//        static PropertiesConfigurer newInstance() {
//            return new PropertiesConfigurer();
//        }
//
//        public PropertiesConfigurer setDateFormat(String dateFormat) {
//            properties.put(DateFormatProperty.class, new DateFormatProperty(dateFormat));
//            return this;
//        }
//
//        public PropertiesConfigurer setDateIdentifier(String identifier) {
//            properties.put(DateFormatProperty.class, new DateIdentifierProperty(identifier));
//            return this;
//        }
//
////        public PropertiesConfigurer setJsonProvider(JsonProvider provider) {
////            properties.put(DateFormatProperty.class, new JsonProviderProperty(provider));
////            return this;
////        }
//
//        public Map<Class<? extends Property<?>>, Property<?>> getProperties() {
//            return properties;
//        }
//
//        void apply() {
//            if (!properties.containsKey(JsonProviderProperty.class)) {
//                properties.put(JsonProviderProperty.class, new JsonProviderProperty(getDefaultProvider()));
//            }
////            Problem.properties.clear();
////            Problem.properties.putAll(p);
//           // return this;
//        }
//    }

    //    static ProblemConfigurer getConfiguration() {
//        return Config.getConfigurer();
//    }

//    final class Config {
//        private static ProblemConfigurer configurer;
//
//        private Config() {
//        }
//
//        public static ProblemConfigurer getConfigurer() {
//            if (configurer == null)
//                configurer = new AutomaticProblemConfigurer();
//            return configurer;
//        }
//
//        public static void setConfigurer(ProblemConfigurer configurer) {
//            if (configurer.getProvider() == null) {
//                try {
//                    var binder = (ProviderBinder) Class
//                            .forName("org.javando.http.problem.impl.ProviderBinderImpl")
//                            .getDeclaredConstructor()
//                            .newInstance();
//                    var provider = binder.getImplementation()
//                            .getDeclaredConstructor()
//                            .newInstance();
//                    configurer.setProvider(provider);
//                } catch (Exception e) {
//                    //selectedProvider = DEFAULT_PROVIDER = new GsonProvider();
//                    e.printStackTrace();
//                    throw new MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e);
//                }
//            }
//            Config.configurer = configurer;
//        }
//    }

    public static Problem from(String json) {
        return from(json, getDefaultProvider());
    }

    public static Problem from(String json, JsonProvider provider) {
        return provider.fromJson(json);
    }

    public static ProblemBuilder create() {
        //configurer.apply();
        return create(getDefaultProvider());
        //return create((JsonProvider) properties.get(JsonProviderProperty.class).getProperty());
    }

    public static ProblemBuilder create(JsonProvider provider) {
        return findProblemBuilderOrThrow(provider);
    }

    private static ProblemBuilder findProblemBuilderOrThrow(JsonProvider provider) {
        try {
            var binder = (ProblemBinder) Class.forName("org.javando.http.problem.impl.ProblemBinderImpl")
                    .getDeclaredConstructor()
                    .newInstance();
            return binder.getImplementation(provider)
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e);
        }
    }

    private static JsonProvider defaultProvider;
    private static JsonProvider getDefaultProvider() {
        if(defaultProvider != null) return defaultProvider;
        try {
            var binder = (ProviderBinder) Class
                    .forName("org.javando.http.problem.impl.ProviderBinderImpl")
                    .getDeclaredConstructor()
                    .newInstance();
            defaultProvider = binder.getImplementation()
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            throw new MissingImplementationException("Error instantiating the default JsonProvider: Cannot find a valid implementation for ProviderBinderImpl", e);
        }
        return defaultProvider;
    }

}
