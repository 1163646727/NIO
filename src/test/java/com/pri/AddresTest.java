package com.pri;

import org.junit.jupiter.api.Test;
import org.openjdk.jol.vm.VM;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import sun.misc.Unsafe;
import java.util.Collections;
import java.lang.reflect.Field;

/**
 * <BR>
 * author: ChenQi <BR>
 * createDate: 2021/10/12 <BR>
 */
@SpringBootTest
public class AddresTest {

    public static String getAddresses(Object... objects)
    {
        StringBuffer sb = new StringBuffer();
        sb.append("0x");
        // sun.arch.data.model=32 // 32 bit JVM
        // sun.arch.data.model=64 // 64 bit JVM
        boolean is64bit = Integer.parseInt(System.getProperty("sun.arch.data.model")) == 32 ? false : true;
        Unsafe unsafe = getUnsafe();
        long last = 0;
        int offset = unsafe.arrayBaseOffset(objects.getClass());
        int scale = unsafe.arrayIndexScale(objects.getClass());
        switch (scale)
        {
            case 4:
                long factor = is64bit ? 8 : 1;
                final long i1 = (unsafe.getInt(objects, offset) & 0xFFFFFFFFL) * factor;
                // 输出指针地址
                sb.append(Long.toHexString(i1));
                last = i1;
                for (int i = 1; i < objects.length; i++)
                {
                    final long i2 = (unsafe.getInt(objects, offset + i * 4) & 0xFFFFFFFFL) * factor;
                    if (i2 > last)
                        sb.append(", +" + Long.toHexString(i2 - last));
                    else
                        sb.append(", -" + Long.toHexString(last - i2));
                    last = i2;
                }
                break;
            case 8:
                throw new AssertionError("Not supported");
        }
        return sb.toString();
    }

    private static Unsafe getUnsafe()
    {
        try
        {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        }
        catch (Exception e)
        {
            throw new AssertionError(e);
        }
    }



    @Test
    public void test(){
        String a = new String("123"); String b = new String("123");
        System.out.println(getAddresses(a));
        System.out.println(getAddresses(b));
        System.out.println("-----------------------------");
        System.out.println(Long.toHexString(VM.current().addressOf(a)));
        System.out.println(Long.toHexString(VM.current().addressOf(b)));
    }
}
