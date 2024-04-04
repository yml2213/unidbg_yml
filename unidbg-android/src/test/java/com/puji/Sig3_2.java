package com.puji;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.Module;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.jni.ProxyClassFactory;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.linux.file.SimpleFileIO;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.github.unidbg.virtualmodule.android.JniGraphics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

public class Sig3_2 extends AbstractJni implements IOResolver {

    private final AndroidEmulator emulator;
    private final DvmClass JNICLibrary;
    private final DvmObject<?> JNICLibrary1;
    private final VM vm;

    public Sig3_2() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .addBackendFactory(new Unicorn2Factory(true))
                .setProcessName("com.kwai.thanos")
                // 设置根目录
                .setRootDir(new File("target/rootfs"))
                .build();

        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/puji/puji.apk"));
        vm.setDvmClassFactory(new ProxyClassFactory());

        vm.setJni(this);
        vm.setVerbose(true);
        emulator.getSyscallHandler().addIOResolver(this);  // 文件监控
        new AndroidModule(emulator, vm).register(memory);       // 使用 libandroid.so 的虚拟模块
        new JniGraphics(emulator, vm).register(memory);     // 使用 libjnigraphics.so 的虚拟模块
        DalvikModule dm = vm.loadLibrary("kwsgmain", true);
        JNICLibrary = vm.resolveClass("com.kuaishou.android.security.internal.dispatch.JNICLibrary");
        JNICLibrary1 = vm.resolveClass("com.kuaishou.android.security.internal.dispatch.JNICLibrary").newObject(null);
        dm.callJNI_OnLoad(emulator);

//        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/resources/example_binaries/myprac/libnative-lib.so"), false);

        // 操作 so 的句柄
        Module module = dm.getModule();
        // 执行 JNI_OnLoad 函数
        vm.callJNI_OnLoad(emulator, module);
    }

    public void destroy() throws IOException {
        emulator.close();
    }

    //    int i4, Object[] objArr
    public String doCommandNative(int i4, Object[] objArr) {

        String methodSign = "doCommandNative(I[Ljava/lang/Object;)Ljava/lang/Object;";
//        System.out.println("objArr = " + objArr.toString());

        StringObject obj = JNICLibrary.callStaticJniMethodObject(emulator, methodSign, i4, ProxyDvmObject.createObject(vm, objArr));
        return obj.getValue();
    }


    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("访问 ====> " + pathname);
        if ("/proc/self/maps".equals(pathname)) {
            return FileResult.success(new SimpleFileIO(oflags, new File("/Users/maps"), pathname));
        } else if ("/proc/stat".equals(pathname)) {
            return FileResult.success(new SimpleFileIO(oflags, new File("unidbg-android/src/test/java/com/rootfs/stat"), pathname));
        }
        return null;
    }


    public static void main(String[] args) throws Exception {
        Sig3_2 signUtil = new Sig3_2();
//        DvmObject<?> context = signUtil.vm.resolveClass("android/content/Context").newObject(null);      // context
//        DvmObject<?> context = signUtil.vm.resolveClass("android/app/Application", signUtil.vm.resolveClass("android/content/ContextWrapper", signUtil.vm.resolveClass("android/content/Context"))).newObject(null);

        DvmObject<?> context = signUtil.vm.resolveClass("com/yxcorp/gifshow/App").newObject(null);
//        DvmObject<?> context = signUtil.vm.resolveClass("com/yxcorp/gifshow/App").newObject(null); // context
        signUtil.vm.addLocalObject(context);


        Object[] obj = new Object[8];

        String[] abis = new String[]{"/rest/puji/photo/like6c5a00ae8d879cfeb8120740fbd2f463"};
        ProxyDvmObject.createObject(signUtil.vm, abis);

        obj[0] = ProxyDvmObject.createObject(signUtil.vm, abis);
//        obj[0] = new String[]{"/rest/puji/photo/like6c5a00ae8d879cfeb8120740fbd2f463"};
        obj[1] = "d7b7d042-d4f2-4012-be60-d97ff2429c17";
        obj[2] = "-1";
        obj[3] = "false";
        obj[4] = context;
        obj[5] = "null";
        obj[6] = "false";
        obj[7] = "";
        String sign = signUtil.doCommandNative(10418, obj);
        System.out.println("sign=" + sign);
        signUtil.destroy();
    }


}
