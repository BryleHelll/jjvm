package com.github.k0kubun.jjvm.bytecode;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

// https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html
public class ClassFileParser {
    // ClassFile {
    //     u4             magic;
    //     u2             minor_version;
    //     u2             major_version;
    //     u2             constant_pool_count;
    //     cp_info        constant_pool[constant_pool_count-1];
    //     u2             access_flags;
    //     u2             this_class;
    //     u2             super_class;
    //     u2             interfaces_count;
    //     u2             interfaces[interfaces_count];
    //     u2             fields_count;
    //     field_info     fields[fields_count];
    //     u2             methods_count;
    //     method_info    methods[methods_count];
    //     u2             attributes_count;
    //     attribute_info attributes[attributes_count];
    // }
    public ClassFile parse(String filename) throws IOException {
        DataInputStream stream = new DataInputStream(new FileInputStream(filename));

        int magic = stream.readInt();
        int minorVersion = stream.readUnsignedShort();
        int majorVersion = stream.readUnsignedShort();
        int constantPoolCount = stream.readUnsignedShort();
        ConstantPoolInfo[] constantPool = parseConstantPool(stream, constantPoolCount);

        return new ClassFile(
                magic,
                minorVersion,
                majorVersion,
                constantPool
        );
    }

    // cp_info {
    //     u1 tag;
    //     u1 info[];
    // }
    private ConstantPoolInfo[] parseConstantPool(DataInputStream stream, int constantPoolCount) throws IOException {
        constantPoolCount--; // index=0 means "no data", and a pool has `constantPoolCount - 1` elements.
        ConstantPoolInfo[] constantPool = new ConstantPoolInfo[constantPoolCount];
        for (int i = 0; i < constantPoolCount; i++) {
            int tag = stream.readUnsignedByte();
            ConstantType type = ConstantType.fromTag(tag);

            ConstantPoolInfo info;
            if (type == ConstantType.Class) {
                // CONSTANT_Class_info {
                //     u1 tag;
                //     u2 name_index;
                // }
                info = new ConstantPoolInfo.Class(stream.readUnsignedShort());
            } else if (type == ConstantType.Fieldref) {
                // CONSTANT_Fieldref_info {
                //     u1 tag;
                //     u2 class_index;
                //     u2 name_and_type_index;
                // }
                info = new ConstantPoolInfo.Fieldref(stream.readUnsignedShort(), stream.readUnsignedShort());
            } else if (type == ConstantType.Methodref) {
                // CONSTANT_Methodref_info {
                //     u1 tag;
                //     u2 class_index;
                //     u2 name_and_type_index;
                // }
                info = new ConstantPoolInfo.Methodref(stream.readUnsignedShort(), stream.readUnsignedShort());
            } else if (type == ConstantType.InterfaceMethodref) {
                // CONSTANT_InterfaceMethodref_info {
                //     u1 tag;
                //     u2 class_index;
                //     u2 name_and_type_index;
                // }
                info = new ConstantPoolInfo.InterfaceMethodref(stream.readUnsignedShort(), stream.readUnsignedShort());
            } else if (type == ConstantType.String) {
                // CONSTANT_String_info {
                //     u1 tag;
                //     u2 string_index;
                // }
                info = new ConstantPoolInfo.String(stream.readUnsignedShort());
            } else if (type == ConstantType.Integer) {
                // CONSTANT_Integer_info {
                //     u1 tag;
                //     u4 bytes;
                // }
                info = new ConstantPoolInfo.Integer(stream.readInt());
            } else if (type == ConstantType.Float) {
                // CONSTANT_Float_info {
                //     u1 tag;
                //     u4 bytes;
                // }
                info = new ConstantPoolInfo.Float(stream.readInt());
            } else if (type == ConstantType.Long) {
                // CONSTANT_Long_info {
                //     u1 tag;
                //     u4 high_bytes;
                //     u4 low_bytes;
                // }
                info = new ConstantPoolInfo.Long(stream.readInt(), stream.readInt());
            } else if (type == ConstantType.Double) {
                // CONSTANT_Double_info {
                //     u1 tag;
                //     u4 high_bytes;
                //     u4 low_bytes;
                // }
                info = new ConstantPoolInfo.Double(stream.readInt(), stream.readInt());
            } else if (type == ConstantType.NameAndType) {
                // CONSTANT_NameAndType_info {
                //     u1 tag;
                //     u2 name_index;
                //     u2 descriptor_index;
                // }
                info = new ConstantPoolInfo.NameAndType(stream.readUnsignedShort(), stream.readUnsignedShort());
            } else if (type == ConstantType.Utf8) {
                // CONSTANT_Utf8_info {
                //     u1 tag;
                //     u2 length;
                //     u1 bytes[length];
                // }
                byte[] bytes = new byte[stream.readUnsignedShort()];
                stream.read(bytes);
                info = new ConstantPoolInfo.Utf8(bytes);
            } else if (type == ConstantType.MethodHandle) {
                // CONSTANT_MethodHandle_info {
                //     u1 tag;
                //     u1 reference_kind;
                //     u2 reference_index;
                // }
                info = new ConstantPoolInfo.MethodHandle(stream.readUnsignedByte(), stream.readUnsignedShort());
            } else if (type == ConstantType.MethodType) {
                // CONSTANT_MethodType_info {
                //     u1 tag;
                //     u2 descriptor_index;
                // }
                info = new ConstantPoolInfo.MethodType(stream.readUnsignedByte());
            } else if (type == ConstantType.InvokeDynamic) {
                // CONSTANT_InvokeDynamic_info {
                //     u1 tag;
                //     u2 bootstrap_method_attr_index;
                //     u2 name_and_type_index;
                // }
                info = new ConstantPoolInfo.MethodHandle(stream.readUnsignedShort(), stream.readUnsignedShort());
            } else {
                throw new UnsupportedOperationException(String.format("Unhandled ConstantType (tag:%d)", tag));
            }
            constantPool[i] = info;
        }
        return constantPool;
    }
}