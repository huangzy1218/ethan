package com.ethan.common.javassist;

import javassist.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Proxy.
 *
 * @author Huang Z.Y.
 */
public class JavassistProxy {

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h) throws Exception {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.makeClass("JavassistProxy" + System.currentTimeMillis());

        // Sets the interface that the proxy class implements
        for (Class<?> iface : interfaces) {
            ctClass.addInterface(pool.get(iface.getName()));
        }

        // Add the InvocationHandler field
        CtField handlerField = new CtField(pool.get(InvocationHandler.class.getName()), "handler", ctClass);
        ctClass.addField(handlerField);

        // 添加构造方法
        CtConstructor constructor = new CtConstructor(new CtClass[]{pool.get(InvocationHandler.class.getName())}, ctClass);
        constructor.setBody("{this.handler = $1;}");
        ctClass.addConstructor(constructor);

        // Add method implementations for each interface method
        for (Class<?> iface : interfaces) {
            for (Method method : iface.getMethods()) {
                String methodName = method.getName();
                CtClass returnType = pool.get(method.getReturnType().getName());
                CtClass[] paramTypes = new CtClass[method.getParameterCount()];
                for (int i = 0; i < method.getParameterCount(); i++) {
                    paramTypes[i] = pool.get(method.getParameterTypes()[i].getName());
                }

                CtMethod ctMethod = new CtMethod(returnType, methodName, paramTypes, ctClass);
                StringBuilder methodBody = new StringBuilder();
                methodBody.append("{\n")
                        .append("  try {\n")
                        .append("    java.lang.reflect.Method m = ").append(iface.getName()).append(".class.getMethod(\"")
                        .append(methodName).append("\", new Class[]{");

                for (int i = 0; i < method.getParameterCount(); i++) {
                    if (i > 0) {
                        methodBody.append(", ");
                    }
                    methodBody.append(method.getParameterTypes()[i].getName()).append(".class");
                }

                methodBody.append("});\n")
                        .append("    return (").append(method.getReturnType().getName()).append(") ")
                        .append("handler.invoke(this, m, $args);\n")
                        .append("  } catch (Throwable t) {\n")
                        .append("    throw new RuntimeException(t);\n")
                        .append("  }\n")
                        .append("}");

                ctMethod.setBody(methodBody.toString());
                ctClass.addMethod(ctMethod);
            }
        }

        // Create a proxy class
        Class<?> proxyClass = ctClass.toClass(loader, null);
        return proxyClass.getConstructor(InvocationHandler.class).newInstance(h);
    }

}
    