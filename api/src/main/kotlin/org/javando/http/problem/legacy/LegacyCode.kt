package org.javando.http.problem.legacy;



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


//sealed class Property<T> {
//    abstract val property: T
//    abstract val name: String
//}
//
//class DateFormatProperty(override val property: String) : Property<String>() {
//    override val name = "org.javando.http.problem.property.dateFormat"
//}
//class DateIdentifierProperty(override val property: String) : Property<String>() {
//    override val name = "org.javando.http.problem.property.dateIdentifier"
//}
//class JsonProviderProperty(override val property: JsonProvider) : Property<JsonProvider>() {
//    override val name = "org.javando.http.problem.property.jsonProvider"
//}


//interface ProblemConfigurer {
//    var provider: JsonProvider?
//    var dateFormat: String
//    var dateIdentifier: String
//
//    fun setJsonProvider(provider: JsonProvider): ProblemConfigurer
//    fun setDateFormat(pattern: String): ProblemConfigurer
//    fun setDateIdentifier(identifier: String): ProblemConfigurer
//    fun andThen() : ProblemBuilder
//
//    companion object Static {
//        const val DEFAULT_DATE_FORMAT: String = "dd/MM/yyyy"
//        const val DEFAULT_DATE_TIME_FORMAT: String = "dd/MM/yyyy hh:mm:ss"
//        const val DEFAULT_DATE_IDENTIFIER = "date"
//        val instance: ProblemConfigurer by lazy { ProblemConfigurerImpl() }
//    }
//}
//
//private open class ProblemConfigurerImpl : ProblemConfigurer {
//
//    override var provider: JsonProvider? = null
//        set(value) {
//            if(value == null)
//                throw IllegalArgumentException("JsonProvider cannot be null")
//            field = value
//            value.dateFormatPattern = SimpleDateFormat(dateFormat)
//            value.dateIdentifier = dateIdentifier
//        }
//    override var dateFormat: String = ProblemConfigurer.DEFAULT_DATE_TIME_FORMAT
//    override var dateIdentifier: String = ProblemConfigurer.DEFAULT_DATE_IDENTIFIER
//
//    override fun setJsonProvider(provider: JsonProvider): ProblemConfigurer {
//        this.provider = provider
//        return this
//    }
//
//    override fun setDateFormat(pattern: String): ProblemConfigurer {
//        this.dateFormat = pattern
//        return this
//    }
//
//    override fun setDateIdentifier(identifier: String): ProblemConfigurer {
//        this.dateIdentifier = identifier
//        return this
//    }
//
//    override fun andThen(): ProblemBuilder {
//        return Problem.create(this)
//    }
//}
//
//private class AutomaticProblemConfigurer  : ProblemConfigurerImpl() {
//
//    init {
//        try {
//            val binder = Class
//                .forName("org.javando.http.problem.impl.ProviderBinderImpl")
//                .getDeclaredConstructor()
//                .newInstance() as ProviderBinder
//            provider = binder.implementation
//                .getDeclaredConstructor()
//                .newInstance()!!.also {
//                    it.dateFormatPattern = SimpleDateFormat(dateFormat);
//                    it.dateIdentifier = dateIdentifier
//                }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            throw MissingImplementationException("Cannot find a valid implementation for ProblemBinder", e)
//        }
//    }
//}

