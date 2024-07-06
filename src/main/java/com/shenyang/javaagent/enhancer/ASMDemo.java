package com.shenyang.javaagent.enhancer;

import org.apache.commons.io.FileUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.File;
import java.io.IOException;
//字节码文件的版本号 需要自己去官方文档去找 JDK17对应的是7
import static org.objectweb.asm.Opcodes.ASM7;
import static org.objectweb.asm.Opcodes.ICONST_0;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/6 上午11:44
 */

//ASM入门案例，向每个方法添加一行字节码指令
public class ASMDemo {
    public static void main(String[] args) throws IOException {
        //1.从本地读取一个字节码文件 byte[]
        byte[] bytes = FileUtils.readFileToByteArray(new File("E:\\WorkAndClass\\agent\\shen-agent\\target\\test-classes\\Test.class"));
        //2.通过ASM修改字节码文件
        //2.1将二进制的数据转换为可以解析的内容
        ClassReader classReader = new ClassReader(bytes);
        //2.2创建visitor对象，修改字节码信息   0是默认参数
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM7, classWriter){
            /**
             * 对字节码信息中的方法信息进行处理（增强）
             * @return MethodVisitor
             */
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                //原始对象
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                //返回自定义的 MethodVisitor对象 this.api指向的就是ASM7
                return new MethodVisitor(this.api,mv){
                    //修改字节码指令
                    @Override
                    public void visitCode() {
                        /*插入一行字节码指令 ICONST_0 将0放入操作数栈中
                        可以进入MethodVisitor源码中，在注解中找到对应的指令，然后使用对应的方法*/
                        visitInsn(ICONST_0);

                    }
                };
            }
        } ;
        classReader.accept(classVisitor,0);
        //3.将修改完的字节码信息写入文件中，进行替换
        FileUtils.writeByteArrayToFile(new File("E:\\WorkAndClass\\agent\\shen-agent\\target\\test-classes\\Test.class"),classWriter.toByteArray());
    }
}
