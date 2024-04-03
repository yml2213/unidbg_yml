package com.puji;

import com.alibaba.fastjson.util.IOUtils;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.DvmObject;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;

@Slf4j
@Data

public class KsJNICLibrary {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    private final DvmClass KsJNICLibraryUtils;

    private final boolean logging;

    KsJNICLibrary(boolean logging) {
        this.logging = logging;

        emulator = AndroidEmulatorBuilder.for32Bit()
                .setProcessName("com.test")
                .addBackendFactory(new Unicorn2Factory(true))
                .build(); // 创建模拟器实例，要模拟32位或者64位，在这里区分
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析

        vm = emulator.createDalvikVM(); // 创建Android虚拟机
        new AndroidModule(emulator, vm).register(memory);
        vm.setVerbose(logging); // 设置是否打印Jni调用细节
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/puji/libkwsgmain.so"), false); // 加载libttEncrypt.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        dm.callJNI_OnLoad(emulator); // 手动执行JNI_OnLoad函数
        module = dm.getModule(); // 加载好的libttEncrypt.so对应为一个模块

        KsJNICLibraryUtils = vm.resolveClass("com/kuaishou/android/security/internal/dispatch/JNICLibrary");
    }

    void destroy() {
        IOUtils.close(emulator);
        if (logging) {
            System.out.println("destroy");
        }
    }

    public static void main(String[] args) throws Exception {
        KsJNICLibrary test = new KsJNICLibrary(true);

        String guid = "d7b7d042-d4f2-4012-be60-d97ff2429c17";
        DvmObject<?> contexts = test.vm.resolveClass("android/content/Context").newObject(null);
        Object[] objArr = {
                new String[]{"/rest/puji/photo/like6c5a00ae8d879cfeb8120740fbd2f463"},
                guid,
                -1,
                false,
                contexts,//contexts,
                null,
                false,
                ""
        };
        String data = test.doCommandNative(10418, ProxyDvmObject.createObject(test.vm, objArr));
        System.out.println(data);

        test.destroy();
    }


    String doCommandNative(Object... args) {
        String methodSign = "doCommandNative(I[Ljava/lang/Object;)Ljava/lang/Object;";
        DvmObject<?> value = KsJNICLibraryUtils.callStaticJniMethodObject(emulator, methodSign, args);
        return value.getValue().toString();
    }

}