package com.puji;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.jni.ProxyClassFactory;
import com.github.unidbg.linux.android.dvm.jni.ProxyDvmObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sig3_2 {

    private final AndroidEmulator emulator;

    private final DvmClass JNICLibrary;
    private final VM vm;

    public Sig3_2() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .addBackendFactory(new Unicorn2Factory(true))
                .setProcessName("com.kwai.thanos")
                .build();

        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/puji/puji.apk"));
        vm.setDvmClassFactory(new ProxyClassFactory());
        vm.setVerbose(true);
        new AndroidModule(emulator, vm).register(memory);
        vm.loadLibrary(new File("unidbg-android/src/test/java/com/puji/libc++_shared.so"), false);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/puji/libkwsgmain.so"), true);
        JNICLibrary = vm.resolveClass("com/kuaishou/android/security/internal/dispatch/JNICLibrary");
        dm.callJNI_OnLoad(emulator);
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

    public String doCommandNative1(String p1, String p2, Map<String, byte[]> map, String p3, int i) {

        String methodSign = "getSign0(Ljava/lang/String;Ljava/lang/String;Ljava/util/Map;Ljava/lang/String;I)Ljava/lang/String;";
        StringObject obj = JNICLibrary.callStaticJniMethodObject(emulator, methodSign, p1, p2, ProxyDvmObject.createObject(vm, map), p3, i);
        return obj.getValue();
    }

//    private synchronized String sign(String p1, String p2, Map<String, String> paramMap, String p3, int i) {
//        Map<String, byte[]> map = new HashMap<>();
//        for (String key : paramMap.keySet()) {
//            map.put(key, paramMap.get(key).getBytes(StandardCharsets.UTF_8));
//        }
//        return doCommandNative(p1, p2, map, p3, i);
//    }


    public static void main(String[] args) throws Exception {
        Sig3_2 signUtil = new Sig3_2();
        DvmObject<?> context = signUtil.vm.resolveClass("android/content/Context").newObject(null);      // context
        Object[] obj = new Object[8];
        obj[0] = new String[]{"/rest/puji/photo/like6c5a00ae8d879cfeb8120740fbd2f463"};
        obj[1] = "d7b7d042-d4f2-4012-be60-d97ff2429c17";
        obj[2] = -1;
        obj[3] = false;
        obj[4] = context;
        obj[5] = null;
        obj[6] = false;
        obj[7] = "";
        String sign = signUtil.doCommandNative(10418, obj);
        System.out.println("sign=" + sign);
        signUtil.destroy();
    }


}
