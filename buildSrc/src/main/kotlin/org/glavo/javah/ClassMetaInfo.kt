package org.glavo.javah

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.FieldVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import java.util.*

internal class ClassMetaInfo : ClassVisitor(Opcodes.ASM7) {
    val constants: MutableList<Constant> = LinkedList()
    val methods: MutableList<NativeMethod> = LinkedList()

    private val counts: MutableMap<String, Int> = HashMap()

    var superClassName: ClassName? = null
    var name: ClassName? = null

    override fun visit(version: Int, access: Int, name: String, signature: String?, superName: String?, interfaces: Array<String?>?) {
        superClassName = if (superName == null) null else ClassName.of(superName.replace('/', '.'))
        this.name = ClassName.of(name.replace('/', '.'))
    }

    override fun visitMethod(access: Int, name: String, descriptor: String, signature: String?, exceptions: Array<String?>?): MethodVisitor? {
        counts[name] = counts.getOrDefault(name, 0) + 1
        if (access and Opcodes.ACC_NATIVE != 0) {
            methods.add(NativeMethod.of(access, name, descriptor))
        }

        return null
    }

    override fun visitField(access: Int, name: String, descriptor: String, signature: String?, value: Any?): FieldVisitor? {
        if (value != null && Constant.isValid(name, value)) {
            constants.add(Constant.of(name, value))
        }

        return null
    }

    fun isOverloadMethod(method: NativeMethod) = counts.getOrDefault(method.name, 1) > 1
}
