package com.github.k0kubun.jjvm.bytecode;

public class ClassFile {
    private int magic;
    private int minorVersion;
    private int majorVersion;
    private ConstantPoolInfo[] constantPool;
    private int accessFlags;
    private int thisClass;
    private int superClass;
    private int[] interfaces;

    public ClassFile(int magic, int minorVersion, int majorVersion, ConstantPoolInfo[] constantPool,
                     int accessFlags, int thisClass, int superClass, int[] interfaces) {
        this.magic = magic;
        this.minorVersion = minorVersion;
        this.majorVersion = majorVersion;
        this.constantPool = constantPool;
        this.accessFlags = accessFlags;
        this.thisClass = thisClass;
        this.superClass = superClass;
        this.interfaces = interfaces;
    }

    public String disassemble() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("class Hello\n"));
        builder.append(String.format("  magic: 0x%X\n", magic));
        builder.append(String.format("  minor version: %d\n", minorVersion));
        builder.append(String.format("  major version: %d\n", majorVersion));
        builder.append(String.format("  flags: TODO\n"));
        builder.append(disassembleConstantPool());
        return builder.toString();
    }

    private String disassembleConstantPool() {
        StringBuilder builder = new StringBuilder();
        builder.append("Constant pool:\n");
        for (int i = 0; i < constantPool.length; i++) {
            String typeName = constantPool[i].getType().toString();
            builder.append(String.format("%5s = %-19s", String.format("#%d", i + 1), typeName));
            builder.append("TODO");
            builder.append("\n");
        }
        return builder.toString();
    }
}
