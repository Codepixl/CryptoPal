package zyzxdev.cryptopal.util

import java.util.*

/**
 * Created by aaron on 6/28/2017.
 */
@FunctionalInterface
interface TaskCompletedCallback{
	fun taskCompleted(data: Any?)
}