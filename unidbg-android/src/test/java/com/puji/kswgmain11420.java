package com.puji;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.EmulatorBuilder;
import com.github.unidbg.Module;
import com.github.unidbg.file.FileResult;
import com.github.unidbg.file.IOResolver;
import com.github.unidbg.file.linux.AndroidFileIO;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.AssetManager;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.wrapper.DvmBoolean;
import com.github.unidbg.linux.android.dvm.wrapper.DvmInteger;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.spi.SyscallHandler;
import com.github.unidbg.virtualmodule.android.AndroidModule;
import com.github.unidbg.virtualmodule.android.JniGraphics;
import com.sun.jna.Pointer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class kswgmain11420 extends AbstractJni implements IOResolver {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    kswgmain11420() throws FileNotFoundException {
        // 创建模拟器实例，要模拟32位或者64位，在这里区分
        EmulatorBuilder<AndroidEmulator> builder = AndroidEmulatorBuilder.for32Bit().setProcessName("com.kwai.thanos");
        emulator = builder.build();
        emulator.getSyscallHandler().setEnableThreadDispatcher(true);
        // 模拟器的内存操作接口
        final Memory memory = emulator.getMemory();
        // 设置系统类库解析
        memory.setLibraryResolver(new AndroidResolver(23));

        // 创建Android虚拟机
        // vm = emulator.createDalvikVM();
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/java/com/puji/puji.apk"));
        // 设置是否打印Jni调用细节
        vm.setVerbose(true);
        new JniGraphics(emulator, vm).register(memory);
        new AndroidModule(emulator, vm).register(memory);
        vm.setJni(this);
        SyscallHandler<AndroidFileIO> handler = emulator.getSyscallHandler();
        handler.addIOResolver(this);

        // 加载libttEncrypt.so到unicorn虚拟内存，加载成功以后会默认调用init_array等函数
        DalvikModule dm = vm.loadLibrary(new File("E:\\codeData\\unidbg\\unidbg-android\\src\\test\\java\\com\\puji\\libkwsgmain.so"), true);
        // 加载好的libttEncrypt.so对应为一个模块
        module = dm.getModule();

        // trace code
//        String traceFile = "unidbg-android\\src\\test\\java\\com\\smile\\gifmaker3\\sig3_jniOnload.trc";
//        GlobalData.ignoreModuleList.add("libc.so");
//        GlobalData.ignoreModuleList.add("libhookzz.so");
//        GlobalData.ignoreModuleList.add("libc++_shared.so");
//        emulator.traceCode(module.base, module.base+module.size).setRedirect(new PrintStream(new FileOutputStream(traceFile), true));

        dm.callJNI_OnLoad(emulator);
    }


    public void init_native() throws FileNotFoundException {
        // trace code
//        String traceFile = "unidbg-android\\src\\test\\java\\com\\smile\\gifmaker3\\sig3_init_native.trc";
//        GlobalData.ignoreModuleList.add("libc.so");
//        GlobalData.ignoreModuleList.add("libhookzz.so");
//        GlobalData.ignoreModuleList.add("libc++_shared.so");
//        emulator.traceCode(module.base, module.base+module.size).setRedirect(new PrintStream(new FileOutputStream(traceFile), true));


//        objArr[0] = [Ljava.lang.String;@8ac3204
//        objArr[1] = d7b7d042-d4f2-4012-be60-d97ff2429c17
//        objArr[2] = -1
//        objArr[3] = false
//        objArr[4] = com.yxcorp.gifshow.App@8ed4c34
//        objArr[5] = null
//        objArr[6] = false
//        objArr[7] =


        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());  // 第一个参数是env
        DvmObject<?> thiz = vm.resolveClass("com/kuaishou/android/security/internal/dispatch/JNICLibrary").newObject(null);
        list.add(vm.addLocalObject(thiz));  // 第二个参数，实例方法是jobject，静态方法是jclass，直接填0，一般用不到。
        DvmObject<?> context = vm.resolveClass("com/yxcorp/gifshow/App").newObject(null);  // context
        vm.addLocalObject(context);
        list.add(10418);  //参数1
        StringObject appkey = new StringObject(vm, "d7b7d042-d4f2-4012-be60-d97ff2429c17");  // SO文件有校验
        vm.addLocalObject(appkey);
        DvmInteger intergetobj = DvmInteger.valueOf(vm, 0);
        vm.addLocalObject(intergetobj);
//        ArrayObject arrayObject =  new ArrayObject(1);
//        StringObject appkey =  new StringObject(vm, "d7b7d042-d4f2-4012-be60-d97ff2429c17" );
//        vm.addLocalObject(appkey);
//        DvmInteger intergetobj = DvmInteger.valueOf(vm, - 1 );
        vm.addLocalObject(intergetobj);
        DvmBoolean boolobj = DvmBoolean.valueOf(vm, false);
        vm.addLocalObject(boolobj);
        StringObject appkey2 = new StringObject(vm, "7e46b28a-8c93-4940-8238-4c60e64e3c81");
        vm.addLocalObject(appkey2);
//        list.add(vm.addLocalObject(new ArrayObject("2321", appkey, -1, intergetobj, context, intergetobj, intergetobj)));
        // 直接通过地址调用
        Number numbers = module.callFunction(emulator, 0x443c1, list.toArray());
        System.out.println("numbers:" + numbers);
        DvmObject<?> object = vm.getObject(numbers.intValue());
        String result = (String) object.getValue();
        System.out.println("result:" + result);
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

    public String get_NS_sig3() throws FileNotFoundException {
        // trace code
//        String traceFile = "unidbg-android\\src\\test\\java\\com\\smile\\gifmaker3\\sig3_new.trc";
//        GlobalData.ignoreModuleList.add("libc.so");
//        GlobalData.ignoreModuleList.add("libhookzz.so");
//        GlobalData.ignoreModuleList.add("libc++_shared.so");
//        emulator.traceCode(module.base, module.base+module.size).setRedirect(new PrintStream(new FileOutputStream(traceFile), true));

        System.out.println("_NS_sig3 start");
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv());  // 第一个参数是env
        DvmObject<?> thiz = vm.resolveClass("com/kuaishou/android/security/internal/dispatch/JNICLibrary").newObject(null);
        list.add(vm.addLocalObject(thiz));  // 第二个参数，实例方法是jobject，静态方法是jclass，直接填0，一般用不到。
        DvmObject<?> context = vm.resolveClass("com/yxcorp/gifshow/App").newObject(null);  // context
        vm.addLocalObject(context);
        list.add(10418);  //参数1
        StringObject urlObj = new StringObject(vm, "/rest/app/eshop/ks/live/item/byGuest6bcab0543b7433b6d0771892528ef686");
        vm.addLocalObject(urlObj);
        ArrayObject arrayObject = new ArrayObject(urlObj);
        StringObject appkey = new StringObject(vm, "d7b7d042-d4f2-4012-be60-d97ff2429c17");
        vm.addLocalObject(appkey);
        DvmInteger intergetobj = DvmInteger.valueOf(vm, -1);
        vm.addLocalObject(intergetobj);
        DvmBoolean boolobj = DvmBoolean.valueOf(vm, false);
        vm.addLocalObject(boolobj);
        StringObject appkey2 = new StringObject(vm, "7e46b28a-8c93-4940-8238-4c60e64e3c81");
        vm.addLocalObject(appkey2);
        list.add(vm.addLocalObject(new ArrayObject(arrayObject, appkey, intergetobj, boolobj, context, null, boolobj, appkey2)));
        // 直接通过地址调用
        Number numbers = module.callFunction(emulator, 0x41680, list.toArray());
        System.out.println("numbers:" + numbers);
        DvmObject<?> object = vm.getObject(numbers.intValue());
        String result = (String) object.getValue();
        System.out.println("result:" + result);
        return result;
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

    public static void main(String[] args) throws FileNotFoundException {
        kswgmain11420 kk = new kswgmain11420();
        kk.init_native();
//        kk.get_NS_sig3();
    }
}

