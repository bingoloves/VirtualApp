package cn.cqs.aop.aspect;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by bingo on 2020/12/4.
 *
 * @Author: bingo
 * @Email: 657952166@qq.com
 * @Description: 测试类
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/12/4
 */
@Aspect
public class ActivityFinishAspect {
    @Pointcut("execution(* finish(..))")
    public void methodPointcut(){}

    @Around("methodPointcut()")
    public void aroundJoinPoint(final ProceedingJoinPoint joinPoint) throws Throwable {
        Log.e("ActivityFinishAspect"," finish");
        joinPoint.proceed();
    }
}
