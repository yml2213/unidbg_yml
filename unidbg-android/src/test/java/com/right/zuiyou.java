package com.right;

import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class zuiyou extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    zuiyou() {
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.xiaochuankeji.tieba").build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android\\src\\test\\java\\com\\right\\right573.apk")); // 创建Android虚拟机
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android\\src\\test\\java\\com\\right\\libnet_crypto.so"), true); // 加载so到虚拟内存
        module = dm.getModule(); //获取本SO模块的句柄

        vm.setJni(this);
        vm.setVerbose(true);
        dm.callJNI_OnLoad(emulator);
    }

    ;

    public void native_init() {
        // 0x4a069
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclass，直接填0，一般用不到。
        module.callFunction(emulator, 0x4a069, list.toArray());
    }


    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "android/content/Context->getClass()Ljava/lang/Class;":{
                return dvmObject.getObjectType();
            }
            case "java/lang/Class->getSimpleName()Ljava/lang/String;":{
                return new StringObject(vm, "AppController");
            }
            case "android/content/Context->getFilesDir()Ljava/io/File;":
            case "java/lang/String->getAbsolutePath()Ljava/lang/String;": {
                return new StringObject(vm, "/data/user/0/cn.xiaochuankeji.tieba/files");
            }

        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    };


    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "com/izuiyou/common/base/BaseApplication->getAppContext()Landroid/content/Context;":
                return vm.resolveClass("android/content/Context").newObject(null);
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }


    public boolean callStaticBooleanMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "android/os/Debug->isDebuggerConnected()Z":
                return false;
        }
        throw new UnsupportedOperationException(signature);
    }

    public int callStaticIntMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "android/os/Process->myPid()I":
                return emulator.getPid();
        }
        throw new UnsupportedOperationException(signature);
    }

    private String callSign() {
        // 准备入参
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclass，直接填0，一般用不到。
        list.add(vm.addLocalObject(new StringObject(vm, "12345")));
        ByteArray plainText = new ByteArray(vm, "r0ysue".getBytes(StandardCharsets.UTF_8));
        list.add(vm.addLocalObject(plainText));
        // 参数列表转换为数组
        Object[] argsArray = list.toArray();
        Number[] numbers = new Number[]{module.callFunction(emulator, 0x4a28D, argsArray)};
        String result = vm.getObject(numbers[0].intValue()).getValue().toString();

        return result;
    }

    ;

    public static void main(String[] args) throws Exception {
        zuiyou test = new zuiyou();
        test.native_init();
        System.out.println(test.callSign());
    }
}

