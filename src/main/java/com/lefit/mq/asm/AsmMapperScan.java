package com.lefit.mq.asm;

import jdk.internal.org.objectweb.asm.AnnotationVisitor;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AnnotationNode;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @ProjectName: java-level
 * @Package: com.test.asm
 * @ClassName: TestAsm
 * @Description: java类作用描述
 * @Author: WANG KANG
 * @CreateDate: 2020/5/11 上午10:31
 * @Version: 1.0
 */
@Deprecated
public class AsmMapperScan extends ClassLoader{

    public static void parse(File f) throws IOException {
        List<String> valu = null;
        ClassReader cr = new ClassReader(new FileInputStream(f));
        ClassNode cn = new ClassNode();//创建ClassNode,读取的信息会封装到这个类里面
        cr.accept(cn, 0);//开始读取
        List<AnnotationNode> annotations = cn.visibleAnnotations;//获取声明的所有注解
        if (annotations != null) {//便利注解
            for (AnnotationNode an : annotations) {
                //获取注解的描述信息
                String anno = an.desc.replaceAll("/", ".");
                String annoName = anno.substring(1, anno.length() - 1);
                if ("org.mybatis.spring.annotation.MapperScan".equals(annoName)) {
                    String className = cn.name.replaceAll("/", ".");
                    //获取注解的属性名对应的值，（values是一个集合，它将注解的属性和属性值都放在了values中，通常奇数为值偶数为属性名）
                    valu = (List<String>) an.values.get(1);
                    valu.add("com.lefit.mq.repository.dao");
                }
            }
        }


        ClassWriter cw=new ClassWriter(ClassWriter.COMPUTE_MAXS);
        MapperScanVisitor myv=new MapperScanVisitor(Opcodes.ASM4,cw);

        AnnotationVisitor av0 = cw.visitAnnotation("Lorg/mybatis/spring/annotation/MapperScan;", true);//在接口上添加注解@MapperScan
        accept(av0, "value", valu);
        accept(av0, "sqlSessionFactoryRef",  "sqlSessionFactoryBean");
        //av0.visitEnd();
        cr.accept(myv, 0);

        byte[] code=cw.toByteArray();//最终想要类的字节码

        FileOutputStream fos = new FileOutputStream(f.getPath());//覆盖当前class文件
        fos.write(code);
        fos.close();

    }

    static void accept(final AnnotationVisitor av, final String name, final Object value) {
        if (value instanceof String[]) {
            String[] typeconst = (String[]) value;
            av.visitEnum(name, typeconst[0], typeconst[1]);
        } else if (value instanceof AnnotationNode) {
            AnnotationNode an = (AnnotationNode) value;
            an.accept(av.visitAnnotation(name, an.desc));
        } else if (value instanceof List) {
            AnnotationVisitor v = av.visitArray(name);
            List array = (List) value;
            for (int j = 0; j < array.size(); ++j) {
                accept(v, null, array.get(j));
            }
            v.visitEnd();
        } else {
            av.visit(name, value);
        }
    }
}
