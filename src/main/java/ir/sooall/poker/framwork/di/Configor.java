package ir.sooall.poker.framwork.di;

import ir.sooall.poker.framwork.di.annotations.Configuration;
import ir.sooall.poker.framwork.di.utils.BeanUtil;
import org.burningwave.core.assembler.ComponentContainer;
import org.burningwave.core.classes.ClassCriteria;
import org.burningwave.core.classes.ClassHunter;
import org.burningwave.core.classes.SearchConfig;
import org.burningwave.core.function.TriFunction;

import javax.management.RuntimeErrorException;
import java.lang.invoke.MethodHandle;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Configor {
    public Map<Class<?>, Class<?>> diMap;
    public Map<Class<?>, Object> applicationScope;
    public Map<Class<?>, BeanUtil.ContextAndMethod> handleMap;


    private static Configor configor;

    private Configor() {
        super();
        diMap = new HashMap<>();
        applicationScope = new HashMap<>();
        handleMap = new HashMap<>();
    }


    public static void run(Class<?> mainClass) {
        try {
            synchronized (Injector.class) {
                if (configor == null) {
                    configor = new Configor();
                    configor.initFramework(mainClass);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Class<T> clazz) {
        try {
            return (T) configor.getBeanInstance(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initFramework(Class<?> mainClass) {

//        Class<?>[] classes = getClasses(mainClass.getPackage().getName(), true);

        ComponentContainer componentContainer = ComponentContainer.getInstance();
        ClassHunter classHunter = componentContainer.getClassHunter();

        String packageRelPath = mainClass.getPackage().getName().replace(".", "/");
        var searchConfig = SearchConfig
            .forResources(packageRelPath)
            .by(ClassCriteria.create().allThoseThatMatch(cls -> cls.getAnnotation(Configuration.class) != null));

        try (ClassHunter.SearchResult result = classHunter.findBy(searchConfig)) {
            Collection<Class<?>> types = result.getClasses();
            var handlers = BeanUtil.handlers(types);
            iterateOverHandlers(handlers, putInDiMap, invokeHandler);
            iterateOverHandlers(handlers, resolveDependencies);


        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private final Function<BeanUtil.ContextAndMethod, Void> putInDiMap = (contextAndHandler) -> {
        var returnType = contextAndHandler.getHandle().type().returnType();
        var interfaces = returnType.getInterfaces();

        handleMap.put(returnType, contextAndHandler);
        if (interfaces.length == 0) {
            diMap.put(returnType, returnType);
        } else {
            for (Class<?> iface : interfaces) {
                diMap.put(returnType, iface);
            }
        }
        return null;
    };


    private final Function<BeanUtil.ContextAndMethod, Void> invokeHandler = (contextAndHandler) -> {
        if (contextAndHandler.getHandle().type().parameterCount() == 1) {
            try {
                applicationScope.put(contextAndHandler.getHandle().type().returnType(), contextAndHandler.getHandle().invoke(contextAndHandler.getContext()));
            } catch (Throwable e) {
                return null;
            }
        }
        return null;
    };

    private final Function<BeanUtil.ContextAndMethod, Void> resolveDependencies = (contextAndHandler) -> {
        try {
            BeanUtil.resolve(this, contextAndHandler);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    };


    @SafeVarargs
    private void iterateOverHandlers(List<BeanUtil.ContextAndMethod> contextAndHandlers
        , Function<BeanUtil.ContextAndMethod, Void>... functions) {
        for (var contextAndHandler : contextAndHandlers) {
            Arrays.stream(functions).forEach(fn -> fn.apply(contextAndHandler));
        }
    }

    /**
     * Overload getBeanInstance to handle qualifier and autowire by type
     */
    public <T> Object getBeanInstance(Class<T> interfaceClass) {
        Class<?> implementationClass = getImplementationClass(interfaceClass);
        return applicationScope.get(implementationClass);
    }

    private Class<?> getImplementationClass(Class<?> interfaceClass) {
        Set<Map.Entry<Class<?>, Class<?>>> implementationClasses = diMap.entrySet().stream()
            .filter(entry -> entry.getValue() == interfaceClass).collect(Collectors.toSet());
        String errorMessage = "";
        if (implementationClasses.size() == 0) {
            errorMessage = "no implementation found for interface " + interfaceClass.getName();
        } else if (implementationClasses.size() == 1) {
            Optional<Map.Entry<Class<?>, Class<?>>> optional = implementationClasses.stream().findFirst();
            return optional.get().getKey();
        } else {
            errorMessage = "There are " + implementationClasses.size() + " of interface " + interfaceClass.getName()
                + " Expected single implementation or make use of @CustomQualifier to resolve conflict";
        }
        throw new RuntimeErrorException(new Error(errorMessage));
    }
}