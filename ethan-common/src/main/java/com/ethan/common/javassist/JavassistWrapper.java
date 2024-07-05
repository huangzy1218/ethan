package com.ethan.common.javassist;

import javassist.*;

import java.lang.reflect.Method;
import java.security.ProtectionDomain;

/**
 * Javassist wrapper.
 *
 * @author Huang Z.Y.
 */
public class JavassistWrapper {

    private final Class<?> wrapperClass;
    private Class<?> targetClass;

    private JavassistWrapper(Class<?> targetClass, Class<?> wrapperClass) {
        this.targetClass = targetClass;
        this.wrapperClass = wrapperClass;
    }

    public static JavassistWrapper getWrapper(Class<?> c) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass(c.getName() + "Wrapper" + System.currentTimeMillis());

        // Add constructor
        CtConstructor constructor = new CtConstructor(new CtClass[]{}, ctClass);
        constructor.setBody("{}");
        ctClass.addConstructor(constructor);

        // Add invokeMethod method
        CtMethod invokeMethod = new CtMethod(pool.get("java.lang.Object"), "invokeMethod",
                new CtClass[]{
                        pool.get("java.lang.Object"),
                        pool.get("java.lang.String"),
                        pool.get("java.lang.Class[]"),
                        pool.get("java.lang.Object[]")
                }, ctClass);

        invokeMethod.setModifiers(Modifier.PUBLIC);
        invokeMethod.setBody(
                "{ " +
                        "    java.lang.reflect.Method m = Class.forName(\"" + c.getName() + "\").getMethod($2, $3); " +
                        "    return m.invoke($1, $4); " +
                        "}"
        );
        ctClass.addMethod(invokeMethod);

        byte[] byteCode = ctClass.toBytecode();
        Class<?> wrapperClass = new InnerClassLoader(c.getClassLoader()).defineClass(ctClass.getName(), byteCode);

        return new JavassistWrapper(c, wrapperClass);
    }

    private static Class<?> defineClass(ClassLoader loader, String name, byte[] byteCode) throws Exception {
        Method method = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class, ProtectionDomain.class);
        method.setAccessible(true);
        return (Class<?>) method.invoke(loader, name, byteCode, 0, byteCode.length, null);
    }

    public Object invokeMethod(Object instance, String methodName, Class<?>[] parameterTypes, Object[] args) throws Throwable {
        java.lang.reflect.Method invokeMethod = wrapperClass.getMethod("invokeMethod", Object.class, String.class, Class[].class, Object[].class);
        return invokeMethod.invoke(wrapperClass.getDeclaredConstructor().newInstance(), instance, methodName, parameterTypes, args);
    }

    private static class InnerClassLoader extends ClassLoader {
        InnerClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(String name, byte[] byteCode) {
            return super.defineClass(name, byteCode, 0, byteCode.length);
        }
    }

}
    