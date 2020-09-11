package com.neucore.neusdk_demo.aop;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect  //①
public class MethodAspect {

    private static final String TAG = "System";
    long start = 0;
    @Before("execution(* com.neucore.neusdk_demo..*(..))")
    public void beforeMethodCall(JoinPoint joinPoint) {
        start = System.currentTimeMillis();
    }
    @After("execution(* com.neucore.neusdk_demo..*(..))")
    public void afterMethodCall(JoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        //signature = joinPoint.getStaticPart().getSignature();
        if(signature!=null){
            long timeused = (System.currentTimeMillis()-start);
            if(timeused>50) {
                Log.w(TAG,signature.getDeclaringType().getSimpleName()+"."+signature.getName() + " timeused->" + timeused); //④
            }
        }
    }

    @AfterThrowing(pointcut = "execution(* com.neucore.neusdk_demo..*(..))" , throwing = "ex")
    public void afterThrowMethodCall(JoinPoint joinPoint,Throwable ex) throws Throwable{
        Signature signature = joinPoint.getSignature();
        Log.e(TAG,signature.getDeclaringType().getSimpleName()+"."+signature.getName(),ex); //④
        throw ex;
    }
}