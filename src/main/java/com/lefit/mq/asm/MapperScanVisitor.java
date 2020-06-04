package com.lefit.mq.asm;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;

/**
 * @ProjectName: java-level
 * @Package: com.test.asm
 * @ClassName: TaxVisitor
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/11 上午10:35
 * @Version: 1.0
 */
@Deprecated
public class MapperScanVisitor extends ClassVisitor implements Opcodes {

    public MapperScanVisitor(int api, ClassVisitor cv) {

        super(api, cv);

    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        /*if (name.equals("request")) {

            AnnotationVisitor av1 = mv.visitAnnotation("Ljavax/ws/rs/POST;", true);//在request上添加@POST注解，对比看上面红色粗体类源码

            AnnotationVisitor av2 = mv.visitAnnotation("Ljavax/ws/rs/Path;", true);//同上

            av2.visit("value", "request");

            av2.visitEnd();//这些api用法，查看asm相关说明

            av1.visitEnd();

        }*/

        return mv;
    }

    public AnnotationVisitor visitAnnotation(String var1, boolean var2) {
        if (var1.equals("Lorg/mybatis/spring/annotation/MapperScan;")){
            return null;
        }
        return this.cv != null ? this.cv.visitAnnotation(var1, var2) : null;
    }

}
