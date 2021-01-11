package org.javando.http.problem;



//configurer.apply();
//return create((JsonProvider) properties.get(JsonProviderProperty.class).getProperty());

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

//public class Providers {
//
//    //private static JsonProvider DEFAULT_PROVIDER;
//    private static final JsonProvider selectedProvider;
//
//    static {
//        try {
//            var binder = (ProviderBinder) Class
//                    .forName("org.javando.http.problem.impl.ProviderBinderImpl")
//                    .getDeclaredConstructor()
//                    .newInstance();
//            selectedProvider = binder.getImplementation()
//                    .getDeclaredConstructor()
//                    .newInstance();
//        } catch (Exception e) {
//            //selectedProvider = DEFAULT_PROVIDER = new GsonProvider();
//            e.printStackTrace();
//            throw new MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e);
//        }
//    }
//
////    public static JsonProvider getDefault() {
////        return DEFAULT_PROVIDER;
////    }
//
//    public static JsonProvider getSelected() {
//        return selectedProvider;
//        //return Optional.ofNullable(selectedProvider).orElse(DEFAULT_PROVIDER);
//    }
//
//}
