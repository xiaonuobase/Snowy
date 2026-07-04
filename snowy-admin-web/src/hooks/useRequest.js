import { ref } from 'vue';

export function useRequest(name, service) {
  // 参数校验（保持不变）
  if (typeof name !== 'string' || !name.trim()) {
    throw new Error('useRequest: first argument "name" must be a non-empty string');
  }
  if (typeof service !== 'function') {
    throw new Error('useRequest: second argument "service" must be a function');
  }

  const data = ref(null);
  const error = ref(null);
  const loading = ref(false);
  const capName = name.charAt(0).toUpperCase() + name.slice(1);

  // ✅ 新增：纯 Promise 方法（不管理任何状态）
  const runAsync = (...args) => {
    return service(...args); // 直接返回原始 Promise
  };

  // ✅ 优化：基于 runAsync 实现 run（避免逻辑重复）
  const run = async (...args) => {
    loading.value = true;
    error.value = null;
    try {
      const result = await runAsync(...args); // ← 复用 runAsync
      data.value = result;
      return result;
    } catch (err) {
      error.value = err;
      throw err;
    } finally {
      loading.value = false;
    }
  };

  return {
    [`${name}Data`]: data,
    [`${name}Error`]: error,
    [`${name}Loading`]: loading,
    [`fetch${capName}`]: run,          // 自动管理状态
    [`fetch${capName}Async`]: runAsync // 纯 Promise 模式
  };
}

/*

✅ 测试用例 1：基础成功请求（run 模式）
const mockService = () => Promise.resolve({ id: 1, name: "Test User" });

// 初始化 hook
const { fetchUser, userData, userLoading, userError } = 
  useRequest('user', mockService);

// 触发请求
fetchUser();

// 验证结果
console.log(userLoading.value); // true → false (自动切换)
console.log(userData.value);    // { id: 1, name: "Test User" }
console.log(userError.value);   // null


✅ 测试用例 2：基础成功请求（runAsync 模式）
const { fetchUserAsync, userData, userLoading } = 
  useRequest('user', mockService);

// 仅执行请求，不修改内部状态
fetchUserAsync().then(result => {
  console.log("Raw result:", result); // { id: 1, name: "Test User" }
  
  // 关键验证：内部状态未被修改
  console.log(userLoading.value); // false (始终为 false)
  console.log(userData.value);    // null (始终为 null)
});


 */