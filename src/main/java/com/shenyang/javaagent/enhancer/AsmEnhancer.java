package com.shenyang.javaagent.enhancer;

import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

/**
 * @author shenyang
 * @version 1.0
 * @info shen-agent
 * @since 2024/7/6 下午1:05
 */
public class AsmEnhancer {

    public static byte[] enhanceClass(byte[] bytes) {
        //2.1将二进制的数据转换为可以解析的内容
        ClassReader classReader = new ClassReader(bytes);
        //2.2创建visitor对象，修改字节码信息   0是默认参数
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor classVisitor = new ClassVisitor(ASM7, classWriter) {
            /**
             * 对字节码信息中的方法信息进行处理（增强）
             * @return MethodVisitor
             */
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                //原始对象
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                //返回自定义的 MethodVisitor对象 this.api指向的就是ASM7
                return new MyMethodVisitor(this.api,mv);
            }
        };
        classReader.accept(classVisitor, 0);
        return classWriter.toByteArray();
    }
}

class MyMethodVisitor extends MethodVisitor {

    public MyMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    /**
     * 方法执行一开始调用 System.out.println 开始执行 方法
     */
    @Override
    public void visitCode() {
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        mv.visitLdcInsn("开始执行");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        super.visitCode();
    }

    /**
     * 返回时执行 System.out.println 结束执行
     * @param opcode 返回的是对象？
     */
    @Override
    public void visitInsn(int opcode) {
        if (opcode == ARETURN || opcode == RETURN) {
            mv.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("结束执行");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }

    /**
     * 制定最大栈深度20，最大局部变量表大小是50
     */
    @Override
    public void visitEnd() {
        mv.visitMaxs(20, 50);
        super.visitEnd();
    }

}
