package com.weibo;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.arm.context.Arm32RegisterContext;
import com.github.unidbg.debugger.BreakPointCallback;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.api.ApplicationInfo;
import com.github.unidbg.linux.android.dvm.api.ClassLoader;
import com.github.unidbg.linux.android.dvm.api.PackageInfo;
import com.github.unidbg.linux.android.dvm.api.Signature;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import unicorn.ArmConst;

import java.io.File;
import java.io.UnsupportedEncodingException;

public class Weibo extends AbstractJni {
    private final AndroidEmulator emulator;
    private final DvmClass WeiboSecurityUtils;
    private final VM vm;

    public Weibo() {
        emulator = AndroidEmulatorBuilder
                .for32Bit()
                .addBackendFactory(new Unicorn2Factory(true))
                .setProcessName("com.weico.international")
                .build();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/weibo/sinaInternational.apk"));
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary("utility", true);
        // patch free
        emulator.attach().addBreakPoint(dm.getModule().findSymbolByName("free").getAddress(), new BreakPointCallback() {
            @Override
            public boolean onHit(Emulator<?> emulator, long address) {
                Arm32RegisterContext registerContext = emulator.getContext();
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_R0, 0);
                emulator.getBackend().reg_write(ArmConst.UC_ARM_REG_PC, registerContext.getLR());
                return true;
            }
        });
        WeiboSecurityUtils = vm.resolveClass("com/sina/weibo/security/WeiboSecurityUtils");
        dm.callJNI_OnLoad(emulator);
    }

    public String callS() {
        DvmObject<?> context = vm.resolveClass("android/app/Application", vm.resolveClass("android/content/ContextWrapper", vm.resolveClass("android/content/Context"))).newObject(null);
        String arg2 = "hello world";
        String arg3 = "123456";
//        String ret =
        return WeiboSecurityUtils.newObject(null).callJniMethodObject(emulator, "calculateS", context, arg2, arg3).getValue().toString();
    }


    public static void main(String[] args) {
        Weibo wb = new Weibo();
        String result = wb.callS();
        System.out.println("call s result:" + result);
    }

    //    @Override
//    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
//        switch (signature){
//            case "android/content/ContextWrapper->getPackageManager()Landroid/content/pm/PackageManager;":{
//                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
//            }
//        }
//        return super.callObjectMethod(vm, dvmObject, signature, varArg);
//    }
    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {

        switch (signature) {
            case "android/content/ContextWrapper->getPackageManager()Landroid/content/pm/PackageManager;": {
                return vm.resolveClass("android/content/pm/PackageManager").newObject(null);
            }
        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }

}