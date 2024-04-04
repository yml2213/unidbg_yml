package com.puji;

import com.github.unidbg.Emulator;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.AssetManager;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.sun.jna.Pointer;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Data
public class Sig3 extends AbstractJni implements IOResolver {


    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;
    public DvmClass cNative;

    Sig3() {

        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.kwai.thanos").build();   // 创建模拟器实例
        final Memory memory = emulator.getMemory();     // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23));     // 设置系统类解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/puji/puji.apk"));       // 创建Android虚拟机

        new AndroidModule(emulator, vm).register(memory);

        vm.loadLibrary(new File("unidbg-android/src/test/java/com/puji/libc++_shared.so"), false);
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android/src/test/java/com/puji/libkwsgmain.so"), true);     // 加载so到虚拟内存
        module = dm.getModule();     // 获取本SO模块的句柄

        vm.setJni(this);
        vm.setVerbose(true);
        dm.callJNI_OnLoad(emulator);        // 调用 JNI_OnLoad

        cNative = vm.resolveClass("com/kuaishou/android/security/internal/dispatch/JNICLibrary");

    }

    public String xPreAuthencode() {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());
        list.add(0);        // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        Object custom = null;
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(custom);
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm, "r0ysue")));
        list.add(vm.addLocalObject(new StringObject(vm, "com.mfw.roadbook")));
        Number number = module.callFunction(emulator, 0x2e301, list.toArray());
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println(result);
        return result;
    }

    public String getSig3() {
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());
//        list.add(0);        // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0，一般用不到。
        DvmObject<?> cnative = cNative.newObject(null);
        list.add(cnative.hashCode()); // 第二个参数，实例方法是jobject，静态方法是jclazz，直接填0这里是不行的，此样本参数2被使用了


//        list.add(10418);
        list.add(vm.addLocalObject(DvmInteger.valueOf(vm, 10418)));
        ArrayObject arrayObject = ArrayObject.newStringArray(vm, "/rest/puji/photo/like6c5a00ae8d879cfeb8120740fbd2f463");
        System.out.println(arrayObject);
        list.add(vm.addLocalObject(arrayObject));
        list.add(vm.addLocalObject(new StringObject(vm, "d7b7d042-d4f2-4012-be60-d97ff2429c17")));
//        list.add(vm.addLocalObject(new StringObject(vm, "-1")));
        list.add(vm.addLocalObject(DvmInteger.valueOf(vm, -1)));

        list.add(vm.addLocalObject(DvmBoolean.valueOf(vm, false)));

        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);      // context
        list.add(vm.addLocalObject(context));
        list.add(vm.addLocalObject(new StringObject(vm, "null")));
        list.add(vm.addLocalObject(DvmBoolean.valueOf(vm, false)));
        list.add(vm.addLocalObject(new StringObject(vm, "")));


//        System.out.println(Arrays.toString(list.toArray()));

        Number number = module.callFunction(emulator, 0x443c1, list.toArray());
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i).toString());
        }
        String result = vm.getObject(number.intValue()).getValue().toString();
        System.out.println(result);
        return result;
    }


    @Override
    public void callStaticVoidMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {

        switch (signature) {
            case "com/kuaishou/android/security/internal/common/ExceptionProxy->nativeReport(ILjava/lang/String;)V":
                return;
        }

        throw new UnsupportedOperationException(signature);

    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        System.out.println(signature);
        System.out.println("============");
        switch (signature) {
            case "com/yxcorp/gifshow/App->getPackageCodePath()Ljava/lang/String;": {
                return new StringObject(vm, "/data/app/com.smile.gifmaker-q14Fo0PSb77vTIOM1-iEqQ==/base.apk");
            }
            case "com/yxcorp/gifshow/App->getAssets()Landroid/content/res/AssetManager;": {
//                return new Long(vm, "3817726272");
                return new AssetManager(vm, signature);
            }
            case "com/yxcorp/gifshow/App->getPackageName()Ljava/lang/String;": {
                return new StringObject(vm, "com.smile.gifmaker");
            }
            case "com/yxcorp/gifshow/App->getPackageManager()Landroid/content/pm/PackageManager;": {
                DvmClass clazz = vm.resolveClass("android/content/pm/PackageManager");
                return clazz.newObject(signature);
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/Boolean->booleanValue()Z":
                DvmBoolean dvmBoolean = (DvmBoolean) dvmObject;
                return dvmBoolean.getValue();
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }


    @Override
    public FileResult resolve(Emulator emulator, String pathname, int oflags) {
        System.out.println("fuck:" + pathname);
        return null;
    }

    public String readStdString(Pointer strptr) {
        Boolean isTiny = (strptr.getByte(0) & 1) == 0;
        if (isTiny) {
            return strptr.getString(1);
        }
        return strptr.getPointer(emulator.getPointerSize() * 2L).getString(0);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "com/kuaishou/android/security/internal/common/ExceptionProxy->getProcessName(Landroid/content/Context;)Ljava/lang/String;":
                return new StringObject(vm, "com.smile.gifmaker");
            case "com/meituan/android/common/mtguard/NBridge->getSecName()Ljava/lang/String;":
                return new StringObject(vm, "ppd_com.sankuai.meituan.xbt");
            case "com/meituan/android/common/mtguard/NBridge->getAppContext()Landroid/content/Context;":
                return vm.resolveClass("android/content/Context").newObject(null);
            case "com/meituan/android/common/mtguard/NBridge->getMtgVN()Ljava/lang/String;":
                return new StringObject(vm, "4.4.7.3");
            case "com/meituan/android/common/mtguard/NBridge->getDfpId()Ljava/lang/String;":
                return new StringObject(vm, "");
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }



    public void init_native() throws FileNotFoundException {

        System.out.println("result:" );
    }

    public static void main(String[] args) throws Exception {
        Sig3 sig3 = new Sig3();
//        sig3.init_native();
        String result = sig3.getSig3();
        log.info("================>{}", result);
        System.out.println(result);
    }
}
