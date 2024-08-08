// 获取app.js 的哈希值
const getAppHash = (scripts) => {
	let localVersion = ''
	for (let i = 0; i < scripts.length; i++) {
		let src = scripts[i].getAttribute('src')
		if (src && src.indexOf('main.') !== -1) {
			// 返回时间戳
			localVersion = src.split('t=')[1] || ''
		}
	}
	return localVersion
}

// 获取本地的app.js版本号
export const getLocalHash = () => {
	return getAppHash(document.getElementsByTagName('script'))
}

// 获取线上的app.js版本号
export const checkHash = () => {
	return new Promise((resolve, reject) => {
		// 加上时间戳，防止缓存
		fetch('/?t=' + Date.now())
			.then(async (res) => {
				let html = await res.text() //转成字符串判断
				let doc = new DOMParser().parseFromString(html, 'text/html')
				let newVersion = getAppHash(doc.getElementsByTagName('script'))
				resolve(newVersion)
			})
			.catch((err) => {
				console.log('获取版本号失败', err)
				reject(err)
			})
	})
}
