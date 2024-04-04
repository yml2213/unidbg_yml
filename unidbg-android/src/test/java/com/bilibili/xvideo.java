package com.bilibili;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.arm.backend.Unicorn2Factory;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.DvmClass;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;

import java.io.File;

public class xvideo extends AbstractJni {
    private final AndroidEmulator emulator;
    private final DvmClass NativeApi;
    private final VM vm;

    public xvideo() {
        emulator = AndroidEmulatorBuilder
                .for64Bit()
                .addBackendFactory(new Unicorn2Factory(true))
                // 设置根目录
                .setRootDir(new File("target/rootfs"))
                .setProcessName("com.sina.oasis")
                .build();
        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23));
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/bilibili/lvzhou.apk"));
        System.out.println(this);
        vm.setJni(this);
        vm.setVerbose(true);
        DalvikModule dm = vm.loadLibrary("oasiscore", true);
        NativeApi = vm.resolveClass("com/weibo/xvideo/NativeApi");
        dm.callJNI_OnLoad(emulator);
    }

    public static void main(String[] args) {
        xvideo xv = new xvideo();
    }
}