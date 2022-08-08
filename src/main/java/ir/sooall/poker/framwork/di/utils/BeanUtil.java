package ir.sooall.poker.framwork.di.utils;

import ir.sooall.poker.framwork.di.Configor;
import ir.sooall.poker.framwork.di.annotations.Bean;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.*;


public class BeanUtil {

    public static class ContextAndMethod {
        private final Object context;
        private final MethodHandle handle;

        public ContextAndMethod(Object context, MethodHandle handle) {
            this.context = context;
            this.handle = handle;
        }

        public Object getContext() {
            return context;
        }

        public MethodHandle getHandle() {
            return handle;
        }
    }

    private BeanUtil() {
        super();
    }

    public static List<BeanUtil.ContextAndMethod> handlers(Collection<Class<?>> clazzes) throws Throwable {
        var publicLookup = MethodHandles.publicLookup();
        var flattenMethodHandlers = new ArrayList<BeanUtil.ContextAndMethod>();
        for (var clazz : clazzes) {
            var instance = clazz.getDeclaredConstructor().newInstance();
            var methodHandlers = Arrays.stream(clazz.getDeclaredMethods()).filter(m -> m.isAnnotationPresent(Bean.class))
                .map(method -> {
                    var type = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
                    var name = method.getName();
                    try {
                        return new ContextAndMethod(instance, publicLookup.findVirtual(clazz, name, type));
                    } catch (NoSuchMethodException | IllegalAccessException e) {
                        e.printStackTrace();
                        return null;
                    }

                }).toList();
            flattenMethodHandlers.addAll(methodHandlers);
        }
        return flattenMethodHandlers;

    }


    public static void resolve(Configor configor, ContextAndMethod contextAndMethod) throws Throwable {
        var handle = contextAndMethod.getHandle();
        var resolvedClass = configor.getBeanInstance(handle.type().returnType());
        if (resolvedClass == null) {
            List<Object> satisfiedParamObjects = new ArrayList<>();
            List<Class<?>> unSatisfiedParams = new ArrayList<>();
            var paramList = handle.type().parameterList();
            for (var param : paramList.subList(1, paramList.size())) {
                if (configor.applicationScope.containsKey(param)) {
                    satisfiedParamObjects.add(configor.applicationScope.get(param));
                } else {
                    unSatisfiedParams.add(param);
                }
            }
            if (satisfiedParamObjects.size() != handle.type().parameterCount() - 1) {
                for (var param : unSatisfiedParams) {
                    resolve(configor, configor.handleMap.get(param));
                }
            } else {
                configor.applicationScope.put(handle.type().returnType(), handle.invoke(contextAndMethod.getContext(), satisfiedParamObjects.toArray()));
            }
        }

    }

}
