// File: src/main/java/org/example/backend/utils/OperatorContext.java
package org.example.backend.utils;

/** 用 ThreadLocal 暂存“当前操作者 ID”，Filter 写入、DAO 读取 */
public final class OperatorContext {
    private static final ThreadLocal<Integer> TL = new ThreadLocal<>();
    private OperatorContext() {}
    public static void set(Integer id) { TL.set(id); }
    public static Integer get()        { return TL.get(); }
    public static void clear()         { TL.remove(); }
}
