package com.puji;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Emulator;
import com.github.unidbg.arm.backend.Backend;
import com.github.unidbg.arm.backend.DynarmicFactory;
import com.github.unidbg.linux.ARM32SyscallHandler;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.linux.android.dvm.DalvikModule;
import com.github.unidbg.linux.android.dvm.VM;
import com.github.unidbg.memory.Memory;
import com.github.unidbg.memory.SvcMemory;
import unicorn.ArmConst;

import java.io.File;
import java.io.IOException;
import java.util.List;

public abstract class AirApkEmulator extends AbstractJni {
    protected final AndroidEmulator emulator;
    protected final VM vm;

    private static class AirARMSyscallHandler extends ARM32SyscallHandler {
        private boolean printSyscall = false;

        private AirARMSyscallHandler(SvcMemory svcMemory, boolean printSyscall) {
            super(svcMemory);
            this.printSyscall = printSyscall;
        }

        @Override
        protected int fork(Emulator<?> emulator) {
            return emulator.getPid();
        }

        @Override
        public void hook(Backend backend, int intno, int swi, Object user) {
            super.hook(backend, intno, swi, user);
            if (printSyscall) {
                System.out.printf("[syscall] NR=%1$s\n", backend.reg_read(ArmConst.UC_ARM_REG_R7).intValue());
            }
        }
    }

    protected abstract List<String> libraries();

    protected abstract String processName();

    protected abstract String apkPath();

    protected void beforeLibrariesLoaded(Memory memory) {
    }

    protected void beforeJniOnLoadCalled(String library) {
    }

    protected boolean callJniOnLoadOnStart(String library) {
        return true;
    }

    protected boolean printSyscall() {
        return false;
    }

    protected boolean verbose() {
        return true;
    }

    public AirApkEmulator() {
        emulator = AndroidEmulatorBuilder.for32Bit()
                .setProcessName(processName())
                .setRootDir(new File("target/rootfs"))
                // .addBackendFactory(new DynarmicFactory(false))
                .build();

        Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(19));

        // Load the apk so that the apk signature can be mocked.
        String apkPath = apkPath();
        vm = emulator.createDalvikVM(apkPath == null ? null : new File(apkPath));
        vm.setJni(this);
        vm.setVerbose(verbose());

        beforeLibrariesLoaded(memory);

        for (String lib : libraries()) {
            DalvikModule dm = vm.loadLibrary(
                    new File(lib),
                    true
            );

            if (callJniOnLoadOnStart(lib)) {
                beforeJniOnLoadCalled(lib);
                dm.callJNI_OnLoad(emulator);
            }
        }
    }

    protected void close() {
        try {
            emulator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}