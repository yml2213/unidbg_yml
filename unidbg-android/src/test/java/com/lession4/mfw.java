package com.lession4;

import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class mfw extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;


    mfw() {
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.mfw.roadbook").build();   // 创建模拟器实例
        final Memory memory = emulator.getMemory();     // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));     // 设置系统类解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/lession4/mafengwo_ziyouxing.apk"));       // 创建Android虚拟机
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/lession4/libmfw.so"), true);     // 加载so到虚拟内存
        module = dm.getModule();     // 获取本SO模块的句柄

        vm.setJni(this);
        vm.setVerbose(true);
        dm.callJNI_OnLoad(emulator);        // 调用 JNI_OnLoad

    }

    public String xPreAuthencode() {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());
        list.add(0);        // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        Object custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom);

        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm,"r0ysue")));
        list.add(vm.addLocalObject(new StringObject(vm,"com.mfw.roadbook")));
        Number number =  module.callFunction(emulator,0x2e301,list.toArray());
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println(result);
        return result;
    }


    public static void main(String[] args) throws Exception {
        mfw test = new mfw();
        System.out.println(test.xPreAuthencode());
    }
}
