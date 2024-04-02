package com.lession2;
// 导入通用且标准的类库

import com.github.unidbg.Emulator;
import com.github.unidbg.hook.hookzz.*;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.pointer.UnidbgPointer;
import com.github.unidbg.utils.Inspector;
import com.lession1.oasis;
import com.sun.jna.Pointer;
import keystone.Keystone;
import keystone.KeystoneArchitecture;
import keystone.KeystoneEncoded;
import keystone.KeystoneMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class sina extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    sina() {
        // 创建模拟器实例,进程名建议依照实际进程名填写，可以规避针对进程名的校验
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.sina.International").build();
        // 获取模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));
        // 创建Android虚拟机,传入APK，Unidbg可以替我们做部分签名校验的工作
        vm = emulator.createDalvikVM(new File("unidbg-android\\src\\test\\java\\com\\lession2\\sinaInternational.apk"));
        // 加载目标SO // 加载so到虚拟内存  true
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android\\src\\test\\java\\com\\lession2\\libutility.so"), true);
        //获取本SO模块的句柄,后续需要用它
        module = dm.getModule();
        vm.setJni(this);    // 设置JNI
        vm.setVerbose(true);    // 打印日志
        // 样本连JNI OnLoad都没有
        // dm.callJNI_OnLoad(emulator); // 调用JNI OnLoad

    }

    ;


    public String calculateS_1() {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());   // 第一个参数 env
        list.add(0);   // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);      // context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm, "12345")));
        list.add(vm.addLocalObject(new StringObject(vm, "r0ysue")));
        // 代码 thumb 模式, 地址+1
        // 参数列表转换为数组
        Object[] argsArray = list.toArray();
        Number[] numbers = new Number[]{module.callFunction(emulator, 0x1E7C + 1, argsArray)};
        String result = vm.getObject(numbers[0].intValue()).getValue().toString();

        return result;
    }

    public String calculateS_2() {

        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);      // context
        // 直接创建 argsArray
        Object[] argsArray = {
                vm.getJNIEnv(),
                0,
                vm.addLocalObject(context),
                vm.addLocalObject(new StringObject(vm, "12345")),
                vm.addLocalObject(new StringObject(vm, "r0ysue"))
        };

        Number[] numbers = new Number[]{module.callFunction(emulator, 0x1E7C + 1, argsArray)};
        System.out.println("========");
        System.out.println(numbers);
        System.out.println(Arrays.toString(numbers));
        System.out.println(numbers[0].intValue());
        System.out.println(vm.getObject(numbers[0].intValue()));
        System.out.println("========");


        String result = vm.getObject(numbers[0].intValue()).getValue().toString();

        return result;
    }


    public String calculateS() {
        // 构建函数参数格式
        List<Object> args = new ArrayList<>(10);
        // 各种基本参数格式兼容
        // 参数1：JNIEnv *env
        args.add(vm.getJNIEnv());
        // 参数2：jobject或jclass 用不到直接填0即可
        // 创建 jobject， 如果没用到的话可以不写
        // cNative = vm.resolveClass("com/xxx/xxx");
        // DvmObject<?> cnative = cNative.newObject(null);
        // args.add(cnative.hashCode());
        args.add(0);
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);      // context
        args.add(vm.addLocalObject(context));
        // 参数3 字符串对象
        String input = "12345";
        args.add(vm.addLocalObject(new StringObject(vm, input)));

        // 参数4 字符串对象
        String input2 = "r0ysue";
        args.add(vm.addLocalObject(new StringObject(vm, input2)));
//        // 参数4 bytes 数组
//        String input2 = "r0ysue";
//        byte[] input_bytes = input2.getBytes(StandardCharsets.UTF_8);
//        ByteArray input_byte_array = new ByteArray(vm, input_bytes);
//        args.add(vm.addLocalObject(input_byte_array));
        // 参数5 bool
        // false 填 0，true 填 1
//        args.add(1);
        // unidbg 主动调用函数
        // 第二个参数是函数偏移量(thumb 记得+1)
        // 第三个参数是参数列表
        Number number = module.callFunction(emulator, 0x1E7C + 1, args.toArray());

        //        // unicorn trace（贼好用！！！堪比 ida trace！！！）
        //        String traceFile = "trace.txt";
        //        PrintStream traceStream = null;
        //        try {
        //            traceStream = new PrintStream(new FileOutputStream(traceFile), true);
        //        } catch (FileNotFoundException e) {
        //            e.printStackTrace();
        //        }
        //        // 核心 trace 开启代码，也可以自己指定函数地址和偏移量
        //        emulator.traceCode(module.base, module.base + module.size).setRedirect(traceStream);
//         获取最终返回值，同时运行过程中的汇编代码和寄存器值会写入到文件中
        System.out.println("===================================");
        System.out.println(vm.getObject(number.intValue()).getValue().toString());
        System.out.println(vm.getObject(number.intValue()));
        System.out.println(vm.getObject(number.intValue()).getValue());
        System.out.println("===================================");

        return vm.getObject(number.intValue()).getValue().toString();


    }


    // 第一种打补丁
    public void patchVerify() {
        int patchCode = 0x4FF00100;  // mov r0,1”的机器码 就是 4FF00100
        emulator.getMemory().pointer(module.base + 0x1E86).setInt(0, patchCode);
    }

    // 第二种打补丁
    public void patchVerify1() {
        Pointer pointer = UnidbgPointer.pointer(emulator, module.base + 0x1E86);
        assert pointer != null;
        byte[] code = pointer.getByteArray(0, 4);
        if (!Arrays.equals(code, new byte[]{(byte) 0xFF, (byte) 0xF7, (byte) 0xEB, (byte) 0xFE})) { // BL sub_1C60
            throw new IllegalStateException(Inspector.inspectString(code, "patch32 code=" + Arrays.toString(code)));
        }
        try (Keystone keystone = new Keystone(KeystoneArchitecture.Arm, KeystoneMode.ArmThumb)) {
            KeystoneEncoded encoded = keystone.assemble("mov r0,1");    // Keystone 把patch代码“mov r0,1"转成机器码
            byte[] patch = encoded.getMachineCode();
            if (patch.length != code.length) {
                throw new IllegalStateException(Inspector.inspectString(patch, "patch32 length=" + patch.length));
            }
            pointer.write(0, patch, 0, patch.length);
        }
    }

    ;

    public void HookMDStringold() {
        // 加载 HookZz
        IHookZz hookZz = HookZz.getInstance(emulator);
        hookZz.wrap(module.base + 0x1BD0 + 1, new WrapCallback<HookZzArm32RegisterContext>() {
            @Override
            // 类似于frida onEnter
            public void preCall(Emulator<?> emulator, HookZzArm32RegisterContext ctx, HookEntryInfo info) {
                // 类似于 frida args[0]
                Pointer input = ctx.getPointerArg(0);
                System.out.println("input:" + input.getString(0));
            }

            @Override
            // 类似于 frida onLeave
            public void postCall(Emulator<?> emulator, HookZzArm32RegisterContext ctx, HookEntryInfo info) {
                Pointer result = ctx.getPointerArg(0);
                System.out.println("input:" + result.getString(0));
            }
        });
    }

    public static void main(String[] args) {
        sina test = new sina();
        test.patchVerify1();
        test.HookMDStringold();
        System.out.println(test.calculateS());
    }


}
