export function durationFormat(duration) {
	duration = Math.max(0, parseInt(duration) || 0)

	const hours = Math.floor(duration / 3600)
	const minutes = Math.floor((duration % 3600) / 60)
	const seconds = Math.floor(duration % 60)

	// 格式化数字为两位数
	const formatNumber = (num) => num.toString().padStart(2, '0')

	// 如果小于60秒，返回 00:xx 格式
	if (duration < 60) {
		return `00:${formatNumber(seconds)}`
	}

	// 如果小于一小时，返回 00:xx:xx 格式
	if (duration < 3600) {
		return `00:${formatNumber(minutes)}:${formatNumber(seconds)}`
	}

	// 大于等于一小时，返回 xx:xx:xx 格式
	return `${formatNumber(hours)}:${formatNumber(minutes)}:${formatNumber(seconds)}`
}

export function isTrue(arg1, arg2) {
	return arg1 == arg2
}
