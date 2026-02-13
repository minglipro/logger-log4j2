package com.mingliqiye.logger

import com.mingliqiye.logger.Loggers.getLogger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * 日志记录器管理对象，提供统一的日志记录器获取功能
 */
object Loggers {

    /**
     * 缓存日志记录器的并发哈希映射，用于避免重复创建相同的日志记录器
     * 初始容量为64，默认负载因子为0.75
     */
    private val loggers: ConcurrentHashMap<String, Logger> = ConcurrentHashMap(64,0.75f)

    /**
     * 根据泛型类型获取对应的日志记录器
     * @param T 泛型类型参数，表示需要获取日志记录器的类类型
     * @return 对应类型的日志记录器实例
     */
    inline fun <reified T> getLogger(): Logger = getLogger(T::class.java)

    /**
     * 根据Class对象获取对应的日志记录器
     * @param T 类型参数，表示需要获取日志记录器的类类型
     * @param clazz 需要获取日志记录器的类的Class对象
     * @return 对应类型的日志记录器实例
     */

    @JvmStatic
    fun <T> getLogger(clazz: Class<T>): Logger =
         loggers[clazz.name] ?: LoggerFactory.getLogger(clazz).also { loggers[clazz.name] = it }

    /**
     * 根据名称获取对应的日志记录器
     * @param name 日志记录器的名称
     * @return 指定名称的日志记录器实例
     */

    @JvmStatic
    fun getLogger(name: String): Logger = loggers[name] ?: LoggerFactory.getLogger(name).also { loggers[name] = it }

    /**
     * 获取调用栈跟踪日志记录器，通过分析当前调用栈来确定日志记录器名称
     * 注意了 该方法性能损耗挺大的
     * @return 基于调用栈信息的相应类的日志记录器实例
     */

    @JvmStatic
    fun getTraceLogger(): Logger = Throwable().stackTrace.let {
        if (it.size > 1) {
            getLogger(it.first().className)
        } else {
            getLogger("Unknown")
        }
    }

    /**
     * 扩展函数，用于获取与给定类关联的日志记录器实例。
     *
     * 此函数通过调用 [getLogger] 方法并传入当前类的 [Class] 对象来获取日志记录器。
     *
     * @return 返回与当前类关联的 [Logger] 实例。
     */
    @JvmName("__Any_getLogger")
    fun Class<out Any>.getLogger() : Logger = getLogger(this)

    /**
     * 扩展函数，用于获取与给定 Kotlin 类关联的日志记录器实例。
     *
     * 此函数首先将 [KClass] 转换为对应的 Java [Class] 对象，
     * 然后调用 [Class.getLogger] 方法获取日志记录器。
     *
     * @return 返回与当前 Kotlin 类关联的 [Logger] 实例。
     */
    @JvmName("__Any_getLogger")
    fun KClass<out Any>.getLogger() : Logger = this.java.getLogger()
}
