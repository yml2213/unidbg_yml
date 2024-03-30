package com.puji;

import com.github.unidbg.linux.android.dvm.AbstractJni;
import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.memory.Memory;
import com.right.zuiyou;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class puji extends AbstractJni {
    private final AndroidEmulator emulator;
    private final VM vm;
    private final Module module;

    puji() {
        emulator = AndroidEmulatorBuilder.for32Bit().setProcessName("com.kwai.thanos").build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory(); // 模拟器的内存操作接口
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android\\src\\test\\java\\com\\puji\\puji.apk")); // 创建Android虚拟机
        DalvikModule dm = vm.loadLibrary(new File("unidbg-android\\src\\test\\java\\com\\puji\\libcore.so"), true); // 加载so到虚拟内存
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

    private String getClock() {
        // 准备入参
        List<Object> list = new ArrayList<>(10);
        list.add(vm.getJNIEnv()); // 第一个参数是env
        list.add(0); // 第二个参数，实例方法是jobject，静态方法是jclass，直接填0，一般用不到。
        // 第三个参数  context
        DvmObject<?> context = vm.resolveClass("android/content/Context").newObject(null);      // context
        list.add(vm.addLocalObject(context));

        // 第四个参数  byteArray 创建 byte 数组并传入数据
        String data = "abi=arm32androidApiLevel=30android_os=0app=0apptype=29appver=5.5.0.291authorId=3525143976boardPlatform=konabottom_navigation=truebrowseType=3c=ANDROID_BAIDU_JSWH_SSYQ_CPC_PUJI_LAXIN,1canLive=falsecdid_tag=0client_key=8d219c8dcold_launch_time_ms=1711729728847country_code=cncs=falsedarkMode=falseddpi=440device_abi=arm64did=ANDROID_93cb288f321199cfdid_gt=1711599804825did_tag=1earphoneMode=1egid=DFPB586E929AB7CFED30773FDF481851D5EC990D71C889D81EF7235E5601F403enableCheckFilter=trueenableDynamicIcon=trueenablePlcEntry=trueftt=grant_browse_type=AUTHORIZEDhotfix_ver=is_background=0isp=CUCCiuid=kcv=1464keyconfig_state=1kpf=ANDROID_PHONEkpn=THANOSkuaishou.api_st=Cg9rdWFpc2hvdS5hcGkuc3QSoAENCiKm9VfCoXRsMvrSDYf4GEkdqNghtlx9_huJcamUA-QSi3JGvu77ZZWg8OB7GhPEK9D9Zw4OfuY8gBq-nZN6ORVQH7KTRu2dAtAyiszudyMwMYaz_1ya_8Ofmd0FI83N7Z4QXwE0JqIAnLT4UdB_U2wUkgvVdz0e7kw38ArnijVyt-hxhMeepQxfxhbQfMOoFRUMDIbB_7X1fO_wVb7kGhJThZNu-rRPH4Mw3KnOATqUKJgiIEhwM07eJFWQdVHPFnTy5b39yVDaMAo-34Q8qo51SAiJKAUwAQlanguage=zh-cnmax_memory=256mod=Xiaomi(M2102J2SC)nbh=44needPhotoCount=truenet=WIFInewOc=ANDROID_BAIDU_JSWH_SSYQ_CPC_PUJI_LAXIN,1oDid=ANDROID_93cb288f321199cfoc=ANDROID_BAIDU_JSWH_SSYQ_CPC_PUJI_LAXIN,1os=androidphotoId=5229805186189161916rdid=ANDROID_5255325a00e2c79fsbh=90serverExpTag=feed_photo|5229805186189161916|3525143976|1_i/2006979282388968945_gnbdiscover1001sh=2340slh=0socName=Qualcomm Snapdragon 8250sw=1080sys=ANDROID_11thermal=10000totalMemory=11598uQaTag=ud=55077737userRecoBit=0ver=5.5videoModelCrowdTag=";
        ByteArray plainText = new ByteArray(vm, data.getBytes(StandardCharsets.UTF_8));
        list.add(vm.addLocalObject(plainText));
        // 第五个参数 int 30
        Integer intObject = 30; // 将 int 转换为 Integer 对象
        list.add(30);

        // 参数列表转换为数组
        Object[] argsArray = list.toArray();
        Number[] numbers = new Number[]{module.callFunction(emulator, 0x33e9, argsArray)};
        String result = vm.getObject(numbers[0].intValue()).getValue().toString();

        return result;
    }

    ;

    public static void main(String[] args) throws Exception {
        puji test = new puji();
//        test.native_init();4
        System.out.println(test.getClock());
    }
}

