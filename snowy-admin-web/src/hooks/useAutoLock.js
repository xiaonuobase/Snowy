/**
 *  Copyright [2022] [https://www.xiaonuo.vip]
 *	Snowy采用APACHE LICENSE 2.0开源协议，您在使用过程中，需要注意以下几点：
 *	1.请不要删除和修改根目录下的LICENSE文件。
 *	2.请不要删除和修改Snowy源码头部的版权声明。
 *	3.本项目代码可免费商业使用，商业使用请保留源码和相关描述文件的项目出处，作者声明等。
 *	4.分发源码时候，请注明软件出处 https://www.xiaonuo.vip
 *	5.不可二次分发开源参与同类竞品，如有想法可联系团队xiaonuobase@qq.com商议合作。
 *	6.若您的项目无法满足以上几点，需要更多功能代码，获取Snowy商业授权许可，请在官网购买授权，地址为 https://www.xiaonuo.vip
 */
import { globalStore } from '@/store'
import tool from '@/utils/tool'
import userCenterApi from '@/api/sys/userCenterApi'

// 视为用户活跃的事件，mousemove/scroll 触发频率极高，统一走节流
const ACTIVITY_EVENT_LIST = ['mousedown', 'mousemove', 'keydown', 'touchstart', 'scroll', 'wheel']
// 活跃事件的节流间隔（毫秒），间隔内的重复触发不再重置计时
const ACTIVITY_THROTTLE_INTERVAL = 1000
// 活跃时间写入本地缓存的间隔（毫秒），比上面宽是因为写缓存要读写整个聚合对象，开销大于重置定时器
const SHARED_ACTIVITY_WRITE_INTERVAL = 5000
// tool.data 中 SNOWY_ 前缀键的聚合存储项，用于跨标签页同步锁屏状态
const SNOWY_SETTING_KEY = 'SNOWY_SETTING'
// 最近活跃时间的缓存键，多个标签页共用同一个 token，活跃时间必须共享
const LAST_ACTIVITY_KEY = 'SNOWY_LAST_ACTIVITY'

const setLastActivityAt = (timestamp) => tool.data.set(LAST_ACTIVITY_KEY, timestamp)

/**
 * 读取全部标签页共享的最近活跃时间
 * 无记录（刚登录）或时间戳晚于当前时刻（改过系统时间）时重置为此刻，避免一进页面就被判定为超时
 */
const getLastActivityAt = () => {
	const lastActivityAt = Number(tool.data.get(LAST_ACTIVITY_KEY)) || 0
	const now = Date.now()
	if (lastActivityAt <= 0 || lastActivityAt > now) {
		setLastActivityAt(now)
		return now
	}
	return lastActivityAt
}

/**
 * 锁定屏幕，先本地锁屏保证界面立即遮蔽，再请求服务端锁定会话
 * 服务端锁定后，除解锁与退出登录外的接口都会返回4012
 */
export const doLockScreen = () => {
	globalStore().setIsLocked(true)
	userCenterApi.userLock()
}

/**
 * 无操作自动锁屏，计时时长取登录用户的 autoLockTime（分钟），为0时不开启
 * 已锁屏时停止计时，解锁后自动重新开始计时
 *
 * 空闲判定基于全部标签页共享的活跃时间：同源标签页共用一个 token，服务端锁定作用在该 token 的会话上，
 * 若各标签页各自计时，空闲的那个到点就会把正在使用的那个一起锁掉
 */
export const useAutoLock = () => {
	const store = globalStore()
	let timer = null
	let lastActivityAt = 0
	let lastSharedAt = 0

	const clearTimer = () => {
		if (timer) {
			clearTimeout(timer)
			timer = null
		}
	}

	// 重置倒计时，未登录、未开启自动锁屏、已锁屏这三种情况都不需要计时
	const resetTimer = () => {
		clearTimer()
		if (!tool.data.get('TOKEN') || store.autoLockTime <= 0 || store.isLocked) {
			return
		}
		const restTime = store.autoLockTime * 60 * 1000 - (Date.now() - getLastActivityAt())
		if (restTime <= 0) {
			doLockScreen()
			return
		}
		// 到点后不直接锁屏，而是重新走一遍本方法：期间若其他标签页有过操作，共享活跃时间已被刷新，这里会自动顺延
		timer = setTimeout(resetTimer, restTime)
	}

	const handleActivity = () => {
		const now = Date.now()
		if (now - lastActivityAt < ACTIVITY_THROTTLE_INTERVAL) {
			return
		}
		lastActivityAt = now
		// 共享活跃时间按更宽的间隔写入，最坏情况下比真实活跃时间滞后 SHARED_ACTIVITY_WRITE_INTERVAL，
		// 相对分钟级的锁屏时长可以忽略
		if (now - lastSharedAt >= SHARED_ACTIVITY_WRITE_INTERVAL) {
			lastSharedAt = now
			setLastActivityAt(now)
		}
		resetTimer()
	}

	// 其他标签页锁屏/解锁时同步本标签页，直接赋值以避免再次写回缓存
	const handleStorageChange = (event) => {
		if (event.key !== SNOWY_SETTING_KEY) {
			return
		}
		const locked = !!tool.data.get('SNOWY_IS_LOCKED')
		if (locked !== store.isLocked) {
			store.isLocked = locked
		}
	}

	onMounted(() => {
		ACTIVITY_EVENT_LIST.forEach((eventName) => window.addEventListener(eventName, handleActivity, { passive: true }))
		window.addEventListener('storage', handleStorageChange)
		nextTick(() => resetTimer())
	})

	onUnmounted(() => {
		ACTIVITY_EVENT_LIST.forEach((eventName) => window.removeEventListener(eventName, handleActivity))
		window.removeEventListener('storage', handleStorageChange)
		clearTimer()
	})

	// 解锁瞬间必须把活跃时间刷到此刻，否则拿锁屏前的旧时间去算，解锁后会立刻又锁上
	watch(
		() => store.isLocked,
		(isLocked, preIsLocked) => {
			if (preIsLocked && !isLocked) {
				lastSharedAt = Date.now()
				setLastActivityAt(lastSharedAt)
			}
			resetTimer()
		}
	)

	// 时长配置变化时按新时长重新计时
	watch(() => store.autoLockTime, resetTimer)

	return { resetTimer }
}
